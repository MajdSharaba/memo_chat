package com.yawar.memo.di

import com.yawar.memo.database.entity.chatRoomEntity.ChatRoomEntityMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object DataBaseModule {
    @Singleton
    @Provides
    fun provideEntityMapper(): ChatRoomEntityMapper {
        return  ChatRoomEntityMapper()
    }
}