package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class JsonParser {
    private final Gson gson = new Gson();

    public List<Launch> parseLaunches(String json) throws JsonSyntaxException {
        Type listType = new TypeToken<List<Launch>>() {
        }.getType();
        return gson.fromJson(json, listType);
    }

    public Launch parseLaunch(String json) throws JsonSyntaxException{
        return gson.fromJson(json, Launch.class);
    }

    public List<Launch> parseLaunchesFromQueryResponse(String json) {
        JsonObject root = gson.fromJson(json, JsonObject.class);
        JsonArray docs = root.getAsJsonArray("docs");
        Type listType = new TypeToken<List<Launch>>(){}.getType();
        return gson.fromJson(docs, listType);
    }
}
