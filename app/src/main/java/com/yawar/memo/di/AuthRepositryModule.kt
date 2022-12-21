package com.yawar.memo.di

import com.yawar.memo.repositry.AuthRepo
import com.yawar.memo.repositry.BlockUserRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object AuthRepositryModule {

    @Singleton
    @Provides
    fun provideAuthRepositry(): AuthRepo {
        return  AuthRepo()
    }
}