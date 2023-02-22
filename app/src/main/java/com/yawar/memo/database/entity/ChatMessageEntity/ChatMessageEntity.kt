package com.yawar.memo.database.entity.ChatMessageEntity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class ChatMessageEntity
    (
    @PrimaryKey(autoGenerate = false)
    var  messageId:String = "",


//    var  id: Long = 0,

    var  isMe:Boolean = false,

    var  message:String = "",

    var  image:String = "",

    var  type:String= "",

    var  senderId:String= "",

    var  recivedId:String = "",

    var  dateTime: Long = 0,

    var  state:String = "",

    var  fileName:String= "",

    var  isUpdate:String="",

    var  isDownload:Boolean = false,

    var  upload:Boolean = false,

    var  isChecked:Boolean = false
)