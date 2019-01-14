package com.github.dinsaw.valuestore.controller;

import com.github.dinsaw.valuestore.util.DateUtils;
import com.github.dinsaw.valuestore.util.MongoUtils;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.RoutingContext;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.dinsaw.valuestore.util.AppConstants.NET_ASSET_VALUES;
import static com.github.dinsaw.valuestore.util.AppConstants.SCHEME_CODE;
import static com.github.dinsaw.valuestore.util.MongoConstants.DESC;
import static com.github.dinsaw.valuestore.util.WebConstants.APPLICATION_JSON_UTF_8;
import static com.github.dinsaw.valuestore.util.WebConstants.CONTENT_TYPE;

/**
 * Created by dinsaw on 15/12/18.
 */
@AllArgsConstructor
public class NavController {
    private MongoClient mongoClient;

    public void getBySchemeCode(RoutingContext routingContext) {
        final String schemeCode = routingContext.request().getParam(SCHEME_CODE);
        JsonObject query = new JsonObject().put(SCHEME_CODE, schemeCode);
        JsonObject sort = new JsonObject().put("date", DESC);
        FindOptions findOptions = new FindOptions().setSort(sort);

        mongoClient.findWithOptions(NET_ASSET_VALUES, query, findOptions, rh -> {
            routingContext.response()
                    .putHeader(CONTENT_TYPE, APPLICATION_JSON_UTF_8)
                    .end(Json.encodePrettily(convertList(rh.result())));
        });
    }

    private List<JsonObject> convertList(List<JsonObject> rawNavList) {
        return rawNavList.stream().map(n -> convert(n)).collect(Collectors.toList());
    }

    private JsonObject convert(JsonObject rawJson) {
        rawJson.remove("_id");
        return rawJson.put("date", MongoUtils.getDate(rawJson.getJsonObject("date")));
    }

}
