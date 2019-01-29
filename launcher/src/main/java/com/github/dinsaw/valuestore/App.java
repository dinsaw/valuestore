package com.github.dinsaw.valuestore;

import com.github.dinsaw.valuestore.verticle.IngestVerticle;
import com.github.dinsaw.valuestore.verticle.Server;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by dinsaw on 2/12/18.
 */
@Slf4j
public class App {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        DeploymentOptions options = new DeploymentOptions()
                .setConfig(getConfig(vertx));
        log.info("Config = {}", options.getConfig());

        Server server = new Server();
        vertx.deployVerticle(server, options, res -> {
            if (res.failed()) res.cause().printStackTrace();
        });

        Vertx ingestVertx = Vertx.vertx();
        ingestVertx.deployVerticle(new IngestVerticle(), options);
    }

    private static JsonObject getConfig(Vertx vertx) {
        JsonObject json = vertx.getOrCreateContext().config();
        log.info("Current Dir {}", System.getProperty("user.dir"));
        if (json == null || json.isEmpty()) {
            return new JsonObject(
                    vertx.fileSystem()
                            .readFileBlocking("conf/config.json"));
        }
        return json;
    }
}
