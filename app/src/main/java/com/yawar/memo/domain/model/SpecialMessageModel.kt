package com.yawar.memo.domain.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
class SpecialMessageModel (
    var  fullname:String  = "",

    var  other_id:String  = "",

    var  sender_id:String  = "",

    var  message:String  = "",

    var  profile_image:String  = "",

    var  message_id:String  = "",

    var  state:String  = "",

    var  user_token:String  = "",

    var  sn:String  = "",

    var  message_type:String  = "",

    var  created_at:String? ,

    var  orginalName:String ,

    var  blocked_for:String?,

    var isDownlod : Boolean = false,

    var  isChecked:Boolean = false,


) :Cloneable, Parcelable {

    public override fun  clone() : ChatMessage {

        var clone : ChatMessage
        try {
            clone = super.clone() as ChatMessage
        } catch ( e : CloneNotSupportedException) {
            throw  RuntimeException(e); //should not happen
        }

        return clone;
    }

}