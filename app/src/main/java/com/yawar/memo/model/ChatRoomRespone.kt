package com.yawar.memo.model

import com.squareup.moshi.Json
import com.yawar.memo.network.networkModel.ChatRoomModelDto
import java.util.*

data class ChatRoomRespone(

    @Json(name = "data")
    var data: ArrayList<ChatRoomModelDto?>? = null,

    )
