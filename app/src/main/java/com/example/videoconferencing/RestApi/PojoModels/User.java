package com.example.videoconferencing.RestApi.PojoModels;

import com.google.gson.annotations.SerializedName;

public class User {

    private int id;
    private String username, email;
    @SerializedName("phoneno")
    private String phoneNo;


    // Constructor
    public User(int id, String username, String email, String phoneNo) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phoneNo = phoneNo;
    }


    // Getters and Setters Methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

}
