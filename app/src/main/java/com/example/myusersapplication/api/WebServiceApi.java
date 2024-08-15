package com.example.myusersapplication.api;

import com.example.myusersapplication.models.CreateUserResponse;
import com.example.myusersapplication.models.UpdateUserResponse;
import com.example.myusersapplication.models.GetUserResponse;
import com.example.myusersapplication.models.UserRequest;
import com.example.myusersapplication.models.GetUsersResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WebServiceApi {

    @GET("users")
    Call<GetUsersResponse> getAllUsers(@Query("page") int page);

    @GET("users/{id}")
    Call<GetUserResponse> getUser(@Path("id") int id);

    @POST("users")
    Call<CreateUserResponse> createUser(@Body UserRequest request);

    @PUT("users/{id}")
    Call<UpdateUserResponse> updateUser(@Path("id") int id, @Body UserRequest request);

    @DELETE("users/{id}")
    Call<Void> deleteUser(@Path("id") int id);
}
