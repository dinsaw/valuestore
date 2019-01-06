package com.github.dinsaw.valuestore.service;

import com.github.dinsaw.navparser.dto.MutualFund;
import com.github.dinsaw.navparser.india.AmfiIndiaNavParser;
import com.github.dinsaw.valuestore.util.AppConstants;
import com.github.dinsaw.valuestore.util.AsyncUtils;
import com.github.dinsaw.valuestore.util.DateUtils;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.UpdateOptions;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.github.dinsaw.valuestore.util.AppConstants.NET_ASSET_VALUES;
import static com.github.dinsaw.valuestore.util.MongoUtils.updateObject;

/**
 * Created by dinsaw on 8/12/18.
 */
@AllArgsConstructor
@Slf4j
public class RefreshService implements Handler<Long> {
    private MongoClient mongoClient;
    private WebClient webClient;

    @Override
    public void handle(Long timerId) {
        log.debug("Starting refresh task for timerId {}", timerId);

        AmfiIndiaNavParser navParser = new AmfiIndiaNavParser();

        webClient.get(80, "www.amfiindia.com", "/spages/NAVAll.txt")
                    .ssl(false)
                    .send(ar -> {
                        if (ar.succeeded()) {
                            HttpResponse<Buffer> response = ar.result();
                            String text = response.bodyAsString();
                            String[] lines = text.split("\n");
                            for (String line: lines) {
                                if (navParser.isHeader(line) || navParser.shouldSkip(line))  {
                                    continue;
                                }
                                log.debug("Processing line {}", line);

                                navParser.parseLine(line.trim())
                                    .ifPresent(m -> saveMutualFund(m));
                            }

                        } else {
                            log.error("Failed to access refresh URL", ar.cause());
                        }
                    });
    }

    private MongoClient saveMutualFund(MutualFund m) {
        JsonObject checkQuery = new JsonObject().put(AppConstants.SCHEME_CODE, m.getSchemeCode());
        UpdateOptions updateOptions = new UpdateOptions().setUpsert(true);

        return mongoClient.updateCollectionWithOptions("mutualFunds", checkQuery,
                            updateObject(mutualFundJson(m)),
                            updateOptions, mr -> {
                                log.debug("Result after saving {}", mr.result());
                                if (mr.failed()) {
                                    log.error("Saving failed.", mr.cause());
                                } else {
                                    saveNav(m);
                                }
                            });
    }



    private void saveNav(MutualFund mutualFund) {
        JsonObject navJsonObject = toNavJson(mutualFund);
        JsonObject query = new JsonObject()
                                .put(AppConstants.SCHEME_CODE, mutualFund.getSchemeCode())
                                .put("date", DateUtils.mongoDate(mutualFund.getNavDate()));
        UpdateOptions updateOptions = new UpdateOptions().setUpsert(true);

        mongoClient.updateCollectionWithOptions(NET_ASSET_VALUES, query, updateObject(navJsonObject),
                updateOptions, ar -> AsyncUtils.logIfFailed(ar, log));
    }

    private JsonObject toNavJson(MutualFund mutualFund) {
        return new JsonObject()
                .put(AppConstants.SCHEME_CODE, mutualFund.getSchemeCode())
                .put("date", DateUtils.mongoDate(mutualFund.getNavDate()))
                .put("value", mutualFund.getNetAssetValue());
    }

    private JsonObject mutualFundJson(MutualFund m) {
        return new JsonObject()
                .put(AppConstants.SCHEME_CODE, m.getSchemeCode())
                .put("schemeName", m.getSchemeName())
                .put("netAssetValue", m.getNetAssetValue())
                .put("isinDivPayout", m.getIsinDivPayout())
                .put("isinDivReinvestment", m.getIsinDivReinvestment())
                .put("navDate", DateUtils.mongoDate(m.getNavDate()));
    }


}


