package com.yawar.memo.domain.model.util.chatRoomUtil

interface EntityMapper <T,EntityModel> {
    fun mapToEntityModel(model: T):EntityModel

    fun mapFromEntityModel(entityModel: EntityModel) : T
}