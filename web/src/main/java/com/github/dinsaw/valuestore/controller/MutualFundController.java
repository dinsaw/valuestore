package com.github.dinsaw.valuestore.controller;

import com.github.dinsaw.valuestore.util.AppConstants;
import com.github.dinsaw.valuestore.util.MongoUtils;
import com.github.dinsaw.valuestore.util.Pagination;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.RoutingContext;
import lombok.AllArgsConstructor;

import java.util.List;

import static com.github.dinsaw.valuestore.util.AppConstants.SCHEME_CODE;
import static com.github.dinsaw.valuestore.util.MongoConstants.ASC;
import static com.github.dinsaw.valuestore.util.MongoConstants.GT;
import static com.github.dinsaw.valuestore.util.WebConstants.APPLICATION_JSON_UTF_8;
import static com.github.dinsaw.valuestore.util.WebConstants.CONTENT_TYPE;

/**
 * Created by dinsaw on 9/12/18.
 */
@AllArgsConstructor
public class MutualFundController {

    private MongoClient mongoClient;

    public void getBySchemeCode(RoutingContext routingContext) {
        final String schemeCode = routingContext.request().getParam(SCHEME_CODE);
        JsonObject query = new JsonObject().put(SCHEME_CODE, schemeCode);

        mongoClient.findOne(AppConstants.MUTUAL_FUNDS, query, null, rh -> {
           routingContext.response()
                   .putHeader(CONTENT_TYPE, APPLICATION_JSON_UTF_8)
                    .end(Json.encodePrettily(convert(rh.result())));
        });
    }

    public void getAll(RoutingContext routingContext) {
        Pagination pagination = new Pagination(routingContext.request());

        JsonObject query = new JsonObject();
        query.put(SCHEME_CODE, new JsonObject().put(GT, pagination.marker()));

        JsonObject sort = new JsonObject().put(SCHEME_CODE, ASC);
        FindOptions findOptions = new FindOptions().setLimit(pagination.limit()).setSort(sort);

        mongoClient.findWithOptions(AppConstants.MUTUAL_FUNDS, query, findOptions, h -> {
           routingContext.response()
                .putHeader(CONTENT_TYPE, APPLICATION_JSON_UTF_8)
                   .end(Json.encodePrettily(convert(h.result())));
        });
    }

    private JsonObject convert(JsonObject result) {
        JsonObject dateJsonObject = result.getJsonObject("navDate");
        result.put("navDate", MongoUtils.getDate(dateJsonObject))
                .remove("_id");
        return result;
    }

    private List<JsonObject> convert(List<JsonObject> resultList) {
        resultList.forEach(r -> convert(r));
        return resultList;
    }
}
