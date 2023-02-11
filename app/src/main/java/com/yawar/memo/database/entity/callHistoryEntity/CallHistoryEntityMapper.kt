package com.yawar.memo.database.entity.callHistoryEntity

import com.yawar.memo.domain.model.CallHistoryModel
import com.yawar.memo.domain.model.util.DomainMapper

class CallHistoryEntityMapper : DomainMapper<CallHistoryEntity, CallHistoryModel> {
    override fun mapToDominModel(model: CallHistoryEntity): CallHistoryModel {
        return CallHistoryModel(
            id = model.id,

            username = model.username,

            caller_id = model.caller_id,

            image = model.image,

            call_type =  model.call_type,

            answer_id = model.answer_id,

            call_status =  model.call_status,

            duration =  model.duration,

            createdAt = model.createdAt.toString()
        )
    }

    override fun mapFromDominModel(domainModel: CallHistoryModel): CallHistoryEntity {
        return CallHistoryEntity(

            id = domainModel.id,

            username = domainModel.username,

            caller_id = domainModel.caller_id,

            image = domainModel.image,

            call_type =  domainModel.call_type,

            answer_id = domainModel.answer_id,

            call_status =  domainModel.call_status,

            duration =  domainModel.duration,

            createdAt = domainModel.createdAt.toLong()
        )
    }
    fun toDomainList(initial: List<CallHistoryEntity?>?) : List<CallHistoryModel?>?{
        return initial?.map {  mapToDominModel(it!!)  }

    }

    fun fromDomainList(initial : ArrayList<CallHistoryModel>) : Array<CallHistoryEntity>{
        return initial?.map {  mapFromDominModel(it!!)  }?.toTypedArray()!!


    }
}