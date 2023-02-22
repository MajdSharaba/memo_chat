package com.yawar.memo.network.networkModel.chatMessageModel

import com.yawar.memo.BaseApp
import com.yawar.memo.database.entity.ChatMessageEntity.ChatMessageEntity
import com.yawar.memo.domain.model.ChatMessage
import com.yawar.memo.domain.model.util.DomainMapper
import com.yawar.memo.domain.model.util.EntityMapper

class ChatMessageDtoMapper : DomainMapper<ChatMessageDto, ChatMessage>,
    EntityMapper<ChatMessageDto, ChatMessageEntity> {
    override fun mapToDominModel(model: ChatMessageDto): ChatMessage {

        return ChatMessage(
            messageId  = model.message_id,

//            id = model.id,

            isMe  =  model.sender_id== BaseApp.instance?.classSharedPreferences?.user?.userId,

            message = model.message,

            image = model.message,

            type = model.message_type,

            senderId = model.sender_id,

            recivedId = model.reciver_id,

            dateTime = model.created_at,

            state = model.state,

            fileName = model.orginalName?:"",

            isUpdate = model.edited,

            isDownload = false,

            upload = false,

            isChecked = false
        )
    }

    override fun mapFromDominModel(domainModel: ChatMessage): ChatMessageDto {
        TODO("Not yet implemented")
    }

    fun toDomainList(initial : ArrayList<ChatMessageDto?>?) : List<ChatMessage?>?{
        return initial?.map {  mapToDominModel(it!!)  }

    }
    ////////////////to Entity

    override fun mapToEntityModel(model: ChatMessageDto): ChatMessageEntity {
        return ChatMessageEntity(
            messageId  = model.message_id,

//            id = model.id.toInt(),

            isMe  =  model.sender_id== BaseApp.instance?.classSharedPreferences?.user?.userId,

            message = model.message,

            image = model.message,

            type = model.message_type,

            senderId = model.sender_id,

            recivedId = model.reciver_id,

            dateTime = model.created_at.toLong(),

            state = model.state,

            fileName = model.orginalName?:"",

            isUpdate = model.edited,

            isDownload = false,

            upload = false,

            isChecked = false
        )
    }

    override fun mapFromEntityModel(entityModel: ChatMessageEntity): ChatMessageDto {
        TODO("Not yet implemented")
    }

    fun toEntityList(initial : List<ChatMessageDto?>?) : Array<ChatMessageEntity>{
        return initial?.map {  mapToEntityModel(it!!)  }?.toTypedArray()!!

    }
}