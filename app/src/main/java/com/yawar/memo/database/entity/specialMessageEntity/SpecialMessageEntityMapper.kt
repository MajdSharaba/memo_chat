package com.yawar.memo.database.entity.specialMessageEntity

import androidx.room.PrimaryKey
import com.yawar.memo.domain.model.SpecialMessageModel
import com.yawar.memo.domain.model.util.DomainMapper

class SpecialMessageEntityMapper : DomainMapper<SpecailMessageEntity, SpecialMessageModel> {
    override fun mapToDominModel(model: SpecailMessageEntity): SpecialMessageModel {
        return SpecialMessageModel(

            fullname = model.fullname,

            other_id = model.other_id,

            message_id = model.message_id,

            message = model.message,

            state = model.state,

            user_token =  model.user_token,

            sn = model.sn,

            message_type =  model.message_type,

            created_at =  model.created_at.toString(),

            orginalName = model.orginalName,

            blocked_for = model.blocked_for,

            profile_image = model.profile_image,

            sender_id = model.sender_id,

            isDownlod = model.isDownlod,

            isChecked = model.isChecked,



            )
    }

    override fun mapFromDominModel(domainModel: SpecialMessageModel): SpecailMessageEntity {
        return SpecailMessageEntity(

            fullname = domainModel.fullname,

            other_id = domainModel.other_id,

            message_id = domainModel.message_id,

            message = domainModel.message,

            state = domainModel.state,

            user_token =  domainModel.user_token,

            sn = domainModel.sn,

            message_type =  domainModel.message_type,

            created_at =  domainModel.created_at?.toLong(),

            orginalName = domainModel.orginalName,

            blocked_for = domainModel.blocked_for,

            profile_image = domainModel.profile_image,

            sender_id = domainModel.sender_id,

            isDownlod = domainModel.isDownlod,

            isChecked = domainModel.isChecked,


            )
    }
    fun toDomainList(initial: List<SpecailMessageEntity?>?) : List<SpecialMessageModel?>?{
        return initial?.map {  mapToDominModel(it!!)  }

    }

    fun fromDomainList(initial : ArrayList<SpecialMessageModel>) : Array<SpecailMessageEntity>{
        return initial?.map {  mapFromDominModel(it!!)  }?.toTypedArray()!!


    }
}