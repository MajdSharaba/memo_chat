package com.yawar.memo.model.util

interface DomainMapper<T,DomainModel> {
    fun mapToDominModel(model: T):DomainModel

    fun mapFromDominModel(domainModel: DomainModel) : T


}
