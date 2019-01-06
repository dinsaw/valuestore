package com.github.dinsaw.valuestore.util;

import io.vertx.core.http.HttpServerRequest;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Objects;

/**
 * Created by dinsaw on 14/12/18.
 */
@Accessors(fluent = true)
public class Pagination {
    final static int DEFAULT_LIMIT = 15;
    final static int MAX_LIMIT = 500;
    final static String LIMIT_PARAM = "limit";
    final static String MARKER_PARAM = "marker";

    @Getter
    private String marker;
    @Getter
    private Integer limit;

    public Pagination(HttpServerRequest request) {
        final String limitString = request.getParam(LIMIT_PARAM);
        limit = Objects.isNull(limitString) ? DEFAULT_LIMIT : Integer.valueOf(limitString);
        limit = limit > MAX_LIMIT ? MAX_LIMIT : limit;

        marker = request.getParam(MARKER_PARAM);
        // TODO - remove Zero as default marker
        marker = Objects.isNull(marker) ? "0" : marker;
    }
}
