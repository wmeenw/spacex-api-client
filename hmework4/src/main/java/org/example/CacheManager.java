package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CacheManager {
    private static final long TTL = 5 * 60 * 1000;
    private final File cacheDir;
    private final Gson gson = new Gson();

    public CacheManager(){
        cacheDir = new File("cache");
        if (!cacheDir.exists()){
            cacheDir.mkdirs();
        }
    }

    //для тестов чтоб
    public CacheManager(String cacheDirPath) {
        cacheDir = new File(cacheDirPath);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
    }

    private File getCacheFile(String key) {
        return new File(cacheDir, key + ".json");
    }

    public void save(String key, String data){
        var file = getCacheFile(key);

        try (var writer = new BufferedWriter(new FileWriter(file))){
            writer.write(data);

            var meta = loadMeta();
            meta.put(key, System.currentTimeMillis());
            saveMeta(meta);
        } catch (IOException e) {
            System.err.println("Не удалось сохранить кеш для ключа " + key + ": " + e.getMessage());
        }
    }

    private Map<String, Long> loadMeta(){
        var file = new File(cacheDir,"cache_meta.json");
        if (!file.exists()) return new HashMap<>();

        try (var reader = new BufferedReader(new FileReader(file))) {
            var sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();
            Type type = new TypeToken<Map<String, Long>>(){}.getType();
            return gson.fromJson(json, type);
        } catch (IOException | JsonSyntaxException e) {
            System.err.println("Ошибка чтения мета-файла: " + e.getMessage());
            return new HashMap<>();
        }
    }

    private void saveMeta(Map<String, Long> meta){
        var file = new File(cacheDir, "cache_meta.json");
        String json = gson.toJson(meta);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
            writer.write(json);
        } catch (IOException e) {
            System.err.println("Не удалось сохранить мета-файл: " + e.getMessage());
        }
    }

    public String load(String key){
        var file = getCacheFile(key);
        if (!file.exists()) return null;
        var meta = loadMeta();
        var time = meta.get(key);
        if (time == null) return null;
        if (System.currentTimeMillis() - time > TTL) return null;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))){
            var sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e){
            System.err.println("Ошибка чтения кеша для ключа " + key + ": " + e.getMessage());
            return null;
        }
    }

    public void clear(){
        var files = cacheDir.listFiles();
        if (files == null) {
            System.err.println("Ошибка очиски файлов: файлы отсутствуют");
            return;
        }
        for (var file : files){
            file.delete();
        }
        saveMeta(new HashMap<>());
        System.out.println("Кеш очищен.");
    }


}
