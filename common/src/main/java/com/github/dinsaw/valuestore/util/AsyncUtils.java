package com.github.dinsaw.valuestore.util;

import io.vertx.core.AsyncResult;
import org.slf4j.Logger;

/**
 * Created by dinsaw on 15/12/18.
 */
public class AsyncUtils {
    public static void logIfFailed(AsyncResult<?> asyncResult, Logger logger) {
        if (asyncResult.failed()) { logger.error("Failed in async op", asyncResult.cause()); }
    }
}
