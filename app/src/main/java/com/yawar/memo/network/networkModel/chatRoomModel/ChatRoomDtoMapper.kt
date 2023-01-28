package com.yawar.memo.network.networkModel.chatRoomModel

import com.yawar.memo.database.entity.chatRoomEntity.ChatRoomEntity
import com.yawar.memo.domain.model.ChatRoomModel
import com.yawar.memo.domain.model.util.chatRoomUtil.DomainMapper
import com.yawar.memo.domain.model.util.chatRoomUtil.EntityMapper

class ChatRoomDtoMapper : DomainMapper<ChatRoomModelDto, ChatRoomModel>,
    EntityMapper<ChatRoomModelDto, ChatRoomEntity> {

    override fun mapToDominModel(model: ChatRoomModelDto): ChatRoomModel {
        return ChatRoomModel(

          username = model.username,

          other_id = model.other_id,

          last_message = model.last_message,

          image = model.image,

          isChecked = model.isChecked,

          numberMessage = model.numberMessage,

          id = model.id,

          state = model.state,

          num_msg = model.num_msg,

          inChat = model.inChat,

          user_token = model.user_token,

          sn = model.sn,

          message_type = model.message_type,

          mstate = model.mstate,

          created_at = model.created_at,

          isTyping = model.isTyping,

          blocked_for = model.blocked_for,

          msg_sender = model.msg_sender,

          phone = model.phone

        )
    }

    override fun mapFromDominModel(domainModel: ChatRoomModel): ChatRoomModelDto {
        return ChatRoomModelDto(
            username = domainModel.username!!,

            other_id = domainModel.other_id!!,

            last_message = domainModel.last_message!!,

            image = domainModel.image!!,

            isChecked = domainModel.isChecked!!,

            numberMessage = domainModel.numberMessage!!,

            id = domainModel.id!!,

            state = domainModel.state.toString(),

            num_msg = domainModel.num_msg!!,

            inChat = domainModel.inChat!!,

            user_token = domainModel.user_token!!,

            sn = domainModel.sn!!,

            message_type = domainModel.message_type!!,

            mstate = domainModel.mstate!!,

            created_at = domainModel.created_at!!,

            isTyping = domainModel.isTyping!!,

            blocked_for = domainModel.blocked_for.toString(),

            msg_sender  = domainModel.msg_sender!!,

            phone  = domainModel.phone!!

        )
    }

    fun toDomainList(initial : ArrayList<ChatRoomModelDto?>?) : List<ChatRoomModel?>?{
        return initial?.map {  mapToDominModel(it!!)  }

    }

    fun fromDomainList(initial : ArrayList<ChatRoomModel?>?) : List<ChatRoomModelDto?>?{
        return initial?.map { it?.let { it1 -> mapFromDominModel(it1) } }

    }
//////for Entity
    override fun mapToEntityModel(model: ChatRoomModelDto): ChatRoomEntity {
    return ChatRoomEntity(

        username = model.username,

        other_id = model.other_id,

        last_message = model.last_message,

        image = model.image,

        isChecked = model.isChecked,

        numberMessage = model.numberMessage,

        id = model.id,

        state = model.state,

        num_msg = model.num_msg,

        inChat = model.inChat,

        user_token = model.user_token,

        sn = model.sn,

        message_type = model.message_type,

        mstate = model.mstate,

        created_at = model.created_at?.toLong(),

        isTyping = model.isTyping,

        blocked_for = model.blocked_for,

        msg_sender = model.msg_sender,

        phone = model.phone

    )    }

    override fun mapFromEntityModel(entityModel: ChatRoomEntity): ChatRoomModelDto {
        return ChatRoomModelDto(

            username = entityModel.username,

            other_id = entityModel.other_id,

            last_message = entityModel.last_message,

            image = entityModel.image,

            isChecked = entityModel.isChecked,

            numberMessage = entityModel.numberMessage,

            id = entityModel.id,

            state = entityModel.state!!,

            num_msg = entityModel.num_msg,

            inChat = entityModel.inChat,

            user_token = entityModel.user_token,

            sn = entityModel.sn,

            message_type = entityModel.message_type!!,

            mstate = entityModel.mstate!!,

            created_at = entityModel.created_at!!.toString(),

            isTyping = entityModel.isTyping,

            blocked_for = entityModel.blocked_for!!,

            msg_sender = entityModel.msg_sender!!,

            phone = entityModel.phone!!

        )
    }

    fun toEntityList(initial : ArrayList<ChatRoomModelDto?>?) : Array<ChatRoomEntity>{
        return initial?.map {  mapToEntityModel(it!!)  }?.toTypedArray()!!

    }

    fun fromEntityList(initial : ArrayList<ChatRoomEntity?>?) : List<ChatRoomModelDto?>?{
        return initial?.map { it?.let { it1 -> mapFromEntityModel(it1) } }

    }
}