package com.yawar.memo.network.networkModel.specialMessageModel

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
class SpecialMessageDto (
    @Json(name = "fullname")
    var  fullname:String  = "",

    @Json(name = "other_id")
    var  other_id:String  = "",

    @Json(name = "message")
    var  message:String  = "",

    @Json(name = "profile_image")
    var  profile_image:String  = "",

    @Json(name = "message_id")
    var  message_id:String  = "",

    @Json(name = "state")
    var  state:String  = "",

    @Json(name = "user_token")
    var  user_token:String  = "",

    @Json(name = "sn")
    var  sn:String  = "",

    @Json(name = "message_type")
    var  message_type:String  = "",

    @Json(name = "created_at")
    var  created_at:String? = "0" ,

    @Json(name = "orginalName")
    var  orginalName:String? ,

    @Json(name = "blocked_for")
    var  blocked_for:String? ,

    @Json(name = "sender_id")
    var  sender_id:String  = "",




) : Parcelable


