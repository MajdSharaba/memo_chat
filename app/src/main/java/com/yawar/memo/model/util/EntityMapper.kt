package com.yawar.memo.model.util

interface EntityMapper <T,EntityModel> {
    fun mapToEntityModel(model: T):EntityModel

    fun mapFromEntityModel(entityModel: EntityModel) : T
}