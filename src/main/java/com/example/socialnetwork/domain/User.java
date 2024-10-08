package com.example.socialnetwork.domain;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User extends Entity<Long> {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private List<User> friends;

    public User(String firstName, String lastName, String username, String email, String password) {
        friends = new ArrayList<>();
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }


    public String getLastName() {
        return lastName;
    }


    public List<User> getFriends() {
        return friends;
    }

    public void addFriend(User u){
        friends.add(u);
    }

    public void removeFriend(User u){
        friends.remove(u);
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        List<Long> friendsId = new ArrayList<>();
        for(User u : friends)
            friendsId.add(u.getId());
        return "User{" +
                "id='" + id + '\'' +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", friends=" + friendsId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User that = (User) o;
        return getId().equals(((User) o).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getFriends());
    }
}
