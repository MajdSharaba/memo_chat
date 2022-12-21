package com.yawar.memo.di

import com.yawar.memo.repositry.ChatMessageRepoo
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
    fun provideChatRoomRepository(): ChatRoomRepoo {
        return  ChatRoomRepoo()
    }
}