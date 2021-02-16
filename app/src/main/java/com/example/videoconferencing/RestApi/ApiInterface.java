package com.example.videoconferencing.RestApi;

import com.example.videoconferencing.RestApi.PojoModels.DeleteUserModel;
import com.example.videoconferencing.RestApi.PojoModels.SignInModel;
import com.example.videoconferencing.RestApi.PojoModels.SignUpModel;
import com.example.videoconferencing.RestApi.PojoModels.UpdateUserPasswordModel;
import com.example.videoconferencing.RestApi.PojoModels.UpdateUserProfileModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    // Calling sign up api
    @FormUrlEncoded
    @POST("signup.php")
    Call<SignUpModel> signUpUser (
            @Field("username") String username,
            @Field("email") String email,
            @Field("phoneno") String phoneNo,
            @Field("password") String password
    );

    // Calling sign in api
    @FormUrlEncoded
    @POST("signin.php")
    Call<SignInModel> signInUser (
            @Field("email") String email,
            @Field("password") String password
    );


    // Calling update user profile api
    @FormUrlEncoded
    @POST("updateuser.php")
    Call<UpdateUserProfileModel> updateUserProfile (
            @Query("id") int id,
            @Field("username") String username,
            @Field("email") String email,
            @Field("phoneno") String phoneNo
    );


    // Calling update user password profile api
    @FormUrlEncoded
    @POST("updatepassword.php")
    Call<UpdateUserPasswordModel> updateUserPassword (
            @Field("email") String email,
            @Field("currentpassword") String currentPassword,
            @Field("newpassword") String newPassword
    );


    // Calling delete user api
    @POST("deleteuser.php")
    Call<DeleteUserModel> deleteUser (
            @Query("id") int id
    );

}
