package com.example.videoconferencing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.videoconferencing.Fragments.ProfileFragment;
import com.example.videoconferencing.Fragments.VideoMeetFragment;
import com.example.videoconferencing.RestApi.SharedPreferenceManager;
import com.example.videoconferencing.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;
    // Declaring fragment transaction
    private FragmentTransaction transaction;
    // Declaring instance of SharedPreferenceManager
    private SharedPreferenceManager sharedPreferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Binding is used instead of findViewById
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Check internet connection
        if (!checkConnection())
            return;


        // Initializing SharedPreferenceManager instance
        sharedPreferenceManager = new SharedPreferenceManager(this);


        // By default VideoMeetFragment is loaded in the MainActivity
        VideoMeetFragment videoMeetFragment = new VideoMeetFragment();
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.navHostFragment, videoMeetFragment);
        transaction.commit();


        /*     Bottom Navigation Drawer Menu     */


        // This method is used to show what will happen after selecting any item from bottom navigation drawer
        binding.bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.videoMeet:
                        if (!checkConnection())
                            return true;
                        else {

                            // When user clicks on "Meet" item it will load VideoMeetFragment in this activity
                            VideoMeetFragment videoMeetFragment = new VideoMeetFragment();
                            transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.navHostFragment, videoMeetFragment);
                            transaction.commit();

                        }
                        break;

                    case R.id.profile:
                        if (!checkConnection())
                            return true;
                        else {

                            // When user clicks on "Profile" item it will load ProfileFragment in this activity
                            ProfileFragment profileFragment = new ProfileFragment();
                            transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.navHostFragment, profileFragment);
                            transaction.commit();

                        }
                        break;

                    case R.id.logout:
                        if (!checkConnection())
                            return true;
                        else {

                            // Alert Dialog Box
                            // It will be shown when we press back button in our mobile
                            new AlertDialog.Builder(MainActivity.this)
                                    .setMessage("Are you sure you want to log out?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        // When user press "YES" button it will log out user from the app and it will move to "SignInActivity" page
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            // Log out user from the app
                                            sharedPreferenceManager.logout();

                                            // After user is logged out successfully it will move to "SignInActivity" page
                                            Intent moveToSignInActivity = new Intent(MainActivity.this, SignInActivity.class);
                                            moveToSignInActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(moveToSignInActivity);

                                        }
                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                // When user press "NO" button it will close the alert dialog box
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            }).show();

                        }
                        break;

                    default:

                }

                return true;

            }
        });

    }


    // This method is used to check internet connection
    private boolean checkConnection() {

        // Initializing connectivity manager to get connectivity information
        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Initializing wifi and mobileNetwork object to check wifi and mobileNetwork connectivity
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        // If wifi and mobile network is connected then this condition will run
        if (wifi.isConnected() || mobileNetwork.isConnected()) {

            // Set visibility visible of main activity if internet is active
            binding.mainPage.setVisibility(View.VISIBLE);

            return true;

        }

        // If wifi and mobile network is not connected then this condition will run
        else {

            // Set visibility visible of main activity if internet is inactive
            binding.mainPage.setVisibility(View.GONE);

            // Initialize dialog
            Dialog dialog = new Dialog(this);

            // Set content view
            dialog.setContentView(R.layout.sample_no_internet_dialog);

            // Set outside touch
            dialog.setCanceledOnTouchOutside(false);

            // Set cancel when back button is pressed
            dialog.setCancelable(false);

            // Set dialog width and height
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            // Set transparent background
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // Set animation
            dialog.getWindow().getAttributes().windowAnimations = R.style.Animation_Design_BottomSheetDialog;

            // Initialize button inside dialog box
            Button btnTryAgain = dialog.findViewById(R.id.btnTryAgain);

            // Perform on click listener on above button "btnTryAgain"
            btnTryAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Call recreate method
                    // If internet connection is off this button won't work whenever clicked
                    // If internet connection is on this button will work dialog box will be closed and it will resume the process of app whenever clicked
                    recreate();

                }
            });

            // Show dialog box
            dialog.show();

            return false;

        }

    }


    // Whenever back button is pressed in the mobile then this method is called
    @Override
    public void onBackPressed() {

        // Alert Dialog Box
        // It will be shown when we press back button in our mobile
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    // When user press "YES" button it will close the app
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).setNegativeButton("no", new DialogInterface.OnClickListener() {
            // When user press "NO" button it will close the alert dialog box
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        }).show();

    }

}
