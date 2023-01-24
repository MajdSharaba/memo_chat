package com.yawar.memo.di

import com.yawar.memo.Api.ChatApi
import com.yawar.memo.network.networkModel.ChatRoomDtoMapper
import com.yawar.memo.repositry.AuthRepo
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