package com.github.dinsaw.valuestore;

import com.github.dinsaw.valuestore.verticle.IngestVerticle;
import com.github.dinsaw.valuestore.verticle.Server;
import io.vertx.config.ConfigRetriever;
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
        Vertx serverVertx = Vertx.vertx();

        ConfigRetriever configRetriever = ConfigRetriever.create(serverVertx);
        configRetriever.getConfig(ar -> {
            if (ar.succeeded()) {
                DeploymentOptions options = new DeploymentOptions()
                        .setConfig(ar.result());

                log.info("Config = {}", options.getConfig());
                Server server = new Server();
                serverVertx.deployVerticle(server, options, res -> {
                    if (res.failed()) res.cause().printStackTrace();
                });

                Vertx ingestVertx = Vertx.vertx();
                ingestVertx.deployVerticle(new IngestVerticle(), options);
            } else {
                log.error("Failed to get config");
            }
        });
    }
}
