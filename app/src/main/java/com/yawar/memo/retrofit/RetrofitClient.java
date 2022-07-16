package com.yawar.memo.retrofit;

import com.yawar.memo.Api.api;
import com.yawar.memo.constant.AllConstants;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient { //world wide cases

    //private  static  final String base_url = "https://still-lake-87096.herokuapp.com/" ;//base url
//    private static final String base_url = "https://memoback.herokuapp.com/";//base url
    private static RetrofitClient instance;
    private final Retrofit retrofit; //retrofit object

    private RetrofitClient(String baseUrl) { //constructor
        retrofit = new Retrofit.Builder().baseUrl(baseUrl).
                addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create()).build();

    }

    public static synchronized RetrofitClient getInstance(String baseUrl) {
//        if (instance == null) {
            instance = new RetrofitClient(baseUrl);
//        }
        return instance;

    }

    public api getapi() {
        return retrofit.create(api.class);
    }
}
