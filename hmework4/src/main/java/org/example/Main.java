package org.example;

public class Main {
    public static void main(String[] args) {
        var httpClient= new SpaceXHttpClient();
        var jsonParser = new JsonParser();
        var jsonBuilder = new JsonBuilder();
        var cacheManager = new CacheManager();
        var menu = new SpaceXMenu(httpClient, jsonParser, jsonBuilder, cacheManager);

        menu.start();
    }
}
