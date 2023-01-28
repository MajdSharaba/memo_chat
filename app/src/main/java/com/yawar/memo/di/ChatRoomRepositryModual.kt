package com.yawar.memo.di

import com.yawar.memo.Api.ChatApi
import com.yawar.memo.database.dao.ChatRoomDatabase
import com.yawar.memo.database.entity.chatRoomEntity.ChatRoomEntityMapper
import com.yawar.memo.network.networkModel.chatRoomModel.ChatRoomDtoMapper
import com.yawar.memo.repositry.ChatRoomRepoo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object ChatRoomRepositryModual {
    @Singleton
    @Provides
    fun provideChatRoomRepository(chatApi: ChatApi, mapper: ChatRoomDtoMapper, database: ChatRoomDatabase, chatRoomEntityMapper: ChatRoomEntityMapper): ChatRoomRepoo {
        return  ChatRoomRepoo(chatApi, mapper,chatRoomEntityMapper, database)
    }
}