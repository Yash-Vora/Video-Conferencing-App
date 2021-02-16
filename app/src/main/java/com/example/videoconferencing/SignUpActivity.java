package com.example.videoconferencing;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.videoconferencing.RestApi.ApiInterface;
import com.example.videoconferencing.RestApi.PojoModels.SignUpModel;
import com.example.videoconferencing.RestApi.RetrofitInstance;
import com.example.videoconferencing.databinding.ActivitySignUpBinding;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {


    private ActivitySignUpBinding binding;
    // Declaring instance of progressDialog
    private Dialog progressDialog;

    // Declaring the instance of ApiInterface
    private ApiInterface apiInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Binding is used instead of findViewById
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
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


        // Following code shows what happens when we click on "Already have an account?Sign In" button
        // When we click on "Already have an account?Sign In" button then it will move to sign in page
        binding.moveToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        // Following code shows what happens when we click on "Sign In" button
        // When we click on "Sign Up" button it will authenticate the user and store data in the database
        binding.buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check internet connection
                if (!checkConnection())
                    return;

                // Performing Validation
                else if (!validateUsername() | !validateEmail() | !validatePhone() | !validatePassword() | !validateConfirmPassword())
                    return;

                // Code for calling the api and it will sign up user in the app
                else {

                    // Showing ProgressDialog box whenever "Sign Up" button is clicked
                    showProgressDialog();

                    // Calling the signUpUser() method from ApiInterface class
                    // Here it is calling the api and get response back from the api in below onResponse() method
                    apiInterface.signUpUser(binding.signUpUsername.getEditText().getText().toString(),
                            binding.signUpEmail.getEditText().getText().toString(),
                            binding.signUpPhoneNo.getEditText().getText().toString(),
                            binding.signUpPassword.getEditText().getText().toString())
                            .enqueue(new Callback<SignUpModel>() {

                                // If response is received successfully after calling the api then this method is called
                                @Override
                                public void onResponse(Call<SignUpModel> call, Response<SignUpModel> response) {

                                    // Closing the ProgressDialog box
                                    dismissProgressDialog();

                                    if (response.body() != null) {

                                        SignUpModel signUpModel = response.body();

                                        if (response.isSuccessful()) {

                                            if (signUpModel.getError().startsWith("200")) {

                                                Toast.makeText(SignUpActivity.this, signUpModel.getMessage(), Toast.LENGTH_SHORT).show();

                                                // Here it will finish this activity and move back to SignInActivity
                                                onBackPressed();

                                            }

                                            else
                                                Toast.makeText(SignUpActivity.this, signUpModel.getMessage(), Toast.LENGTH_SHORT).show();

                                        }

                                        else
                                            Toast.makeText(SignUpActivity.this, signUpModel.getMessage(), Toast.LENGTH_SHORT).show();


                                    }

                                    else {

                                        Log.e("Response Error", "List is empty");

                                        Toast.makeText(SignUpActivity.this, "Sign up failed. Please sign up again", Toast.LENGTH_LONG).show();

                                    }

                                }

                                // If any error is occurred while receiving the response from the api then this method is called
                                @Override
                                public void onFailure(Call<SignUpModel> call, Throwable t) {

                                    // Closing the ProgressDialog box
                                    dismissProgressDialog();

                                    Log.e("Response Error", t.getLocalizedMessage());

                                    Toast.makeText(SignUpActivity.this, "Sign up failed. Please sign up again. Check your connection", Toast.LENGTH_LONG).show();

                                }

                            });

                }

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
            binding.signUpPage.setVisibility(View.VISIBLE);

            return true;

        }

        // If wifi and mobile network is not connected then this condition will run
        else {

            // Set visibility visible of main activity if internet is inactive
            binding.signUpPage.setVisibility(View.GONE);

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


    // Username Validation
    private boolean validateUsername() {

        String Username = binding.signUpUsername.getEditText().getText().toString();
        //        ^           Beginning of the string
        //       [A-Za-z]     First character is not whitespace but it can be either capitals or small letter
        //       [A-Z a-z]    Other characters can be either whitespaces or capitals or small letters
        //        +           String contains at least one alphabetical char
        //        $           End of the string
        String checkUsername = "^[A-Za-z][A-Z a-z]+$";

        // If username is empty then following error message is shown to the user
        if(Username.isEmpty()) {
            binding.signUpUsername.setError("Field cannot be empty");
            return false;
        }

        // If username length is greater than 20 then following error message is shown to the user
        else if(Username.length()>20) {
            binding.signUpUsername.setError("Name is too large");
            return false;
        }

        // If username length is less than 5 then following error message is shown to the user
        else if(Username.length()<=5) {
            binding.signUpUsername.setError("Name is too short");
            return false;
        }

        // If username does not match with above regex string then following error message is shown to the user
        else if(!Username.matches(checkUsername)) {
            binding.signUpUsername.setError("Name must not contain numeric or special characters or first whitespace");
            return false;
        }

        // If all above validation is done successfully then error will be removed
        else {
            binding.signUpUsername.setError(null);
            binding.signUpUsername.setErrorEnabled(false);
            return true;
        }

    }

    // Email Validation
    private boolean validateEmail() {

        String Email = binding.signUpEmail.getEditText().getText().toString();
        //       [a-z0-9._-]     Starting part of the email must be from a-z,0-9,.,_,-
        //       @               Then after next character @ is mandatory
        //       [a-z]           Then after next part of the email must be from a-z
        //       .               Then after next character . is mandatory
        //       [a-z]           Then after next part of the email must be from a-z
        String checkEmail = "[a-z0-9._-]+@[a-z]+\\.+[a-z]+";

        // If email is empty then following error message is shown to the user
        if(Email.isEmpty()) {
            binding.signUpEmail.setError("Field cannot be empty");
            return false;
        }

        // If email does not match with above regex string then following error message is shown to the user
        else if(!Email.matches(checkEmail)) {
            binding.signUpEmail.setError("Invalid Email!");
            return false;
        }

        // If all above validation is done successfully then error will be removed
        else {
            binding.signUpEmail.setError(null);
            binding.signUpEmail.setErrorEnabled(false);
            return true;
        }

    }

    // Phone Number Validation
    private boolean validatePhone() {

        String phoneNumber = binding.signUpPhoneNo.getEditText().getText().toString();
        //        [1-9]          It matches first digit and checks if number lies between 1 to 9
        //        [0-9]          It matches other digits and checks if number lies between 0 to 9
        //        {9}            It specifies remaining
        String checkMobileNumber = "[1-9][0-9]{9}";

        // If phone number is empty then following error message is shown to the user
        if(phoneNumber.isEmpty()) {
            binding.signUpPhoneNo.setError("Field cannot be empty");
            return false;
        }

        // If phone number is greater or less than 10 characters then following error message is shown to the user
        else if(phoneNumber.length()>10 || phoneNumber.length()<10) {
            binding.signUpPhoneNo.setError("Mobile number must be of 10 characters");
            return false;
        }

        // If phone number does not match with above regex string then following error message is shown to the user
        else if(!phoneNumber.matches(checkMobileNumber)) {
            binding.signUpPhoneNo.setError("Invalid mobile number");
            return false;
        }

        // If all above validation is done successfully then error will be removed
        else {
            binding.signUpPhoneNo.setError(null);
            return true;
        }

    }

    // Password Validation
    private boolean validatePassword() {

        String Password = binding.signUpPassword.getEditText().getText().toString();

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
            binding.signUpPassword.setError("Field cannot be empty");
            return false;
        }

        // If password is less than 12 characters then following error message is shown to the user
        else if(Password.length() < 12) {
            binding.signUpPassword.setError("Password must be of at least 12 characters");
            return false;
        }

        // If there is any whitespace in password then following error message is shown to the user
        else if(whiteSpace.matcher(Password).find()) {
            binding.signUpPassword.setError("Whitespaces aren't allowed");
            return false;
        }

        // If password does not match with above regex string then following error message is shown to the user
        else if(!lowerCase.matcher(Password).find() || !upperCase.matcher(Password).find() ||
                !digitCase.matcher(Password).find() || !specialCharacters.matcher(Password).find()) {
            binding.signUpPassword.setError("Password is too weak");
            return false;
        }

        // If all above validation is done successfully then error will be removed
        else {
            binding.signUpPassword.setError(null);
            binding.signUpPassword.setErrorEnabled(false);
            return true;
        }

    }

    // Confirm Password Validation
    private boolean validateConfirmPassword() {

        String confirmPassword = binding.signUpConfirmPassword.getEditText().getText().toString();
        String Password = binding.signUpPassword.getEditText().getText().toString();

        // If password is empty then following error message is shown to the user
        if(confirmPassword.isEmpty()) {
            binding.signUpConfirmPassword.setError("Field cannot be empty");
            return false;
        }

        // If confirm password is not equal to password then following error message is shown to the user
        else if(!confirmPassword.equals(Password)) {
            binding.signUpConfirmPassword.setError("Password is not matching");
            return false;
        }

        // If all above validation is done successfully then error will be removed
        else {
            binding.signUpConfirmPassword.setError(null);
            binding.signUpConfirmPassword.setErrorEnabled(false);
            return true;
        }

    }

}