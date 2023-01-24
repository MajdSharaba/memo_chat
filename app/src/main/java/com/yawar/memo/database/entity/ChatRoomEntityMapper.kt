package com.yawar.memo.database.entity

import com.yawar.memo.model.ChatRoomModel
import com.yawar.memo.model.util.DomainMapper

class ChatRoomEntityMapper : DomainMapper<ChatRoomEntity, ChatRoomModel> {
    override fun mapToDominModel(model: ChatRoomEntity): ChatRoomModel {
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

            created_at = model.created_at.toString(),

            isTyping = model.isTyping,

            blocked_for = model.blocked_for,

            msg_sender = model.msg_sender,

            phone = model.phone

        )
    }

    override fun mapFromDominModel(domainModel: ChatRoomModel): ChatRoomEntity {
        return ChatRoomEntity(
            username = domainModel.username!!,

            other_id = domainModel.other_id!!,

            last_message = domainModel.last_message!!,

            image = domainModel.image!!,

            isChecked = domainModel.isChecked!!,

            numberMessage = domainModel.numberMessage?:"0",

            id = domainModel.id!!,

            state = domainModel.state.toString(),

            num_msg = domainModel.num_msg!!,

            inChat = domainModel.inChat!!,

            user_token = domainModel.user_token!!,

            sn = domainModel.sn!!,

            message_type = domainModel.message_type!!,

            mstate = domainModel.mstate!!,

            created_at = domainModel.created_at!!.toLong(),

            isTyping = domainModel.isTyping!!,

            blocked_for = domainModel.blocked_for.toString(),

            msg_sender  = domainModel.msg_sender!!,

            phone  = domainModel.phone?:""

        )
    }
    fun toDomainList(initial: List<ChatRoomEntity>) : List<ChatRoomModel?>?{
        return initial?.map {  mapToDominModel(it!!)  }

    }

    fun fromDomainList(initial : ArrayList<ChatRoomModel>) : Array<ChatRoomEntity>{
        return initial?.map {  mapFromDominModel(it!!)  }?.toTypedArray()!!


    }
}