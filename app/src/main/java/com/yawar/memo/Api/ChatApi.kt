package com.yawar.memo.Api

import com.android.volley.NetworkResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.model.ChatRoomRespone
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.components.SingletonComponent
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
    fun  getSpecialNumbers (@Field("uuid") uuid: String?): Deferred<String?>?


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

    @FormUrlEncoded
    @POST("googlesignup")
    fun register(
        @Field("email") email: String?,
        @Field("image") img: String?,
        @Field("first_name") firstName: String?,
        @Field("last_name") lastName: String?,
        @Field("sn") sn: String?,
        @Field("phone") phone: String?,
        @Field("uuid") uuid: String?,
    ): Deferred<String?>?


    @FormUrlEncoded
    @POST("upadteImageProfile")
    fun updateImage(
        @Field("id") id: String?,
        @Field("image") img: String?,
    ): Deferred<String?>?


    @FormUrlEncoded
    @POST("APIS/updateprofile.php")
    fun updateProfile(
        @Field("first_name") firstName: String?,
        @Field("last_name") lastName: String?,
        @Field("id") userId: String?,

        ): Deferred<String?>?


    @FormUrlEncoded
    @POST("APIS/delete_my_account.php")
    fun deleteAccount(
        @Field("sn") sn: String?,
        @Field("user_id") my_id: String?,
        ): Deferred<String?>?


    @FormUrlEncoded
    @POST("get_my_messages")
    fun getUnRecivedMessages(
        @Field("user_id") id: String?,
    ): Deferred<String?>?



}


    private val retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(AllConstants.base_url_final)
        .build()

//fun retrofit(string: String): Retrofit {
//         val retrofit = Retrofit.Builder()
//        .addConverterFactory(ScalarsConverterFactory.create())
//        .addCallAdapterFactory(CoroutineCallAdapterFactory())
//        .addConverterFactory(GsonConverterFactory.create())
//        .baseUrl(string)
//        .build()
//
//    return retrofit
//}

//////////
// public  object  GdgApi {
////   public class GdgApi(string: String) {
//
//   public val apiService: ChatApi = retrofit.create(ChatApi::class.java)
////    val apiService: ChatApi = retrofit(string).create(ChatApi::class.java)
//
//
//
//
//}
///////////

@Module
@InstallIn(SingletonComponent::class)
object MovieModule {
    @Provides
    fun provideMovieService(): ChatApi
            = retrofit.create(ChatApi::class.java)
}


