package com.github.dinsaw.valuestore.util;

import io.vertx.core.json.JsonObject;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created by dinsaw on 9/12/18.
 */
public class DateUtils {
    public static Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static JsonObject mongoDate(LocalDate localDate) {
        return new JsonObject().put("$date",
                localDate.atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME)+".000Z") ;
    }
}
