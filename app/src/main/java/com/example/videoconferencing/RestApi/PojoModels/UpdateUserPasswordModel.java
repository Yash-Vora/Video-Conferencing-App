package com.example.videoconferencing.RestApi.PojoModels;

public class UpdateUserPasswordModel {

    private String error, message;


    // Constructor
    public UpdateUserPasswordModel(String error, String message) {
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
