package com.github.dinsaw.valuestore;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

/**
 * Created by dinsaw on 9/12/18.
 */
public class Config {
    public static MongoClient mongoClient(Vertx vertx, JsonObject config){
        return MongoClient.createShared(vertx, new JsonObject()
                .put("db_name",  config.getValue("mongo.db_name"))
                .put("connection_string", config.getString("mongo.connection_string"))
                .put("username", config.getString("mongo.username"))
                .put("password", config.getString("mongo.password"))
                .put("heartbeatFrequencyMS", config.getInteger("mongo.heartbeatFrequencyMS"))
                .put("minHeartbeatFrequencyMS", config.getInteger("mongo.minHeartbeatFrequencyMS"))
                .put("waitQueueMultiple", config.getInteger("mongo.waitQueueMultiple")));
    }
}
