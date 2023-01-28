package com.yawar.memo.di

import com.yawar.memo.network.networkModel.chatRoomModel.ChatRoomDtoMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {
    @Singleton
    @Provides
    fun provideChatRoomMapper(): ChatRoomDtoMapper {
        return  ChatRoomDtoMapper()
    }
}