package com.github.dinsaw.valuestore.service;

import com.github.dinsaw.navparser.dto.MutualFund;
import com.github.dinsaw.navparser.india.AmfiIndiaNavParser;
import com.github.dinsaw.valuestore.collections.ArrayBatchPusher;
import com.github.dinsaw.valuestore.collections.BatchPusher;
import com.github.dinsaw.valuestore.util.AppConstants;
import com.github.dinsaw.valuestore.util.AsyncUtils;
import com.github.dinsaw.valuestore.util.DateUtils;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.*;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
                        List<MutualFund> mutualFunds = new ArrayList<>(500);
                        if (ar.succeeded()) {
                            HttpResponse<Buffer> response = ar.result();
                            String text = response.bodyAsString();
                            String[] lines = text.split("\n");
                            BatchPusher<MutualFund> pusher = new ArrayBatchPusher<>(1000, this::saveMutualFunds);
                            for (String line: lines) {
                                if (navParser.isHeader(line) || navParser.shouldSkip(line))  {
                                    continue;
                                }

                                navParser.parseLine(line.trim())
                                    .ifPresent(m -> pusher.add(m));
                            }
                            pusher.flush();
                        } else {
                            log.error("Failed to access refresh URL", ar.cause());
                        }
                    });
    }

    private void saveMutualFunds(List<MutualFund> mutualFundList) {
        List<BulkOperation> bulkOperations = mutualFundList.stream().map(m -> {
            JsonObject checkQuery = new JsonObject().put(AppConstants.SCHEME_CODE, m.getSchemeCode());
            return BulkOperation.createUpdate(checkQuery, updateObject(mutualFundJson(m)), true, false);
        }).collect(Collectors.toList());

        BulkWriteOptions bulkWriteOptions = new BulkWriteOptions().setWriteOption(WriteOption.ACKNOWLEDGED);
        mongoClient.bulkWriteWithOptions("mutualFunds", bulkOperations, bulkWriteOptions, mr -> {
            log.debug("Result after saving {}", mr.result());
            if (mr.failed()) {
                log.error("Saving failed.", mr.cause());
            } else {
                saveNavs(mutualFundList);
            }
        });
    }


    private void saveMutualFund(MutualFund m) {
        JsonObject checkQuery = new JsonObject().put(AppConstants.SCHEME_CODE, m.getSchemeCode());
        UpdateOptions updateOptions = new UpdateOptions().setUpsert(true);

        mongoClient.updateCollectionWithOptions("mutualFunds", checkQuery,
                            updateObject(mutualFundJson(m)),
                            updateOptions, mr -> {
                                log.debug("Result after saving {}", mr.result().toJson());
                                if (mr.failed()) {
                                    log.error("Saving failed.", mr.cause());
                                } else {
                                    saveNav(m);
                                }
                            });
    }

    private void saveNavs(List<MutualFund> mutualFundList) {
        List<BulkOperation> bulkOperations = mutualFundList.stream().map(m -> {
            JsonObject navJsonObject = toNavJson(m);
            JsonObject query = new JsonObject()
                    .put(AppConstants.SCHEME_CODE, m.getSchemeCode())
                    .put("date", DateUtils.mongoDate(m.getNavDate()));
            return BulkOperation.createUpdate(query, updateObject(navJsonObject),
                                        true, false);

        }).collect(Collectors.toList());

        BulkWriteOptions bulkWriteOptions = new BulkWriteOptions().setWriteOption(WriteOption.ACKNOWLEDGED);
        mongoClient.bulkWriteWithOptions(NET_ASSET_VALUES, bulkOperations, bulkWriteOptions,
                ar -> AsyncUtils.logIfFailed(ar, log));
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


