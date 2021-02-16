package com.example.videoconferencing.RestApi.PojoModels;

public class DeleteUserModel {

    private String error, message;


    // Constructor
    public DeleteUserModel(String error, String message) {
        this.error = error;
        this.message = message;
    }


    // Getters and Setters Methods
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
