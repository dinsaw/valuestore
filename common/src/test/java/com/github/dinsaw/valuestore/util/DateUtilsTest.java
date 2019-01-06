package com.github.dinsaw.valuestore.util;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestOptions;
import io.vertx.ext.unit.TestSuite;
import io.vertx.ext.unit.report.ReportOptions;

import java.time.LocalDate;

/**
 * Created by dinsaw on 9/12/18.
 */
public class DateUtilsTest {

    public static void main(String[] args) {
        TestSuite testSuite = TestSuite.create("UtilTest");
        testSuite.test("mongoDate returns valid test", context -> {
            JsonObject jsonObject = DateUtils.mongoDate(LocalDate.of(2018,10, 12));
            JsonObject expected =  new JsonObject().put("$date", "2018-10-12T00:00:00.000Z");
            context.assertEquals(expected, jsonObject);
        });

        testSuite.run(new TestOptions().addReporter(new ReportOptions().setTo("console")));
    }

}