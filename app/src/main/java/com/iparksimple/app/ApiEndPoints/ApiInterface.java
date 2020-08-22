package com.iparksimple.app.ApiEndPoints;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiInterface {


    @FormUrlEncoded
    @POST("auth/register")
    Call<SignUpResult> SignUp(@Field("email") String email, @Field("password")String password, @Field("phone")String Phone);

    @FormUrlEncoded
    @POST("auth/login")
    Call<LoginResult> Login(@Field("username") String username, @Field("password")String password);

    @FormUrlEncoded
    @POST("auth/forgot")
    Call<ForgotPasswordResult>ForgotPassword(@Field("username")String Username);

    @FormUrlEncoded
    @POST("auth/reset")
    Call<ResetPasswordResult>ResetPassword(HashMap<String,String>map);

    @GET("customer/profile")
    Call<ProfileResult>Getprofile(@Header("authorization")String authorization);

    @FormUrlEncoded
    @POST("customer/update")
    Call<UpdateProfileResult>Update_profile(@Header("authorization")String token, @FieldMap HashMap<String,String>map);

    @GET("auth/logout")
    Call<LogoutResult>Logout();

    @GET("parking/map_search")
    Call<ParkingLotsListResult>GetHomeData(@Header("latitude") String Latitude, @Header("longitude")String Longitude, @Header("type")String Type, @Header("Distance")String Distance);

    @GET("lots?id=19")
    Call<LotsDetailResult>Lots_details(@Field("id")String id);


    @FormUrlEncoded
    @POST("vehicle/add_vehicle")
    Call<AddVehicleResult>Add_vehicle(@Header("Authorization")String token, @FieldMap HashMap<String,String>map);


    @GET("vehicle/vehicle_list")
    Call<VehicleListResult>VehicleList(@Header("authorization")String Token);








}
