package com.yawar.memo.network.networkModel.callHistoryModel

import com.yawar.memo.Api.ChatApi
import com.yawar.memo.database.dao.ChatRoomDatabase
import com.yawar.memo.database.entity.callHistoryEntity.CallHistoryEntity
import com.yawar.memo.domain.model.CallHistoryModel
import com.yawar.memo.domain.model.util.DomainMapper
import com.yawar.memo.domain.model.util.EntityMapper

import com.yawar.memo.repositry.CallHistoryRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


class CallHistoryDtoMapper : DomainMapper<CallHistoryDto, CallHistoryModel>,
    EntityMapper<CallHistoryDto, CallHistoryEntity> {
    override fun mapToDominModel(model: CallHistoryDto): CallHistoryModel {
        return CallHistoryModel(
            id = model.id,

            username = model.first_name + " " + model.last_name,

            caller_id = model.caller,

            image = model.profile_image,

            call_type =  model.call_type,

            answer_id = model.answer,

            call_status =  model.call_state,

            duration =  model.duration?:"0",

            createdAt = model.call_time?:"0"
        )
    }

    override fun mapFromDominModel(domainModel: CallHistoryModel): CallHistoryDto {
        TODO("Not yet implemented")
    }

    fun toDomainList(initial : ArrayList<CallHistoryDto?>?) : List<CallHistoryModel?>?{
        return initial?.map {  mapToDominModel(it!!)  }

    }


    //////for Entity


    override fun mapToEntityModel(model: CallHistoryDto): CallHistoryEntity {
        return CallHistoryEntity(
            id = model.id,

            username = model.first_name + " " + model.last_name,

            caller_id = model.caller,

            image = model.profile_image,

            call_type =  model.call_type,

            answer_id = model.answer,

            call_status =  model.call_state,

            duration =  model.duration?:"0",

            createdAt = model.call_time?.toLong() ?: 0
        )
    }

    override fun mapFromEntityModel(entityModel: CallHistoryEntity): CallHistoryDto {
        TODO("Not yet implemented")
    }


    fun toEntityList(initial : List<CallHistoryDto?>?) : Array<CallHistoryEntity>{
        return initial?.map {  mapToEntityModel(it!!)  }?.toTypedArray()!!

    }


}
