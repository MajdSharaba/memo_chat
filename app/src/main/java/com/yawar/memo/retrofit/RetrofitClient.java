package com.yawar.memo.retrofit;

import com.squareup.moshi.Moshi;
import com.yawar.memo.Api.api;
import com.yawar.memo.constant.AllConstants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient { //world wide cases

    //private  static  final String base_url = "https://still-lake-87096.herokuapp.com/" ;//base url
//    private static final String base_url = "https://memoback.herokuapp.com/";//base url
    private static RetrofitClient instance;
    private final Retrofit retrofit; //retrofit object
    final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(50, TimeUnit.SECONDS)
            .writeTimeout(50, TimeUnit.SECONDS)
            .readTimeout(50, TimeUnit.SECONDS)
            .build();
//    private Moshi moshi = new Moshi.Builder()
//            .add(new KotlinJsonAdapterFactory())
//            .build();
    private RetrofitClient(String baseUrl) { //constructor
        retrofit = new Retrofit.Builder().baseUrl(baseUrl).
                client(okHttpClient).
                addConverterFactory(ScalarsConverterFactory.create())

//                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create()).build();

    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient(AllConstants.base_url_final);
        }
        return instance;

    }

    public api getapi() {
        return retrofit.create(api.class);
    }
}
