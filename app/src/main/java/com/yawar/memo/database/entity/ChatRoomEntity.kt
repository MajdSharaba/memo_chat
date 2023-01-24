package com.yawar.memo.database.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class ChatRoomEntity   (

    var username:String = "",

    var other_id:String = "",

    var last_message:String = "",

    var image:String = "",

    var isChecked:Boolean = false,

    var numberMessage: String? = "",

    @PrimaryKey
    var id:String = "",

    var state:String? = "",

    var num_msg:String = "",

    var inChat:Boolean = false,

    var user_token:String = "",

    var sn:String = "",

    var message_type:String? = "",

    var mstate:String? = "",

    var created_at:Long ? = 0,

    var isTyping:Boolean,

    var blocked_for:String?,

    var msg_sender:String? = "",

    var phone:String? = "") {




}