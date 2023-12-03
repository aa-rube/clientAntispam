package app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class GroupToListen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String groupUserName;
    private Long ownerChatId;

    public int getId() {
        return id;
    }

    public String getGroupUserName() {
        return groupUserName;
    }

    public void setGroupUserName(String groupUserName) {
        this.groupUserName = groupUserName;
    }

    public Long getOwnerChatId() {
        return ownerChatId;
    }

    public void setOwnerChatId(Long ownerChatId) {
        this.ownerChatId = ownerChatId;
    }
}
