package com.example.videoconferencing;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.videoconferencing.RestApi.ApiInterface;
import com.example.videoconferencing.RestApi.RetrofitInstance;
import com.example.videoconferencing.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends AppCompatActivity {


    private ActivityForgotPasswordBinding binding;
    // Declaring instance of progressDialog
    private Dialog progressDialog;

    // Declaring the instance of ApiInterface
    private ApiInterface apiInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Binding is used instead of findViewById
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Check internet connection
        if (!checkConnection())
            return;


        // Initializing ApiInterface instance
        apiInterface = RetrofitInstance.getRetrofit().create(ApiInterface.class);


        // Following code shows what happens when we click on "<-" button
        // When we click on "<-" button then it will finish this activity and move to previous activity
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        // Following code shows what happens when we click on "Reset Password" button
        // When we click on "Reset Password" button then user will receive reset password link on their email address
        binding.buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check internet connection
                if (!checkConnection())
                    return;

                // Performing Validation
                else if (!validateEmail())
                    return;

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
            binding.forgotPasswordPage.setVisibility(View.VISIBLE);

            return true;

        }

        // If wifi and mobile network is not connected then this condition will run
        else {

            // Set visibility visible of main activity if internet is inactive
            binding.forgotPasswordPage.setVisibility(View.GONE);

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


    /*    Code for showing/dismissing progress dialog box    */


    // This method is used to show progress dialog
    private void showProgressDialog() {

        // Initialize progress dialog
        progressDialog = new Dialog(this);

        // Set content view
        progressDialog.setContentView(R.layout.progress_dialog);

        // Set outside touch
        progressDialog.setCanceledOnTouchOutside(false);

        // Set cancel when back button is pressed
        progressDialog.setCancelable(false);

        // Set progress dialog width and height
        progressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

        // Set transparent background
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Set animation
        progressDialog.getWindow().getAttributes().windowAnimations = R.style.Animation_Design_BottomSheetDialog;

        // Show progress dialog box
        progressDialog.show();

    }

    // This method is used to dismiss progress dialog
    private void dismissProgressDialog() {

        if (progressDialog.isShowing())
            progressDialog.dismiss();

    }


    /*    Validation Methods    */


    // Email Validation
    private boolean validateEmail() {

        String Email = binding.resetPasswordEmail.getEditText().getText().toString();
        //       [a-z0-9._-]     Starting part of the email must be from a-z,0-9,.,_,-
        //       @               Then after next character @ is mandatory
        //       [a-z]           Then after next part of the email must be from a-z
        //       .               Then after next character . is mandatory
        //       [a-z]           Then after next part of the email must be from a-z
        String checkEmail = "[a-z0-9._-]+@[a-z]+\\.+[a-z]+";

        // If email is empty then following error message is shown to the user
        if(Email.isEmpty()) {
            binding.resetPasswordEmail.setError("Field cannot be empty");
            return false;
        }

        // If email does not match with above regex string then following error message is shown to the user
        else if(!Email.matches(checkEmail)) {
            binding.resetPasswordEmail.setError("Invalid Email!");
            return false;
        }

        // If all above validation is done successfully then error will be removed
        else {
            binding.resetPasswordEmail.setError(null);
            binding.resetPasswordEmail.setErrorEnabled(false);
            return true;
        }

    }

}