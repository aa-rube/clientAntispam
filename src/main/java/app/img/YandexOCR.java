package app.img;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class YandexOCR {

    public String sendOCRRequest(String imagePath, String iAmToken, String folderId) throws Exception {

        byte[] fileData = Files.readAllBytes(Paths.get(imagePath));
        String base64Image = Base64.getEncoder().encodeToString(fileData);

        String requestBody = String.format("{\"mimeType\": \"JPEG\", \"languageCodes\": [\"*\"], \"model\":" +
                        " \"page\", \"content\": \"%s\"}",base64Image);

        HttpURLConnection connection = getHttpURLConnectionMethod(iAmToken, folderId);

        OutputStream os = connection.getOutputStream();
        os.write(requestBody.getBytes());
        os.flush();

        InputStream is = connection.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        os.close();
        is.close();
        br.close();
        return extractTextFromJson(response.toString());
    }

    private HttpURLConnection getHttpURLConnectionMethod(String iAmToken, String folderId) throws IOException {
        URL url = new URL("https://ocr.api.cloud.yandex.net/ocr/v1/recognizeText");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + iAmToken);
        connection.setRequestProperty("x-folder-id", folderId);
        connection.setRequestProperty("x-data-logging-enabled", "true");
        connection.setDoOutput(true);
        return connection;
    }

    public String extractTextFromJson(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            JSONObject result = json.getJSONObject("result");
            JSONObject textAnnotation = result.getJSONObject("textAnnotation");

            StringBuilder extractedText = new StringBuilder();
            JSONArray blocks = textAnnotation.getJSONArray("blocks");
            for (int i = 0; i < blocks.length(); i++) {
                JSONObject block = blocks.getJSONObject(i);
                JSONArray lines = block.getJSONArray("lines");
                for (int j = 0; j < lines.length(); j++) {
                    JSONObject line = lines.getJSONObject(j);
                    String text = line.getString("text");
                    extractedText.append(text).append(" ");
                }
            }

            return extractedText.toString().trim();
        } catch (Exception e) {
            return "null";
        }
    }
}