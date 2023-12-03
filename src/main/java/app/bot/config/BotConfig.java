package app.bot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {
    @Value("${bot.username}")
    private String botName;
    @Value("${bot.token}")
    private String token;
    @Value("${oauth.token}")
    private String yandexToken;
    @Value("${folder.id}")
    private String folderYandexId;

    public Long getOwnerChatId() {
        return ownerChatId;
    }

    @Value("${owner.chat.id}")
    private Long ownerChatId;
    public String getFolderYandexId() {
        return folderYandexId;
    }
    public String getYandexToken() {
        return yandexToken;
    }
    public String getBotName() {
        return botName;
    }
    public String getToken() {
        return token;
    }
}