package app.img;

import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class DownloadImg {
    private final YandexOCR yandex = new YandexOCR();

    public String getPhotoText(org.telegram.telegrambots.meta.api.objects.File file,
                               String fileId, String token, String iAmToken, String folderId) throws Exception {

        String fileUrl = "https://api.telegram.org/file/bot" + token + "/" + file.getFilePath();
        URL url = new URL(fileUrl);
        InputStream inputStream = url.openStream();

        java.io.File tempFile = java.io.File.createTempFile(fileId, ".jpg");
        FileOutputStream outputStream = new FileOutputStream(tempFile);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        inputStream.close();
        outputStream.close();

        return yandex.sendOCRRequest(tempFile.getAbsolutePath(), iAmToken, folderId);
    }

}