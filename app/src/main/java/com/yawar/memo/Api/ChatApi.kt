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
