package com.yawar.memo.network.networkModel.chatMessageModel

import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize


@Parcelize
class ChatMessageDto(

    @Json(name = "message_id")
    var  message_id:String = "",

//    @Json(name = "id")
//    var  id:String = "",

    @Json(name = "message")
    var  message:String = "",

    @Json(name = "message_type")
    var  message_type:String= "",

    @Json(name = "sender_id")
    var  sender_id:String= "",

    @Json(name = "reciver_id")
    var  reciver_id:String = "",

    @Json(name = "created_at")
    var  created_at:String= "",

    @Json(name = "state")
    var  state:String = "",

    @Json(name = "orginalName")
    var  orginalName:String= "",

    @Json(name = "edited")
    var  edited:String="",
)