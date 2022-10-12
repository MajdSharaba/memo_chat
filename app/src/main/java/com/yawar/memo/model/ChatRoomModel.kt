package com.yawar.memo.model

import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
 data class ChatRoomModel (

    @Json(name = "username")
    var  username:String = "",

    @Json(name = "other_id")
    var  other_id:String = "",

    @Json(name = "last_message")
    var  last_message:String = "",

    @Json(name = "image")
    var  image:String = "",

    @Json(name = "isChecked")
    var isChecked:Boolean = false,

    @Json(name = "numberMessage")
    var  numberMessage:String = "",

    @Json(name = "id")
    var  id:String = "",

    @Json(name = "state")
    var  state:String = "",

    @Json(name = "num_msg")
    var  num_msg:String = "",

    @Json(name = "inChat")
    var inChat:Boolean = false,

    @Json(name = "user_token")
    var  user_token:String = "",

    @Json(name = "sn")
    var  sn:String = "",

    @Json(name = "message_type")
    var  message_type:String = "",

    @Json(name = "mstate")
    var  mstate:String = "",

    @Json(name = "created_at")
    var  created_at:String = "",

    @Json(name = "isTyping")
    var isTyping:Boolean = false,

    @Json(name = "blocked_for")
    var  blocked_for:String? ,

    @Json(name = "msg_sender")
    var  msg_sender:String = "",

    @Json(name = "phone")
    var  phone:String = "",
        ): Cloneable {

    public override fun  clone() :ChatRoomModel {

        var clone :ChatRoomModel
        try {
            clone = super.clone() as ChatRoomModel
        } catch ( e : CloneNotSupportedException) {
            throw  RuntimeException(e); //should not happen
        }

        return clone;
    }
}