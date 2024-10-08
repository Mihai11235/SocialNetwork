package com.example.socialnetwork.domain;

import java.time.LocalDateTime;

public class FriendRequestDTO {
    Long userId;
    String username;
    LocalDateTime date;

    public FriendRequestDTO(Long id, String username, LocalDateTime date) {
        this.userId = id;
        this.username = username;
        this.date = date;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
