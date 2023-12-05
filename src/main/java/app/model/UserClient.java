package app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class UserClient {
    @Id
    private Long chatId;
    private String userClintName;
    private boolean isPaid;
    private LocalDateTime subscriptionEnd;
    private boolean checkPhoto;
    private boolean tryPeriod;

    public boolean isTryPeriod() {
        return tryPeriod;
    }
    public void setTryPeriod(boolean tryPeriod) {
        this.tryPeriod = tryPeriod;
    }
    public boolean isCheckPhoto() {
        return checkPhoto;
    }
    public void setCheckPhoto(boolean checkPhoto) {
        this.checkPhoto = checkPhoto;
    }
    public Long getChatId() {
        return chatId;
    }
    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
    public String getUserClintName() {
        return userClintName;
    }
    public void setUserClintName(String userClintName) {
        this.userClintName = userClintName;
    }
    public boolean isPaid() {
        return isPaid;
    }
    public void setPaid(boolean paid) {
        isPaid = paid;
    }
    public LocalDateTime getSubscriptionEnd() {
        return subscriptionEnd;
    }
    public void setSubscriptionEnd(LocalDateTime subscriptionEnd) {
        this.subscriptionEnd = subscriptionEnd;
    }
}
