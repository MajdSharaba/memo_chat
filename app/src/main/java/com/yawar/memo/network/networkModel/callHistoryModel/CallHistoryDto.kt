package com.yawar.memo.network.networkModel.callHistoryModel

import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
class CallHistoryDto (
    @Json(name = "id")
    var id: String = "",

    @Json(name = "first_name")
    var first_name: String = "",

    @Json(name = "last_name")
    var last_name: String = "",

    @Json(name = "caller")
    var caller: String? = "",

    @Json(name = "profile_image")
    var profile_image: String = "",

    @Json(name = "call_type")
    var call_type: String  = "",

    @Json(name = "answer")
    var answer: String  = "",

    @Json(name = "call_state")
    var call_state: String = "",

    @Json(name = "duration")
    var duration: String? = "",

    @Json(name = "call_time")
    var call_time: String? = "0",

){
}