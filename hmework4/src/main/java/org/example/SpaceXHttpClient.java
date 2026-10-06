package org.example;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SpaceXHttpClient {
    private static final int CONNECT_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 10000;

    public String get(String urlString) throws IOException {
        HttpURLConnection connection = null;
        try {
            var url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);

            int status = connection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                String response = readStream(inputStream);
                return response;
            } else {
                InputStream inputStream = connection.getErrorStream();
                String error = readStream(inputStream);
                throw new IOException("HTTP error " + status + ": " + error);
            }
        } finally {
            if (connection != null){
                connection.disconnect();
            }
        }
    }

    public String readStream(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return "";
        }
        try (var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            var sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
    }

    public String post(String urlString, String jsonBody) throws IOException {
        HttpURLConnection connection = null;
        try {
            var url = new URL(urlString);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setDoOutput(true);

            try (OutputStream outputStream = connection.getOutputStream();
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
                writer.write(jsonBody);
            }

            int status = connection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                String response = readStream(inputStream);
                return response;
            } else {
                InputStream inputStream = connection.getErrorStream();
                String error = readStream(inputStream);
                throw new IOException("HTTP error " + status + ": " + error);
            }
        } finally {
            if (connection != null){
                connection.disconnect();
            }
        }
    }
}
