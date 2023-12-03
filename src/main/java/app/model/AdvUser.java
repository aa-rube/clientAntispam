package app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "advertisers_users")
public class AdvUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String userName;
    private int postCount;
    private int daysOfPermission;
    private Long adminChatIdOwner;
    private String adminUserNameOwner;
    private String permissionToGroup;
    private LocalDateTime endPermission;
    private  boolean started;


    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public int getDaysOfPermission() {
        return daysOfPermission;
    }

    public void setDaysOfPermission(int daysOfPermission) {
        this.daysOfPermission = daysOfPermission;
    }

    public Long getAdminChatIdOwner() {
        return adminChatIdOwner;
    }

    public void setAdminChatIdOwner(Long adminChatIdOwner) {
        this.adminChatIdOwner = adminChatIdOwner;
    }

    public String getAdminUserNameOwner() {
        return adminUserNameOwner;
    }

    public void setAdminUserNameOwner(String adminUserNameOwner) {
        this.adminUserNameOwner = adminUserNameOwner;
    }

    public String getPermissionToGroup() {
        return permissionToGroup;
    }

    public void setPermissionToGroup(String permissionToGroup) {
        this.permissionToGroup = permissionToGroup;
    }

    public LocalDateTime getEndPermission() {
        return endPermission;
    }

    public void setEndPermission(LocalDateTime endPermission) {
        this.endPermission = endPermission;
    }
}