package com.github.dinsaw.valuestore;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

/**
 * Created by dinsaw on 9/12/18.
 */
public class Config {
    public static MongoClient mongoClient(Vertx vertx){
        return MongoClient.createShared(vertx, new JsonObject()
                .put("db_name", "valueStore")
                .put("connection_string", "mongodb://127.0.0.1:27017")
                .put("username", "valueUser")
                .put("password", "Password*2018")
                .put("heartbeatFrequencyMS", 1000000)
                .put("minHeartbeatFrequencyMS", 1000000));
    }
}
