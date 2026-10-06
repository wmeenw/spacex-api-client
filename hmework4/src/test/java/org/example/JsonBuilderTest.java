package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JsonBuilderTest {
    private final JsonBuilder builder = new JsonBuilder();

    @Test
    void buildSuccessQueryTrue() {
        String result = builder.buildSuccessQuery(true);
        String expected = "{\"query\": {\"success\": true}}";
        assertEquals(expected, result);
    }

    @Test
    void buildSuccessQueryFalse() {
        String result = builder.buildSuccessQuery(false);
        String expected = "{\"query\": {\"success\": false}}";
        assertEquals(expected, result);
    }

    @Test
    void buildDateQueryReturnCorrectJson() {
        String result = builder.buildDateQuery("2020-01-01", "2020-12-31");
        String expected = "{\"query\": {\"date_utc\": {\"$gte\": \"2020-01-01T00:00:00.000Z\", \"$lte\": \"2020-12-31T23:59:59.999Z\"}}}";
        assertEquals(expected, result);
    }
}