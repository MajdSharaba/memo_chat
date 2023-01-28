package com.yawar.memo.domain.model.util.chatRoomUtil

interface DomainMapper<T,DomainModel> {
    fun mapToDominModel(model: T):DomainModel

    fun mapFromDominModel(domainModel: DomainModel) : T


}
