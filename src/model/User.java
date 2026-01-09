package model;

import java.time.LocalDateTime;

/**
 * Model class representing the users table
 */
public class User {
    private Long id;
    private Long companyId;
    private String username;
    private String password;
    private String description;
    private String userGroup; // ADMIN or STAFF
    private String status; // YES or NO
    private LocalDateTime createdAt;

    // Constructors
    public User() {}

    public User(Long id, Long companyId, String username, String password,
                String description, String userGroup, String status) {
        this.id = id;
        this.companyId = companyId;
        this.username = username;
        this.password = password;
        this.description = description;
        this.userGroup = userGroup;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(userGroup);
    }

    public boolean isStaff() {
        return "STAFF".equals(userGroup);
    }

    @Override
    public String toString() {
        return username + " (" + userGroup + ")";
    }
}
