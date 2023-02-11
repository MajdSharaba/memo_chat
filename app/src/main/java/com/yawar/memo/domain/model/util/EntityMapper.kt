package com.yawar.memo.domain.model.util

interface EntityMapper <T,EntityModel> {
    fun mapToEntityModel(model: T):EntityModel

    fun mapFromEntityModel(entityModel: EntityModel) : T
}