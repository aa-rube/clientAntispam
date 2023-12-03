package app.img;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class IAmToken {
    public String getIAmToken(String oathToken, String oldToken) {
        try {
            String url = "https://iam.api.cloud.yandex.net/iam/v1/tokens";
            String jsonInputString = "{\"yandexPassportOauthToken\":\"" + oathToken + "\"}";

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            OutputStream os = con.getOutputStream();
            os.write(jsonInputString.getBytes());
            os.flush();
            os.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();
            JSONObject jsonObject = new JSONObject(response.toString());
            return jsonObject.getString("iamToken");
        } catch (Exception e) {
            return oldToken;
        }
    }
}
