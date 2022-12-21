package com.yawar.memo.di

import com.yawar.memo.repositry.ChatMessageRepoo
import com.yawar.memo.repositry.UserInformationRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object CharMessageRepositryModule {
    @Singleton
    @Provides
    fun provideMessageRepository(): ChatMessageRepoo {
        return  ChatMessageRepoo()
    }

}
