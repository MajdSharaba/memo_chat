package com.yawar.memo.network.networkModel.chatRoomModel

import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatRoomModelDto(
    @Json(name = "username")
    var  username:String  = "",

    @Json(name = "other_id")
    var  other_id:String  = "",

    @Json(name = "last_message")
    var  last_message:String  = "",

    @Json(name = "image")
    var  image:String  = "",

    @Json(name = "isChecked")
    var isChecked:Boolean  = false,

    @Json(name = "numberMessage")
    var  numberMessage:String?  = "",

    @Json(name = "id")
    var  id:String  = "",

    @Json(name = "state")
    var  state:String  = "",

    @Json(name = "num_msg")
    var  num_msg:String  = "",

    @Json(name = "inChat")
    var inChat:Boolean  = false,

    @Json(name = "user_token")
    var  user_token:String  = "",

    @Json(name = "sn")
    var  sn:String  = "",

    @Json(name = "message_type")
    var  message_type:String  = "",

    @Json(name = "mstate")
    var  mstate:String  = "",

    @Json(name = "created_at")
    var  created_at:String? ,

    @Json(name = "isTyping")
    var isTyping:Boolean  = false,

    @Json(name = "blocked_for")
    var  blocked_for:String ,

    @Json(name = "msg_sender")
    var  msg_sender:String  = "",

    @Json(name = "phone")
    var  phone:String  = "",
    )

/**
 * Convert Network results to database objects
 */
//fun ChatRoomNetworkModel.asDomainModel(): List<ChatRoomModel> {
//    return chatRoomModels.map {
//        ChatRoomModel(
//            username = ,
//
//            other_id = "",
//
//            last_message = "",
//
//            image = "",
//
//            isChecked = false,
//
//            numberMessage = "",
//
//            id = "",
//
//            state = "",
//
//            num_msg  = "",
//
//            inChat = false,
//
//            user_token  = "",
//
//            sn = "",
//
//            message_type = "",
//
//            mstate = "",
//
//            created_at = "",
//
//            isTyping = false,
//
//            blocked_for =  ,
//
//            msg_sender  =,
//
//            phone  = ,
//        )
//            )
//    }
//}

//fun NetworkVideoContainer.asDatabaseModel(): Array<DatabaseVideo> {
//    return videos.map {
//        DatabaseVideo(
//            title = it.title,
//            description = it.description,
//            url = it.url,
//            updated = it.updated,
//            thumbnail = it.thumbnail)
//    }.toTypedArray()
//}
