package com.yawar.memo.retrofit

import com.google.android.datatransport.runtime.dagger.internal.DoubleCheck.lazy
import com.yawar.memo.Api.ChatApi
import com.yawar.memo.constant.AllConstants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//import com.yawar.memo.Api.api
//import com.yawar.memo.constant.AllConstants
//import okhttp3.OkHttpClient
//import retrofit2.Retrofit
//import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
//import retrofit2.converter.gson.GsonConverterFactory
//import retrofit2.converter.scalars.ScalarsConverterFactory
//import java.util.concurrent.TimeUnit
////
////class RetrofitClient private constructor() {
////    private val retrofit //retrofit object
////            : Retrofit
//    val okHttpClient = OkHttpClient.Builder()
//        .connectTimeout(50, TimeUnit.SECONDS)
//        .writeTimeout(50, TimeUnit.SECONDS)
//        .readTimeout(50, TimeUnit.SECONDS)
//        .build()
//
//  private  val retrofit = Retrofit.Builder().baseUrl(AllConstants.base_url_final).client(okHttpClient)
//    .addConverterFactory(ScalarsConverterFactory.create())
//    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//    .addConverterFactory(GsonConverterFactory.create()).build()
//
////    fun getapi(): api {
////        return retrofit.create(api::class.java)
////    }
//
// object ChatApi {
//    fun  getapi(): api {
//        return retrofit.create(api::class.java)
//    }
//}
//
//
//
//
////    companion object {
////        //world wide cases
////        //private  static  final String base_url = "https://still-lake-87096.herokuapp.com/" ;//base url
////        //    private static final String base_url = "https://memoback.herokuapp.com/";//base url
////        @get:Synchronized
////        var instance: RetrofitClient? = null
////            get() {
////                if (field == null) {
////                    field = RetrofitClient(AllConstants.base_url_final)
////                }
////                return field
////            }
////            private set
////    }
//
//
//object RetrofitClient {
//
//
//    private fun getRetrofit(): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl(AllConstants.base_url_final)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }
//
//    val apiService: ChatApi = getRetrofit().create(ChatApi::class.java)
//}

