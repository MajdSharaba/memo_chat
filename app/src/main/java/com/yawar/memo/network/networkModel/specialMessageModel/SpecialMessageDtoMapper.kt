package com.yawar.memo.network.networkModel.specialMessageModel

import android.util.Log
import com.yawar.memo.database.entity.callHistoryEntity.CallHistoryEntity
import com.yawar.memo.database.entity.specialMessageEntity.SpecailMessageEntity
import com.yawar.memo.domain.model.CallHistoryModel
import com.yawar.memo.domain.model.SpecialMessageModel
import com.yawar.memo.domain.model.util.DomainMapper
import com.yawar.memo.domain.model.util.EntityMapper
import com.yawar.memo.network.networkModel.callHistoryModel.CallHistoryDto

class SpecialMessageDtoMapper : DomainMapper<SpecialMessageDto, SpecialMessageModel>,
    EntityMapper<SpecialMessageDto, SpecailMessageEntity> {
    override fun mapToDominModel(model: SpecialMessageDto): SpecialMessageModel {
        Log.d("mapToDominModel", "mapToDominModel:${model.sender_id + model.message} ")
        return SpecialMessageModel(

        fullname = model.fullname,

        other_id = model.other_id,

        message_id = model.message_id,

        state = model.state,

        message = model.message,

        user_token =  model.user_token,

        sn = model.sn,

        message_type =  model.message_type,

        created_at =  model.created_at,

        orginalName = model.orginalName?:"",

        blocked_for = model.blocked_for,

        profile_image = model.profile_image,

        isChecked = false,

        sender_id = model.sender_id

        )

    }

    override fun mapFromDominModel(domainModel: SpecialMessageModel): SpecialMessageDto {

        Log.d("mapToDominModel", "mapFromDominModel:${domainModel.sender_id + domainModel.message} ")

        return SpecialMessageDto(
            fullname = domainModel.fullname,

            other_id = domainModel.other_id,

            message_id = domainModel.message_id,

            state = domainModel.state,

            message = domainModel.message,


            user_token =  domainModel.user_token,

            sn = domainModel.sn,

            message_type =  domainModel.message_type,

            created_at =  domainModel.created_at,

            orginalName = domainModel.orginalName,

            blocked_for = domainModel.blocked_for,

            profile_image = domainModel.profile_image,

            sender_id = domainModel.sender_id



            )
    }



    fun toDomainList(initial : ArrayList<SpecialMessageDto?>?) : List<SpecialMessageModel?>?{
        return initial?.map {  mapToDominModel(it!!)  }

    }


    override fun mapToEntityModel(model: SpecialMessageDto): SpecailMessageEntity {
        Log.d("mapToDominModel", "mapToEntityModel:${model.sender_id + model.message} ")

        return SpecailMessageEntity(
            fullname = model.fullname,

            other_id = model.other_id,

            message_id = model.message_id,

            message = model.message,


            state = model.state,

            user_token =  model.user_token,

            sn = model.sn,

            message_type =  model.message_type,

            created_at =  model.created_at?.toLong(),

            orginalName = model.orginalName?:"",

            blocked_for = model.blocked_for,

            profile_image = model.profile_image,

            isChecked = false,


            sender_id = model.sender_id

        )
    }

    override fun mapFromEntityModel(entityModel: SpecailMessageEntity): SpecialMessageDto {

        Log.d("mapToDominModel", "mapFromEntityModel:${entityModel.sender_id + entityModel.message} ")

        return SpecialMessageDto(
            fullname = entityModel.fullname,

            other_id = entityModel.other_id,

            message_id = entityModel.message_id,

            message = entityModel.message,


            state = entityModel.state,

            user_token =  entityModel.user_token,

            sn = entityModel.sn,

            message_type =  entityModel.message_type,

            created_at =  entityModel.created_at.toString(),

            orginalName = entityModel.orginalName?:"",

            blocked_for = entityModel.blocked_for,

            profile_image = entityModel.profile_image,



            sender_id = entityModel.sender_id

        )
    }

    fun toEntityList (initial : List<SpecialMessageDto?>?) : Array<SpecailMessageEntity>{
        return initial?.map {  mapToEntityModel(it!!)  }?.toTypedArray()!!
    }
}