package com.example.myusersapplication.api;
import com.example.myusersapplication.models.GetUsersResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WebServiceApi {

    @GET("users")
    Call<GetUsersResponse> getAllUsers(@Query("page") int page);
}