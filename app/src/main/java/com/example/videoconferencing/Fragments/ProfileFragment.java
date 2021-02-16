package com.example.videoconferencing.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.videoconferencing.R;
import com.example.videoconferencing.RestApi.ApiInterface;
import com.example.videoconferencing.RestApi.PojoModels.DeleteUserModel;
import com.example.videoconferencing.RestApi.PojoModels.UpdateUserPasswordModel;
import com.example.videoconferencing.RestApi.PojoModels.UpdateUserProfileModel;
import com.example.videoconferencing.RestApi.RetrofitInstance;
import com.example.videoconferencing.RestApi.SharedPreferenceManager;
import com.example.videoconferencing.SignInActivity;
import com.example.videoconferencing.databinding.FragmentProfileBinding;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {


    private FragmentProfileBinding binding;
    // Declaring instance of progressDialog
    private Dialog progressDialog;
    // Declaring instance of SharedPreferenceManager
    private SharedPreferenceManager sharedPreferenceManager;

    // Declaring the instance of ApiInterface
    private ApiInterface apiInterface;


    // Constructor
    public ProfileFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);


        // Initializing ApiInterface instance
        apiInterface = RetrofitInstance.getRetrofit().create(ApiInterface.class);


        // Initializing SharedPreferenceManager instance
        sharedPreferenceManager = new SharedPreferenceManager(getActivity());


        // Show logged in user profile information
        binding.profileUsername.getEditText().setText(sharedPreferenceManager.getUser().getUsername());
        binding.profileEmail.getEditText().setText(sharedPreferenceManager.getUser().getEmail());
        binding.profilePhoneNo.getEditText().setText(sharedPreferenceManager.getUser().getPhoneNo());


        // Following code shows what happens when we click on "Delete" icon
        // When we click on "Delete" icon then it will delete user account
        binding.deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check internet connection
                if (!checkConnection())
                    return;

                // Code for calling the api and it will delete user account
                else {

                    // Alert Dialog Box
                    // It will be shown when when user press delete icon
                    new AlertDialog.Builder(getContext())
                            .setMessage("Are you sure you want to delete your account?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                // When user press "YES" button it will delete user account
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    // Showing ProgressDialog box whenever "Delete" icon then it will delete the account
                                    showProgressDialog();

                                    // Calling the deleteUser() method from ApiInterface class
                                    // Here it is calling the api and get response back from the api in below onResponse() method
                                    apiInterface.deleteUser(sharedPreferenceManager.getUser().getId())
                                            .enqueue(new Callback<DeleteUserModel>() {

                                                // If response is received successfully after calling the api then this method is called
                                                @Override
                                                public void onResponse(Call<DeleteUserModel> call, Response<DeleteUserModel> response) {

                                                    if (response.body() != null) {

                                                        DeleteUserModel deleteUserModel = response.body();

                                                        if (response.isSuccessful()) {

                                                            if (deleteUserModel.getError().startsWith("200")) {

                                                                // It will remove user session by deleting user information from SharedPreferenceManager
                                                                sharedPreferenceManager.logout();

                                                                // Closing the ProgressDialog box
                                                                dismissProgressDialog();

                                                                Toast.makeText(getContext(), deleteUserModel.getMessage(), Toast.LENGTH_SHORT).show();

                                                                // After getting successful response and user account is deleted successfully it will move to "SignInActivity" page
                                                                Intent moveToSignInActivity = new Intent(getActivity(), SignInActivity.class);
                                                                moveToSignInActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                startActivity(moveToSignInActivity);

                                                            }

                                                            else {

                                                                // Closing the ProgressDialog box
                                                                dismissProgressDialog();

                                                                Toast.makeText(getContext(), deleteUserModel.getMessage(), Toast.LENGTH_SHORT).show();

                                                            }

                                                        }

                                                        else {

                                                            // Closing the ProgressDialog box
                                                            dismissProgressDialog();

                                                            Toast.makeText(getContext(), deleteUserModel.getMessage(), Toast.LENGTH_SHORT).show();

                                                        }

                                                    }

                                                    else {

                                                        // Closing the ProgressDialog box
                                                        dismissProgressDialog();

                                                        Log.e("Response Error", "List is empty");

                                                        Toast.makeText(getContext(), "Delete account failed. Please delete your account again", Toast.LENGTH_LONG).show();

                                                    }

                                                }

                                                // If any error is occurred while receiving the response from the api then this method is called
                                                @Override
                                                public void onFailure(Call<DeleteUserModel> call, Throwable t) {

                                                    // Closing the ProgressDialog box
                                                    dismissProgressDialog();

                                                    Log.e("Response Error", t.getLocalizedMessage());

                                                    Toast.makeText(getContext(), "Delete account failed. Check your connection", Toast.LENGTH_LONG).show();

                                                }

                                            });

                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                                 dialogInterface.cancel();
                        }
                    }).show();

                }

            }
        });


        // Following code shows what happens when we click on "save" button
        // When we click on "save" button then it will update user profile
        binding.updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check internet connection
                if (!checkConnection())
                    return;

                // Performing Validation
                else if (!validateUsername() | !validateEmail() | !validatePhone())
                    return;

                // Code for calling the api and it will update user profile
                else {

                    // Showing ProgressDialog box whenever "Save" button for updating profile
                    showProgressDialog();

                    // Calling the updateUserProfile() method from ApiInterface class
                    // Here it is calling the api and get response back from the api in below onResponse() method
                    apiInterface.updateUserProfile(sharedPreferenceManager.getUser().getId(),
                            binding.profileUsername.getEditText().getText().toString(),
                            binding.profileEmail.getEditText().getText().toString(),
                            binding.profilePhoneNo.getEditText().getText().toString())
                            .enqueue(new Callback<UpdateUserProfileModel>() {

                                // If response is received successfully after calling the api then this method is called
                                @Override
                                public void onResponse(Call<UpdateUserProfileModel> call, Response<UpdateUserProfileModel> response) {

                                    if (response.body() != null) {

                                        UpdateUserProfileModel updateUserProfileModel = response.body();

                                        if (response.isSuccessful()) {

                                            if (updateUserProfileModel.getError().startsWith("200")) {

                                                // Save user data in SharedPreferenceManager and now user is logged in the app
                                                sharedPreferenceManager.saveUser(updateUserProfileModel.getUser());

                                                // Closing the ProgressDialog box
                                                dismissProgressDialog();

                                                Toast.makeText(getContext(), updateUserProfileModel.getMessage(), Toast.LENGTH_SHORT).show();

                                            }

                                            else {

                                                // Closing the ProgressDialog box
                                                dismissProgressDialog();

                                                Toast.makeText(getContext(), updateUserProfileModel.getMessage(), Toast.LENGTH_SHORT).show();

                                            }

                                        }

                                        else {

                                            // Closing the ProgressDialog box
                                            dismissProgressDialog();

                                            Toast.makeText(getContext(), updateUserProfileModel.getMessage(), Toast.LENGTH_SHORT).show();

                                        }


                                    }

                                    else {

                                        // Closing the ProgressDialog box
                                        dismissProgressDialog();

                                        Log.e("Response Error", "List is empty");

                                        Toast.makeText(getContext(), "Update profile failed. Please update your profile again", Toast.LENGTH_LONG).show();

                                    }

                                }

                                // If any error is occurred while receiving the response from the api then this method is called
                                @Override
                                public void onFailure(Call<UpdateUserProfileModel> call, Throwable t) {

                                    // Closing the ProgressDialog box
                                    dismissProgressDialog();

                                    Log.e("Response Error", t.getLocalizedMessage());

                                    Toast.makeText(getContext(), "Update profile failed. Check your connection", Toast.LENGTH_LONG).show();

                                }

                            });

                }

            }
        });


        // Following code shows what happens when we click on "save" button
        // When we click on "save" button then it will update user password
        binding.updatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check internet connection
                if (!checkConnection())
                    return;

                // Performing Validation
                else if (!validateCurrentPassword() | !validateNewPassword())
                    return;

                // Code for calling the api and it will update user password
                else {

                    // Showing ProgressDialog box whenever "Save" button for updating password
                    showProgressDialog();

                    // Calling the updateUserPassword() method from ApiInterface class
                    // Here it is calling the api and get response back from the api in below onResponse() method
                    apiInterface.updateUserPassword(sharedPreferenceManager.getUser().getEmail(),
                            binding.currentPassword.getEditText().getText().toString(),
                            binding.newPassword.getEditText().getText().toString())
                            .enqueue(new Callback<UpdateUserPasswordModel>() {

                                // If response is received successfully after calling the api then this method is called
                                @Override
                                public void onResponse(Call<UpdateUserPasswordModel> call, Response<UpdateUserPasswordModel> response) {

                                    if (response.body() != null) {

                                        UpdateUserPasswordModel updateUserPasswordModel = response.body();

                                        if (response.isSuccessful()) {

                                            if (updateUserPasswordModel.getError().startsWith("200")) {

                                                // Closing the ProgressDialog box
                                                dismissProgressDialog();

                                                Toast.makeText(getContext(), updateUserPasswordModel.getMessage(), Toast.LENGTH_SHORT).show();

                                                // After password is updated successfully password field will be empty
                                                binding.currentPassword.getEditText().setText(null);
                                                binding.newPassword.getEditText().setText(null);

                                            }

                                            else {

                                                // Closing the ProgressDialog box
                                                dismissProgressDialog();

                                                Toast.makeText(getContext(), updateUserPasswordModel.getMessage(), Toast.LENGTH_SHORT).show();

                                            }

                                        }

                                        else {
                                            // Closing the ProgressDialog box
                                            dismissProgressDialog();

                                            Toast.makeText(getContext(), updateUserPasswordModel.getMessage(), Toast.LENGTH_SHORT).show();

                                        }


                                    }

                                    else {

                                        // Closing the ProgressDialog box
                                        dismissProgressDialog();

                                        Log.e("Response Error", "List is empty");

                                        Toast.makeText(getContext(), "Update password failed. Please update your password again", Toast.LENGTH_LONG).show();

                                    }

                                }

                                // If any error is occurred while receiving the response from the api then this method is called
                                @Override
                                public void onFailure(Call<UpdateUserPasswordModel> call, Throwable t) {

                                    // Closing the ProgressDialog box
                                    dismissProgressDialog();

                                    Log.e("Response Error", t.getLocalizedMessage());

                                    Toast.makeText(getContext(), "Update password failed. Check your connection", Toast.LENGTH_LONG).show();

                                }

                            });

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


    /*    Code for showing/dismissing progress dialog box    */


    // This method is used to show progress dialog
    private void showProgressDialog() {

        // Initialize progress dialog
        progressDialog = new Dialog(getActivity());

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

        String Username = binding.profileUsername.getEditText().getText().toString();
        //        ^           Beginning of the string
        //       [A-Za-z]     First character is not whitespace but it can be either capitals or small letter
        //       [A-Z a-z]    Other characters can be either whitespaces or capitals or small letters
        //        +           String contains at least one alphabetical char
        //        $           End of the string
        String checkUsername = "^[A-Za-z][A-Z a-z]+$";

        // If username is empty then following error message is shown to the user
        if(Username.isEmpty()) {
            binding.profileUsername.setError("Field cannot be empty");
            return false;
        }

        // If username length is greater than 20 then following error message is shown to the user
        else if(Username.length()>20) {
            binding.profileUsername.setError("Name is too large");
            return false;
        }

        // If username length is less than 5 then following error message is shown to the user
        else if(Username.length()<=5) {
            binding.profileUsername.setError("Name is too short");
            return false;
        }

        // If username does not match with above regex string then following error message is shown to the user
        else if(!Username.matches(checkUsername)) {
            binding.profileUsername.setError("Name must not contain numeric or special characters or first whitespace");
            return false;
        }

        // If all above validation is done successfully then error will be removed
        else {
            binding.profileUsername.setError(null);
            binding.profileUsername.setErrorEnabled(false);
            return true;
        }

    }

    // Email Validation
    private boolean validateEmail() {

        String Email = binding.profileEmail.getEditText().getText().toString();
        //       [a-z0-9._-]     Starting part of the email must be from a-z,0-9,.,_,-
        //       @               Then after next character @ is mandatory
        //       [a-z]           Then after next part of the email must be from a-z
        //       .               Then after next character . is mandatory
        //       [a-z]           Then after next part of the email must be from a-z
        String checkEmail = "[a-z0-9._-]+@[a-z]+\\.+[a-z]+";

        // If email is empty then following error message is shown to the user
        if(Email.isEmpty()) {
            binding.profileEmail.setError("Field cannot be empty");
            return false;
        }

        // If email does not match with above regex string then following error message is shown to the user
        else if(!Email.matches(checkEmail)) {
            binding.profileEmail.setError("Invalid Email!");
            return false;
        }

        // If all above validation is done successfully then error will be removed
        else {
            binding.profileEmail.setError(null);
            binding.profileEmail.setErrorEnabled(false);
            return true;
        }

    }

    // Phone Number Validation
    private boolean validatePhone() {

        String phoneNumber = binding.profilePhoneNo.getEditText().getText().toString();
        //        [1-9]          It matches first digit and checks if number lies between 1 to 9
        //        [0-9]          It matches other digits and checks if number lies between 0 to 9
        //        {9}            It specifies remaining
        String checkMobileNumber = "[1-9][0-9]{9}";

        // If phone number is empty then following error message is shown to the user
        if(phoneNumber.isEmpty()) {
            binding.profilePhoneNo.setError("Field cannot be empty");
            return false;
        }

        // If phone number is greater or less than 10 characters then following error message is shown to the user
        else if(phoneNumber.length()>10 || phoneNumber.length()<10) {
            binding.profilePhoneNo.setError("Mobile number must be of 10 characters");
            return false;
        }

        // If phone number does not match with above regex string then following error message is shown to the user
        else if(!phoneNumber.matches(checkMobileNumber)) {
            binding.profilePhoneNo.setError("Invalid mobile number");
            return false;
        }

        // If all above validation is done successfully then error will be removed
        else {
            binding.profilePhoneNo.setError(null);
            return true;
        }

    }

    // Current Password Validation
    private boolean validateCurrentPassword() {

        String Password = binding.currentPassword.getEditText().getText().toString();

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
            binding.currentPassword.setError("Field cannot be empty");
            return false;
        }

        // If password is less than 12 characters then following error message is shown to the user
        else if(Password.length() < 12) {
            binding.currentPassword.setError("Password must be of at least 12 characters");
            return false;
        }

        // If there is any whitespace in password then following error message is shown to the user
        else if(whiteSpace.matcher(Password).find()) {
            binding.currentPassword.setError("Whitespaces aren't allowed");
            return false;
        }

        // If password does not match with above regex string then following error message is shown to the user
        else if(!lowerCase.matcher(Password).find() || !upperCase.matcher(Password).find() ||
                !digitCase.matcher(Password).find() || !specialCharacters.matcher(Password).find()) {
            binding.currentPassword.setError("Password is too weak");
            return false;
        }

        // If all above validation is done successfully then error will be removed
        else {
            binding.currentPassword.setError(null);
            binding.currentPassword.setErrorEnabled(false);
            return true;
        }

    }

    // New Password Validation
    private boolean validateNewPassword() {

        String Password = binding.newPassword.getEditText().getText().toString();

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
            binding.newPassword.setError("Field cannot be empty");
            return false;
        }

        // If password is less than 12 characters then following error message is shown to the user
        else if(Password.length() < 12) {
            binding.newPassword.setError("Password must be of at least 12 characters");
            return false;
        }

        // If there is any whitespace in password then following error message is shown to the user
        else if(whiteSpace.matcher(Password).find()) {
            binding.newPassword.setError("Whitespaces aren't allowed");
            return false;
        }

        // If password does not match with above regex string then following error message is shown to the user
        else if(!lowerCase.matcher(Password).find() || !upperCase.matcher(Password).find() ||
                !digitCase.matcher(Password).find() || !specialCharacters.matcher(Password).find()) {
            binding.newPassword.setError("Password is too weak");
            return false;
        }

        // If all above validation is done successfully then error will be removed
        else {
            binding.newPassword.setError(null);
            binding.newPassword.setErrorEnabled(false);
            return true;
        }

    }

}