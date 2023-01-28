package com.yawar.memo.network.networkModel.chatRoomModel

import com.squareup.moshi.Json
import com.yawar.memo.network.networkModel.chatRoomModel.ChatRoomModelDto
import java.util.*

data class ChatRoomRespone(

    @Json(name = "data")
    var data: ArrayList<ChatRoomModelDto?>? = null,

    )
