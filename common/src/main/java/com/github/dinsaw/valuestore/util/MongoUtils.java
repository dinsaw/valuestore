package com.github.dinsaw.valuestore.util;

import io.vertx.core.json.JsonObject;

/**
 * Created by dinsaw on 15/12/18.
 */
public class MongoUtils {

    public static JsonObject updateObject(JsonObject jsonObject) {
        return new JsonObject().put(MongoConstants.SET, jsonObject);
    }

    public static String getDate(JsonObject jsonObject) {
        return jsonObject.getString("$date").split("T")[0];
    }
}
