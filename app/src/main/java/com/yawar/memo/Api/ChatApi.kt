package com.yawar.memo.Api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.model.ChatRoomRespone
import io.reactivex.Single
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

interface ChatApi {
    @GET("APIS/mychat.php")
    fun getChatRoom(@Query("user_id") user_id: String?): Deferred<ChatRoomRespone>


    @FormUrlEncoded
    @POST("archivechat")
    fun addToArchived(
        @Field("my_id") my_id: String?,
        @Field("your_id") anthor_user_id: String?,
    ): Deferred<String>

    @FormUrlEncoded
    @POST("deletearchive")
    fun removeFromArchived(
        @Field("my_id") my_id: String?,
        @Field("your_id") anthor_user_id: String?,
    ): Deferred<String>


    @FormUrlEncoded
    @POST("deleteconversation")
    fun  deleteChatRoom(
        @Field("my_id") my_id: String?,
        @Field("your_id") anthor_user_id: String?,
    ): Deferred<String?>?


    @FormUrlEncoded
    @POST("messagesbyusers")
    fun getChatMessgeHistory(
        @Field("sender_id") my_id: String?,
        @Field("reciver_id") anthor_user_id: String?,
    ): Deferred<String?>?

    @FormUrlEncoded
    @POST("deletemessage2")
    fun deleteMessage(
        @Field("message_id") message_id: String?,
        @Field("user_id") user_id: String?,
    ): Deferred<String?>?


    @FormUrlEncoded
    @POST("myblocklist")
    fun getBlockKist(@Field("my_id") user_id: String?): Deferred<String?>?


    @FormUrlEncoded
    @POST("addtoblock")
    fun blockUser(
        @Field("my_id") my_id: String?,
        @Field("user_id") anthor_user_id: String?,
    ): Deferred<String?>?

    @FormUrlEncoded
    @POST("deleteblock")
    fun  //endpoint
            unBlockUser(
        @Field("my_id") my_id: String?,
        @Field("user_id") anthor_user_id: String?,
    ): Deferred<String?>?


    @FormUrlEncoded
    @POST("APIS/signup.php")
    fun  getSpecialNumbers (@Field("phone") phoneNumber: String?): Deferred<String?>?


    @FormUrlEncoded
    @POST("addtoken")
    fun sendFcmToken(
        @Field("users_id") my_id: String?, @Field("token") fcmToken: String?
    ): Deferred<String?>?

    @FormUrlEncoded
    @POST("getmedia")
    fun getMedia(
        @Field("sender_id") sender_id: String?,
        @Field("reciver_id") user_id: String?
    ): Deferred<String?>?

    @FormUrlEncoded
    @POST("profile")
    fun getUserInformation(@Field("id") anthor_user_id: String?): Deferred<String?>?


    @FormUrlEncoded
    @POST("mycalls")
    fun getMyCalls(@Field("my_id") my_id: String?): Deferred<String?>?

    @FormUrlEncoded
    @POST("APIS/search_for_user.php")
    fun search(
        @Field("sn") search_parameters: String?,
        @Field("page") page: String?,
        @Field("my_id") my_id: String?
    ): Deferred<String?>?


    @FormUrlEncoded
    @POST("APIS/mycontact.php")
    fun sendContactNumber(
        @Field("data") data: String?,
        @Field("id") my_id: String?,
    ): Deferred<String?>?

    

}


    private val retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(AllConstants.base_url_final)
        .build()


object GdgApi {
val apiService: ChatApi = retrofit.create(ChatApi::class.java)

}
