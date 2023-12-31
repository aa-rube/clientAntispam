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
    private int isPaid;
    private LocalDateTime subscriptionEnd;
    private int checkPhoto;
    private int tryPeriod;

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

    public int getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(int isPaid) {
        this.isPaid = isPaid;
    }

    public LocalDateTime getSubscriptionEnd() {
        return subscriptionEnd;
    }

    public void setSubscriptionEnd(LocalDateTime subscriptionEnd) {
        this.subscriptionEnd = subscriptionEnd;
    }

    public int getCheckPhoto() {
        return checkPhoto;
    }

    public void setCheckPhoto(int checkPhoto) {
        this.checkPhoto = checkPhoto;
    }

    public int getTryPeriod() {
        return tryPeriod;
    }

    public void setTryPeriod(int tryPeriod) {
        this.tryPeriod = tryPeriod;
    }
}
