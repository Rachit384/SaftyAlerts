
package com.example.saftyalerts.Model;

public class Post {
    private String userId;
    private String user;
    private String userProfileImage;
    private String text;
    private String imageUrl;


    public Post() {
        // Default constructor required for Firebase
    }

    public Post(String userId, String user, String userProfileImage, String text, String imageUrl) {
        this.userId = userId;
        this.user = user;
        this.userProfileImage = userProfileImage;
        this.text = text;
        this.imageUrl = imageUrl;

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getuser() {
        return user;
    }

    public void setuser(String user) {
        this.user = user;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

