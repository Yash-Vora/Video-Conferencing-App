package com.example.videoconferencing.RestApi.PojoModels;

public class UpdateUserProfileModel {

    private User user;
    private String error,message;


    // Constructor
    public UpdateUserProfileModel(User user, String error, String message) {
        this.user = user;
        this.error = error;
        this.message = message;
    }


    // Getters and Setters Methods
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
