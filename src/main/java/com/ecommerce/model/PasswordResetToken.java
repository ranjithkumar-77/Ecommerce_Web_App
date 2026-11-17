package com.ecommerce.model;

import java.sql.Timestamp;

public class PasswordResetToken {

    private int id;
    private int userId;
    private String token;
    private Timestamp expiryTime;
    private boolean used;
    private Timestamp createdAt;

    public PasswordResetToken() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Timestamp getExpiryTime() { return expiryTime; }
    public void setExpiryTime(Timestamp expiryTime) { this.expiryTime = expiryTime; }

    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "PasswordResetToken{id=" + id + ", userId=" + userId +
               ", used=" + used + ", expiryTime=" + expiryTime + "}";
    }
}
