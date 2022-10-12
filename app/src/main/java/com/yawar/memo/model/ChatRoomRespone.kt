package com.yawar.memo.model

import com.squareup.moshi.Json
import java.util.*

data class ChatRoomRespone(

    @Json(name = "data")
    var data: ArrayList<ChatRoomModel?>? = null,

    )
