package com.github.dinsaw.valuestore.verticle;

import com.github.dinsaw.valuestore.Config;
import com.github.dinsaw.valuestore.controller.MutualFundController;
import com.github.dinsaw.valuestore.controller.NavController;
import com.github.dinsaw.valuestore.service.InfoService;
import com.github.dinsaw.valuestore.util.RestPathConstants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.IndexOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

import static com.github.dinsaw.valuestore.util.AppConstants.MUTUAL_FUNDS;
import static com.github.dinsaw.valuestore.util.AppConstants.NET_ASSET_VALUES;
import static com.github.dinsaw.valuestore.util.AsyncUtils.logIfFailed;
import static com.github.dinsaw.valuestore.util.MongoConstants.ASC;
import static com.github.dinsaw.valuestore.util.MongoConstants.DESC;
import static com.github.dinsaw.valuestore.util.RestPathConstants.GET_MF_NAV_PATH;
import static com.github.dinsaw.valuestore.util.WebConstants.*;

/**
 * Created by dinsaw on 2/12/18.
 */
@Slf4j
public class Server extends AbstractVerticle {

    private InfoService infoService = new InfoService();
    private MongoClient mongoClient;

    @Override
    public void start() throws Exception {
        log.debug("Starting REST Backend");

        initMongoClient();
        createDbIndexes();

        Router router = Router.router(vertx);
        defineRoutes(router);

        vertx.createHttpServer().requestHandler(router).listen(config().getInteger("http.port"));
    }

    private void initMongoClient() {
        mongoClient = Config.mongoClient(vertx, config());
    }

    private void createDbIndexes() {
        // Create MF index
        JsonObject mfIndexCriteria = new JsonObject()
                .put("schemeCode", ASC);
        IndexOptions mfIndexOptions = new IndexOptions().unique(true);
        mongoClient.createIndexWithOptions(MUTUAL_FUNDS, mfIndexCriteria, mfIndexOptions,
                ar -> logIfFailed(ar, log));

        // Create Nav index
        JsonObject navIndexCriteria = new JsonObject()
                                        .put("schemeCode", ASC)
                                        .put("date", DESC);
        IndexOptions navIndexOptions = new IndexOptions().unique(true);
        mongoClient.createIndexWithOptions(NET_ASSET_VALUES, navIndexCriteria, navIndexOptions,
                ar -> logIfFailed(ar, log));
    }


    private void defineRoutes(Router router) {
        log.debug("Defining web routes");

        router.get("/hello")
                .handler(rc -> rc.response()
                            .putHeader(CONTENT_TYPE, TEXT_HTML)
                            .end("Hello, Welcome to ValueStore :)"));
        router.get("/")
                .handler(rc -> rc.response()
                            .putHeader(CONTENT_TYPE, APPLICATION_JSON_UTF_8)
                                .end(Json.encodePrettily(infoService.greet())));

        MutualFundController mutualFundController = new MutualFundController(mongoClient);
        router.get(RestPathConstants.GET_MUTUAL_FUND_PATH).handler(mutualFundController::getBySchemeCode);
        router.get(RestPathConstants.MUTUAL_FUNDS_BASE).handler(mutualFundController::getAll);

        NavController navController = new NavController(mongoClient);
        router.get(GET_MF_NAV_PATH).handler(navController::getBySchemeCode);
    }
}
