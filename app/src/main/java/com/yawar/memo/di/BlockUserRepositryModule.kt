package com.yawar.memo.di

import com.yawar.memo.repositry.BlockUserRepo
import com.yawar.memo.repositry.ChatMessageRepoo
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
    fun provideBlockUserRepository(): BlockUserRepo {
        return  BlockUserRepo()
    }
}