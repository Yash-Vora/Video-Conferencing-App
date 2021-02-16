package com.example.videoconferencing;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.videoconferencing.RestApi.ApiInterface;
import com.example.videoconferencing.RestApi.PojoModels.SignInModel;
import com.example.videoconferencing.RestApi.RetrofitInstance;
import com.example.videoconferencing.RestApi.SharedPreferenceManager;
import com.example.videoconferencing.databinding.ActivitySignInBinding;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {


    private ActivitySignInBinding binding;
    // Declaring instance of progressDialog
    private Dialog progressDialog;
    // Declaring instance of SharedPreferenceManager
    private SharedPreferenceManager sharedPreferenceManager;

    // Declaring the instance of ApiInterface
    private ApiInterface apiInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Binding is used instead of findViewById
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Check internet connection
        if (!checkConnection())
            return;


        // Initializing ApiInterface instance
        apiInterface = RetrofitInstance.getRetrofit().create(ApiInterface.class);


        // Initializing SharedPreferenceManager instance
        sharedPreferenceManager = new SharedPreferenceManager(this);


        // Following code shows what happens when we click on "Forgot Password?" button
        // When we click on "Forgot Password?" button then it will move to forgot password page
        binding.forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Shared Animation Code
                Pair[] pairs = new Pair[3];

                pairs[0] = new Pair<View,String>(binding.logoImage, "logoImage");
                pairs[1] = new Pair<View,String>(binding.logoText, "logoText");
                pairs[2] = new Pair<View,String>(binding.buttonSignIn, "button");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignInActivity.this, pairs);

                // Moving to ForgotPassword using the shared animation
                Intent moveToSignUpActivity = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
                startActivity(moveToSignUpActivity, options.toBundle());

            }
        });


        // Following code shows what happens when we click on "Want to create account?Sign Up" button
        // When we click on "Already have an account?Sign In" button then it will move to sign up page
        binding.moveToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Shared Animation Code
                Pair[] pairs = new Pair[4];

                pairs[0] = new Pair<View,String>(binding.logoImage, "logoImage");
                pairs[1] = new Pair<View,String>(binding.logoText, "logoText");
                pairs[2] = new Pair<View,String>(binding.buttonSignIn, "button");
                pairs[3] = new Pair<View,String>(binding.moveToSignUp, "newPage");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignInActivity.this, pairs);

                // Moving to SignUpActivity using the shared animation
                Intent moveToSignUpActivity = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(moveToSignUpActivity, options.toBundle());

            }
        });


        // Following code shows what happens when we click on "Sign In" button
        // When we click on "Sign In" button it will authenticate the user and sign in user into the app
        binding.buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check internet connection
                if (!checkConnection())
                    return;

                // Performing Validation
                else if (!validateEmail() | !validatePassword())
                    return;

                // Code for calling the api and it will sign in user in the app
                else {

                    // Showing ProgressDialog box whenever "Sign In" button is clicked
                    showProgressDialog();

                    // Calling the signInUser() method from ApiInterface class
                    // Here it is calling the api and get response back from the api in below onResponse() method
                    apiInterface.signInUser(binding.signInEmail.getEditText().getText().toString(),
                            binding.signInPassword.getEditText().getText().toString())
                            .enqueue(new Callback<SignInModel>() {

                                // If response is received successfully after calling the api then this method is called
                                @Override
                                public void onResponse(Call<SignInModel> call, Response<SignInModel> response) {

                                    if (response.body() != null) {

                                        SignInModel signInModel = response.body();

                                        if (response.isSuccessful()) {

                                            if (signInModel.getError().startsWith("200")) {

                                                // Save user data in SharedPreferenceManager and now user is logged in the app
                                                sharedPreferenceManager.saveUser(signInModel.getUser());

                                                // Closing the ProgressDialog box
                                                dismissProgressDialog();

                                                // After getting successful response and user is signed in successful it will move to "MainActivity" page
                                                Intent moveToMainActivity = new Intent(SignInActivity.this, MainActivity.class);
                                                moveToMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(moveToMainActivity);

                                            }

                                            else {

                                                // Closing the ProgressDialog box
                                                dismissProgressDialog();

                                                Toast.makeText(SignInActivity.this, signInModel.getMessage(), Toast.LENGTH_SHORT).show();

                                            }

                                        }

                                        else {

                                            // Closing the ProgressDialog box
                                            dismissProgressDialog();

                                            Toast.makeText(SignInActivity.this, signInModel.getMessage(), Toast.LENGTH_SHORT).show();

                                        }


                                    }

                                    else {

                                        // Closing the ProgressDialog box
                                        dismissProgressDialog();

                                        Log.e("Response Error", "List is empty");

                                        Toast.makeText(SignInActivity.this, "Sign up failed. Please sign up again", Toast.LENGTH_LONG).show();

                                    }

                                }

                                // If any error is occurred while receiving the response from the api then this method is called
                                @Override
                                public void onFailure(Call<SignInModel> call, Throwable t) {

                                    // Closing the ProgressDialog box
                                    dismissProgressDialog();

                                    Log.e("Response Error", t.getLocalizedMessage());

                                    Toast.makeText(SignInActivity.this, "Sign in failed. Please sign in again. Check your connection", Toast.LENGTH_LONG).show();

                                }

                            });

                }

            }
        });


        // If user is already logged in then it will directly open "Main Activity" page
        if (sharedPreferenceManager.isLoggedIn()) {

            // As user is logged in the app it will move to MainActivity
            Intent moveToMainActivity = new Intent(SignInActivity.this, MainActivity.class);
            moveToMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(moveToMainActivity);

        }

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
            binding.signInPage.setVisibility(View.VISIBLE);

            return true;

        }

        // If wifi and mobile network is not connected then this condition will run
        else {

            // Set visibility visible of main activity if internet is inactive
            binding.signInPage.setVisibility(View.GONE);

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

        String Email = binding.signInEmail.getEditText().getText().toString();
        //       [a-z0-9._-]     Starting part of the email must be from a-z,0-9,.,_,-
        //       @               Then after next character @ is mandatory
        //       [a-z]           Then after next part of the email must be from a-z
        //       .               Then after next character . is mandatory
        //       [a-z]           Then after next part of the email must be from a-z
        String checkEmail = "[a-z0-9._-]+@[a-z]+\\.+[a-z]+";

        // If email is empty then following error message is shown to the user
        if(Email.isEmpty()) {
            binding.signInEmail.setError("Field cannot be empty");
            return false;
        }

        // If email does not match with above regex string then following error message is shown to the user
        else if(!Email.matches(checkEmail)) {
            binding.signInEmail.setError("Invalid Email!");
            return false;
        }

        // If all above validation is done successfully then error will be removed
        else {
            binding.signInEmail.setError(null);
            binding.signInEmail.setErrorEnabled(false);
            return true;
        }

    }

    // Password Validation
    private boolean validatePassword() {

        String Password = binding.signInPassword.getEditText().getText().toString();

        // Password must contain at least 1 special character
        Pattern specialCharacters = Pattern.compile("[!@#$%^&+=]");
        // Password must contain at least 1 lowercase character
        Pattern lowerCase = Pattern.compile("[a-z]");
        // Password must contain at least 1 uppercase character
        Pattern upperCase = Pattern.compile("[A-Z]");
        // Password must contain at least 1 digit
        Pattern digitCase = Pattern.compile("[0-9]");
        // Password must not contain any white spaces
        Pattern whiteSpace = Pattern.compile("[ ]");

        // If password is empty then following error message is shown to the user
        if(Password.isEmpty()) {
            binding.signInPassword.setError("Field cannot be empty");
            return false;
        }

        // If password is less than 12 characters then following error message is shown to the user
        else if(Password.length() < 12) {
            binding.signInPassword.setError("Password must be of at least 12 characters");
            return false;
        }

        // If there is any whitespace in password then following error message is shown to the user
        else if(whiteSpace.matcher(Password).find()) {
            binding.signInPassword.setError("Whitespaces aren't allowed");
            return false;
        }

        // If password does not match with above regex string then following error message is shown to the user
        else if(!lowerCase.matcher(Password).find() || !upperCase.matcher(Password).find() || !digitCase.matcher(Password).find() ||
                !specialCharacters.matcher(Password).find()) {
            binding.signInPassword.setError("Password is too weak");
            return false;
        }

        // If all above validation is done successfully then error will be removed
        else {
            binding.signInPassword.setError(null);
            binding.signInPassword.setErrorEnabled(false);
            return true;
        }

    }

}