package com.yawar.memo.Api;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface api {

    @GET("APIS/mychat.php")//endpoint

    Single<String> getChatRoom(@Query("user_id") String user_id);

    @FormUrlEncoded

    @POST("APIS/allblock.php")//endpoint

    Single<String> getBlockKist(@Field("my_id") String user_id);

    @FormUrlEncoded

    @POST("APIS/signup.php")//endpoint

    Single<String> getSpecialNumbers(@Field("phone") String phoneNumber);

    @FormUrlEncoded

    @POST("messagesbyusers")//endpoint

    Single<String> getChatMessgeHistory(@Field("sender_id") String my_id
            ,@Field("reciver_id") String anthor_user_id);


}
