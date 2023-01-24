package com.yawar.memo.model

import kotlinx.android.parcel.Parcelize

@Parcelize
 data class ChatRoomModel(

    var username:String = "",

    var other_id:String = "",

    var last_message:String = "",

    var image:String = "",

    var isChecked:Boolean = false,

    var numberMessage: String? = "",

    var id:String = "",

    var state:String? = "",

    var num_msg:String = "",

    var inChat:Boolean = false,

    var user_token:String = "",

    var sn:String = "",

    var message_type:String? = "",

    var mstate:String? = "",

    var created_at:String? = "",

    var isTyping:Boolean = false,

    var blocked_for:String?,

    var msg_sender:String? = "",

    var phone:String? = "",
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