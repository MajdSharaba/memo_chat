package com.yawar.memo.database.entity.ChatMessageEntity

import com.yawar.memo.domain.model.ChatMessage
import com.yawar.memo.domain.model.util.DomainMapper

class ChatMessageEntityMapper : DomainMapper<ChatMessageEntity, ChatMessage> {
    override fun mapToDominModel(model: ChatMessageEntity): ChatMessage {
        return  ChatMessage(
           messageId  = model.messageId,

//            id = model.id.toString(),

          isMe  = model.isMe,

          message = model.message,

          image = model.image,

          type = model.type,

          senderId = model.senderId,

          recivedId = model.recivedId,

          dateTime = model.dateTime.toString(),

          state = model.state,

          fileName = model.fileName?:"",

          isUpdate = model.isUpdate,

          isDownload = model.isDownload,

          upload = model.upload,

          isChecked = model.isChecked
        )
    }

    override fun mapFromDominModel(domainModel: ChatMessage): ChatMessageEntity {
        return  ChatMessageEntity(

            messageId  = domainModel.messageId,

//            id = domainModel.id.toLong(),

            isMe  = domainModel.isMe,

            message = domainModel.message,

            image = domainModel.image,

            type = domainModel.type,

            senderId = domainModel.senderId,

            recivedId = domainModel.recivedId,

            dateTime = domainModel.dateTime.toLong(),

            state = domainModel.state,

            fileName = domainModel.fileName?:"",

            isUpdate = domainModel.isUpdate,

            isDownload = domainModel.isDownload,

            upload = domainModel.upload,

            isChecked = domainModel.isChecked
        )
    }

    fun toDomainList(initial: List<ChatMessageEntity?>?) : List<ChatMessage?>?{
        return initial?.map {  mapToDominModel(it!!)  }

    }

    fun fromDomainList(initial : ArrayList<ChatMessage>) : Array<ChatMessageEntity>{
        return initial?.map {  mapFromDominModel(it!!)  }?.toTypedArray()!!


    }
}