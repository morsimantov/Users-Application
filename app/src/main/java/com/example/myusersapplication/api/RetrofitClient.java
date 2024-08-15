package com.example.myusersapplication.api;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String base_url = "https://reqres.in/api/";
    private static RetrofitClient instance;
    // retrofit object
    private Retrofit retrofit;

    public RetrofitClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public WebServiceApi getApi() {
        return retrofit.create(WebServiceApi.class);
    }
}
