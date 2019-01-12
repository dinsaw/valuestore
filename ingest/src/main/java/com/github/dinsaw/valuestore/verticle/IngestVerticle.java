package com.github.dinsaw.valuestore.verticle;

import com.github.dinsaw.valuestore.Config;
import com.github.dinsaw.valuestore.service.RefreshService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.ext.web.client.WebClient;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

/**
 * Created by dinsaw on 8/12/18.
 */
@Slf4j
public class IngestVerticle extends AbstractVerticle{

    @Override
    public void start() throws Exception {
        log.debug("Starting Ingest Verticle");

        vertx.setTimer(5000, buildRefreshService());

        long refreshDelay = Duration.of(6, ChronoUnit.HOURS).toMillis();
        vertx.setPeriodic(refreshDelay, buildRefreshService());

    }


    private Handler<Long> buildRefreshService() {

        WebClient webClient = WebClient.create(vertx);

        final RefreshService refreshService = new RefreshService(Config.mongoClient(vertx, config()), webClient);
        return refreshService;
    }


}
