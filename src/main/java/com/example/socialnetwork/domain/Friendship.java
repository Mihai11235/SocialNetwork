package com.example.socialnetwork.domain;
import java.time.LocalDateTime;

public class Friendship extends Entity<Tuple<Long,Long>> {


    LocalDateTime friendsFrom;

    public Friendship(Long user1, Long user2) {
        setId(new Tuple<>(user1,user2));
        this.friendsFrom = LocalDateTime.now();
    }

    public LocalDateTime getFriendsFrom() {
        return friendsFrom;
    }

    public Long getUser1() {
        return getId().getLeft();
    }

    public Long getUser2() {
        return getId().getRight();
    }
    public void setFriendsFrom(LocalDateTime friendsFrom) {
        this.friendsFrom = friendsFrom;
    }

    public void setId(Long id1, Long id2){
        setId(new Tuple<>(id1, id2));
    }

    /**
     *
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return friendsFrom;
    }
}
