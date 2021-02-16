package com.example.videoconferencing.RestApi;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.videoconferencing.RestApi.PojoModels.User;

public class SharedPreferenceManager {

    private static String SHARED_PREF_NAME = "sharedPreference";
    private SharedPreferences sharedPreferences;
    private Context context;
    private SharedPreferences.Editor editor;


    // Constructor
    public SharedPreferenceManager(Context context) {
        this.context = context;
    }


    // This method is used to save user data
    public void saveUser(User user) {

        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt("id", user.getId());
        editor.putString("username", user.getUsername());
        editor.putString("email", user.getEmail());
        editor.putString("phoneNo", user.getPhoneNo());
        editor.putBoolean("loggedIn", true);
        editor.apply();

    }


    // This method is used to check whether user is logged in
    public boolean isLoggedIn() {

        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("loggedIn", false);

    }


    // This method is used to get the user data
    public User getUser() {

        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(sharedPreferences.getInt("id", -1),
                sharedPreferences.getString("username", null),
                sharedPreferences.getString("email", null),
                sharedPreferences.getString("phoneNo", null));

    }


    // This method is used to log out the user from the app
    public void logout() {

        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

    }

}
