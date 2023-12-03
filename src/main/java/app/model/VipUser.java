package app.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Table(name = "vip")
public class VipUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    public int getId() {
        return id;
    }
    private String userName;
    private Long ownerChatId;
    public Long getOwnerChatId() {
        return ownerChatId;
    }
    public void setOwnerChatId(Long ownerChatId) {
        this.ownerChatId = ownerChatId;
    }
    public VipUser() {
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
