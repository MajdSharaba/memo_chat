package com.yawar.memo.di

import com.yawar.memo.Api.ChatApi
import com.yawar.memo.repositry.BlockUserRepo
import com.yawar.memo.repositry.ChatMessageRepoo
import com.yawar.memo.repositry.ChatRoomRepoo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object BlockUserRepositryModule {
    @Singleton
    @Provides
    fun provideBlockUserRepository(chatApi: ChatApi, chatRoomRepoo: ChatRoomRepoo): BlockUserRepo {
        return  BlockUserRepo(chatApi,chatRoomRepoo)
    }
}