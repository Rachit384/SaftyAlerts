package com.example.saftyalerts.Model;


public class usermodel {
    private String username;
    private String profileImageUrl;

    public usermodel() {
        // Default constructor required for calls to DataSnapshot.getValue(usermodel.class)
    }

    public usermodel(String username, String profileImageUrl) {
        this.username = username;
        this.profileImageUrl = profileImageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}