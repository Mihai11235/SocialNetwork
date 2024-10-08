package com.example.socialnetwork.domain;

import java.time.LocalDateTime;

public class FriendRequest extends Entity<Tuple<Long, Long>> {

    LocalDateTime date;

    public FriendRequest(Long user1, Long user2) {
        setId(new Tuple<>(user1,user2));
        this.date = LocalDateTime.now();
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Long getUser1() {
        return getId().getLeft();
    }

    public Long getUser2() {
        return getId().getRight();
    }
}
