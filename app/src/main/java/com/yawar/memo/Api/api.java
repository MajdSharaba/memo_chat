package com.yawar.memo.Api;

import com.yawar.memo.model.ChatRoomRespone;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface api {

    @GET("APIS/mychat.php")
//endpoint

//    Single<String> getChatRoom(@Query("user_id") String user_id);
    Single<ChatRoomRespone> getChatRoom(@Query("user_id") String user_id);


    @FormUrlEncoded

    @POST("myblocklist")
//endpoint

    Single<String> getBlockKist(@Field("my_id") String user_id);

    @FormUrlEncoded

    @POST("APIS/signup.php")
//endpoint

    Single<String> getSpecialNumbers(@Field("phone") String phoneNumber);

    @FormUrlEncoded

    @POST("messagesbyusers")
//endpoint

    Single<String> getChatMessgeHistory(@Field("sender_id") String my_id
            , @Field("reciver_id") String anthor_user_id);

    @FormUrlEncoded

    @POST("addtoken")
//endpoint
    Single<String> sendFcmToken(@Field("users_id") String my_id
            , @Field("token") String fcmToken);


    @FormUrlEncoded

    @POST("archivechat")
//endpoint
    Single<String> addToArchived(@Field("my_id") String my_id
            , @Field("your_id") String anthor_user_id);

    @FormUrlEncoded

    @POST("deletearchive")
//endpoint
    Single<String> removeFromArchived(@Field("my_id") String my_id
            , @Field("your_id") String anthor_user_id);


    @FormUrlEncoded

    @POST("deleteconversation")
//endpoint
    Single<String> deleteChatRoom(@Field("my_id") String my_id
            , @Field("your_id") String anthor_user_id);

    @FormUrlEncoded

    @POST("deletemessage2")
//endpoint
    Single<String> deleteMessage(@Field("message_id") String message_id
            , @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("getmedia")
//endpoint
    Single<String> getMedia(@Field("sender_id") String sender_id, @Field("reciver_id") String user_id);

    @FormUrlEncoded
    @POST("addtoblock")
//endpoint
    Single<String> blockUser(@Field("my_id") String my_id, @Field("user_id") String anthor_user_id);

    @FormUrlEncoded
    @POST("deleteblock")
//endpoint
    Single<String> unBlockUser(@Field("my_id") String my_id, @Field("user_id") String anthor_user_id);

    @FormUrlEncoded
    @POST("APIS/search_for_user.php")
//endpoint
    Single<String> search(@Field("sn") String search_parameters, @Field("page") String page, @Field("my_id") String my_id);

    @FormUrlEncoded
    @POST("mycalls")
//endpoint
    Single<String> getMyCalls(@Field("my_id") String my_id);

    @Multipart
    @POST("uploadImgChat")
    Call<String> fileUpload(

            @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("profile")
    Single<String> getUserInformation(@Field("id") String anthor_user_id);

}