package com.example.sslimplementation;

import android.content.Context;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class ApiClient {

    public static final String BASE_URL = "";

    private static Retrofit retrofit = null;
    private static Retrofit retrofitSocial = null;
    private static Retrofit retrofitGoogle = null;


    public static Retrofit getClient(Context context) {


        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(SelfSigningClientBuilder.createClient(context))
                    .baseUrl(Constants.BASE_URL)

                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }



}
