package org.example;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class SpaceXMenu {
    private SpaceXHttpClient httpClient;
    private JsonParser jsonParser;
    private JsonBuilder jsonBuilder;
    private final CacheManager cacheManager;
    private static final String BASE_URL = "https://api.spacexdata.com";

    SpaceXMenu(SpaceXHttpClient httpClient, JsonParser jsonParser, JsonBuilder jsonBuilder, CacheManager cacheManager){
        this.httpClient = httpClient;
        this.jsonParser = jsonParser;
        this.jsonBuilder = jsonBuilder;
        this.cacheManager = cacheManager;
    }

    private void printMenu(){
        System.out.println("=== SpaceX Launch Explorer ===");
        System.out.println("1. Показать все запуски");
        System.out.println("2. Показать последний запуск");
        System.out.println("3. Поиск запусков по дате");
        System.out.println("4. Показать только успешные запуски");
        System.out.println("5. Показать только неудачные запуски");
        System.out.println("6. Очистить кеш");
        System.out.println("7. Выход");
    }

    private void printLaunches(List<Launch> launches){
        for (var launch : launches){
            System.out.println("#" + launch.getFlightNumber() + " " + launch.getName() + " | " + launch.getDateUtc() + " | Успех: " + formatSuccess(launch));
        }
    }

    private void printLaunch(Launch launch){
        System.out.println("Запуск: " + launch.getName());
        System.out.println("Номер: " + launch.getFlightNumber());
        System.out.println("Дата: " + launch.getDateUtc());
        System.out.println("Успех: " + formatSuccess(launch));
        if (launch.getDetails() == null || launch.getDetails().isEmpty()){
            System.out.println("Описание: нет данных");
        } else {
            System.out.println("Описание: " + launch.getDetails());
        }
    }

    public void start() {
        var scanner = new Scanner(System.in);
        while(true){
            printMenu();
            String choice = scanner.nextLine().trim();
            if (choice.equals("1")){
                showAllLaunches();
            } else if (choice.equals("2")){
                showLatestLaunch();
            } else if (choice.equals("3")){
                System.out.print("Введите дату начала (YYYY-MM-DD): ");
                String startDate = scanner.nextLine().trim();
                System.out.print("Введите дату конца (YYYY-MM-DD): ");
                String endDate = scanner.nextLine().trim();
                showLaunchesByDate(startDate, endDate);
            } else if (choice.equals("4")) {
                showLaunchesBySuccess(true);
            } else if (choice.equals("5")) {
                showLaunchesBySuccess(false);
            } else if (choice.equals("6")) {
                cacheManager.clear();


            } else if (choice.equals("7")) {
                System.out.println("byyyeee");
                return;
            } else {
                    System.out.println("Введено некорректное число/символ, попробуйте снова!");
            }
        }
    }

    private void showAllLaunches() {
        String cachedJson = cacheManager.load("launches_all");
        if (cachedJson != null){
            var launches = jsonParser.parseLaunches(cachedJson);
            printLaunches(launches);
            return;
        }
        try {
            String json = httpClient.get(BASE_URL + "/v5/launches");
            cacheManager.save("launches_all", json);
            var launches = jsonParser.parseLaunches(json);
            printLaunches(launches);
        } catch (IOException e){
            String cacheJsonNew = cacheManager.load("launches_all");
            if (cacheJsonNew != null) {
                var launches = jsonParser.parseLaunches(cacheJsonNew);
                printLaunches(launches);
                System.err.println("[!] Сервер недоступен. Показаны данные из кеша.");
                return;
            }
            System.err.println("Ошибка сети: " + e.getMessage());
        } catch (JsonSyntaxException e){
            String cacheJsonNew = cacheManager.load("launches_all");
            if (cacheJsonNew != null) {
                var launches = jsonParser.parseLaunches(cacheJsonNew);
                printLaunches(launches);
                System.err.println("[!] Сервер недоступен. Показаны данные из кеша.");
                return;
            }
            System.err.println("Ошибка обработки данных JSON: " + e.getMessage());
        }
    }

    private void showLatestLaunch() {
        String cachedJson = cacheManager.load("launches_latest");
        if (cachedJson != null){
            var launch = jsonParser.parseLaunch(cachedJson);
            printLaunch(launch);
            return;
        }
        try {
            String json = httpClient.get(BASE_URL + "/v5/launches/latest");
            cacheManager.save("launches_latest", json);
            var launch = jsonParser.parseLaunch(json);
            printLaunch(launch);

        } catch (IOException e){
            String cacheJsonNew = cacheManager.load("launches_latest");
            if (cacheJsonNew != null) {
                var launch = jsonParser.parseLaunch(cacheJsonNew);
                printLaunch(launch);
                System.err.println("[!] Сервер недоступен. Показаны данные из кеша.");
                return;
            }
            System.err.println("Ошибка сети: " + e.getMessage());
        } catch (JsonSyntaxException e){
            String cacheJsonNew = cacheManager.load("launches_latest");
            if (cacheJsonNew != null) {
                var launch = jsonParser.parseLaunch(cacheJsonNew);
                printLaunch(launch);
                System.err.println("[!] Сервер недоступен. Показаны данные из кеша.");
                return;
            }
            System.err.println("Ошибка обработки данных JSON: " + e.getMessage());
        }
    }

    private String formatSuccess(Launch launch){
         if (launch.isUpcoming()) return "предстоящий";
         Boolean success = launch.getSuccess();
         if (success == null) return "нет данных";
         return success ? "да" : "нет";
    }

    private void showLaunchesByDate(String startDate, String endDate) {
        String key = "query_" + startDate + "_" + endDate;
        String cachedJson = cacheManager.load(key);
        if (cachedJson != null){
            var launches = jsonParser.parseLaunchesFromQueryResponse(cachedJson);
            printLaunches(launches);
            return;
        }
        try {
            String jsonBody = jsonBuilder.buildDateQuery(startDate, endDate);
            String json = httpClient.post(BASE_URL + "/v5/launches/query", jsonBody);
            cacheManager.save(key, json);
            var launches = jsonParser.parseLaunchesFromQueryResponse(json);
            printLaunches(launches);
            if (launches.isEmpty()){
                System.out.println("За указанный период запуски не найдены");
            }
        } catch (IOException e){
            String cacheJsonNew = cacheManager.load(key);
            if (cacheJsonNew != null) {
                var launches = jsonParser.parseLaunchesFromQueryResponse(cacheJsonNew);
                printLaunches(launches);
                System.err.println("[!] Сервер недоступен. Показаны данные из кеша.");
                return;
            }
            System.err.println("Ошибка сети: " + e.getMessage());
        } catch (JsonSyntaxException e){
            String cacheJsonNew = cacheManager.load(key);
            if (cacheJsonNew != null) {
                var launches = jsonParser.parseLaunchesFromQueryResponse(cacheJsonNew);
                printLaunches(launches);
                System.err.println("[!] Сервер недоступен. Показаны данные из кеша.");
                return;
            }
            System.err.println("Ошибка обработки данных JSON: " + e.getMessage());
        }
    }

    private void showLaunchesBySuccess(boolean isSuccessful) {
        String key;
        if (isSuccessful){
            key = "query_success_true";
        } else {
            key = "query_success_false";
        }
        String cachedJson = cacheManager.load(key);
        if (cachedJson != null){
            var launches = jsonParser.parseLaunchesFromQueryResponse(cachedJson);
            printLaunches(launches);
            return;
        }
        try {
            String jsonBody = jsonBuilder.buildSuccessQuery(isSuccessful);
            String json = httpClient.post(BASE_URL + "/v5/launches/query", jsonBody);
            cacheManager.save(key, json);

            var launches = jsonParser.parseLaunchesFromQueryResponse(json);
            printLaunches(launches);
            if (launches.isEmpty()){
                System.out.println("Запуски не найдены");
            }
        } catch (IOException e){
            String cacheJsonNew = cacheManager.load(key);
            if (cacheJsonNew != null) {
                var launches = jsonParser.parseLaunchesFromQueryResponse(cacheJsonNew);
                printLaunches(launches);
                System.err.println("[!] Сервер недоступен. Показаны данные из кеша.");
                return;
            }
            System.err.println("Ошибка сети: " + e.getMessage());
        } catch (JsonSyntaxException e){
            String cacheJsonNew = cacheManager.load(key);
            if (cacheJsonNew != null) {
                var launches = jsonParser.parseLaunchesFromQueryResponse(cacheJsonNew);
                printLaunches(launches);
                System.err.println("[!] Сервер недоступен. Показаны данные из кеша.");
                return;
            }
            System.err.println("Ошибка обработки данных JSON: " + e.getMessage());
        }
    }
}
