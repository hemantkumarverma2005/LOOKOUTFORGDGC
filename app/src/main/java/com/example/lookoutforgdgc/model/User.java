package com.example.lookoutforgdgc.model;

import androidx.annotation.NonNull;

public class User {
    private int id;
    private String username;
    private String email;
    private String password;
    private long score;

    public User() { }

    public User(int id, String username, String email, String password, long score) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.score = score;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserName() { return username; }
    public void setUserName(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public long getScore() { return score; }
    public void setScore(long score) { this.score = score; }

    @NonNull
    public static User empty() { return new User(0, "", "", "", 0L); }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", score=" + score +
                '}';
    }
}
