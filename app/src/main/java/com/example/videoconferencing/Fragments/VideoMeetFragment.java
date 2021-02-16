package com.example.videoconferencing.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.example.videoconferencing.R;
import com.example.videoconferencing.databinding.FragmentVideoMeetBinding;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

public class VideoMeetFragment extends Fragment {


    private FragmentVideoMeetBinding binding;
    // Declaring Jitsi server url
    private URL serverURL;


    // Constructor
    public VideoMeetFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentVideoMeetBinding.inflate(inflater, container, false);


        // Initializing Jitsi in our project
        try {

            // Setting server URL
            serverURL = new URL("https://meet.jit.si");

            // Setting conference options
            JitsiMeetConferenceOptions defaultOptions =
                    new JitsiMeetConferenceOptions.Builder()
                            .setServerURL(serverURL)
                            .setWelcomePageEnabled(false)
                            .build();

            // Change default conference options to above declared default options
            JitsiMeet.setDefaultConferenceOptions(defaultOptions);

        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }


        // Following code shows what happens when we click on "Join" button
        // When we click on "Join" button then it will join the meeting using the secret code entered by the user
        binding.joinMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check internet connection
                if (!checkConnection())
                    return;

                // Performing Validation
                else if (!validateSecretCode())
                    return;

                // Here user can start video conference call along with live chatting with their friends
                else {

                    // Setting video conferencing options to initiate video conferencing
                    JitsiMeetConferenceOptions options =
                            new JitsiMeetConferenceOptions.Builder()
                                    .setRoom(binding.secretCode.getEditText().getText().toString())
                                    .setAudioMuted(false)
                                    .setVideoMuted(false)
                                    .setAudioOnly(false)
                                    .setFeatureFlag("invite.enabled", false)
                                    .setWelcomePageEnabled(false)
                                    .build();

                    // Launch video conferencing using the above conference options
                    JitsiMeetActivity.launch(getContext(), options);

                }

            }
        });


        // Following code shows what happens when we click on "Share" button
        // When we click on "Share" button then user can share the secret code with their friends
        binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check internet connection
                if (!checkConnection())
                    return;

                // Validating secret code
                else if (!validateSecretCode())
                    return;

                // Here user can share secret code with their friends to join meeting
                else {

                    // Implicit Intent
                    // Implicit intent is used whenever we don't know where to go from the current activity. Here we don't know destination activity
                    // Example implicit intent is share button where user selects where to share data
                    Intent sendSecretCode = new Intent();
                    sendSecretCode.setAction(Intent.ACTION_SEND);
                    sendSecretCode.setType("text/plain");
                    sendSecretCode.putExtra(Intent.EXTRA_TEXT, "" + binding.secretCode.getEditText().getText().toString());
                    startActivity(sendSecretCode);

                }

            }
        });


        return binding.getRoot();

    }


    // This method is used to check internet connection
    private boolean checkConnection() {

        // Initializing connectivity manager to get connectivity information
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

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
            Dialog dialog = new Dialog(getContext());

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
                    getActivity().recreate();

                }
            });

            // Show dialog box
            dialog.show();

            return false;

        }

    }


    /*    Validation Methods    */


    // Secret Code Validation
    private boolean validateSecretCode() {

        String secretCode = binding.secretCode.getEditText().getText().toString();

        // If secret code is empty then following error message is shown to the user
        if (secretCode.isEmpty()) {
            binding.secretCode.setError("Field cannot be empty");
            return false;
        }

        // If secret code is less than 6 characters then following error message is shown to the user
        else if (secretCode.length() < 6) {
            binding.secretCode.setError("Secret code must be of at least 6 characters");
            return false;
        }

        // If all above validation is done successfully then error will be removed
        else {
            binding.secretCode.setError(null);
            binding.secretCode.setErrorEnabled(false);
            return true;
        }

    }

}