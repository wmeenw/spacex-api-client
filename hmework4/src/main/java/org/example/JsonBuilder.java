package org.example;

import com.google.gson.Gson;

public class JsonBuilder {
    private final Gson gson = new Gson();

    public String toJson(Object obj){
        return gson.toJson(obj);
    }

    public String buildDateQuery(String startDate, String endDate){
        return "{\"query\": {\"date_utc\": {\"$gte\": \"" + startDate + "T00:00:00.000Z\", \"$lte\": \"" + endDate + "T23:59:59.999Z\"}}}";
    }

    public String buildSuccessQuery(boolean success) {
        return "{\"query\": {\"success\": " + success + "}}";
    }
}
