package com.yawar.memo.di

import com.yawar.memo.Api.ChatApi
import com.yawar.memo.database.dao.ChatRoomDatabase
import com.yawar.memo.database.entity.callHistoryEntity.CallHistoryEntityMapper
import com.yawar.memo.database.entity.specialMessageEntity.SpecialMessageEntityMapper
import com.yawar.memo.network.networkModel.callHistoryModel.CallHistoryDtoMapper
import com.yawar.memo.network.networkModel.specialMessageModel.SpecialMessageDtoMapper
import com.yawar.memo.repositry.AuthRepo
import com.yawar.memo.repositry.CallHistoryRepo
import com.yawar.memo.repositry.SpecialMessagesRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object SpecialMessagesModule {
    @Provides
    fun provideSpecialMessagesRepositry(chatApi: ChatApi, database: ChatRoomDatabase, SpecialMessageDtoMapper: SpecialMessageDtoMapper, specialMessageEntityMapper : SpecialMessageEntityMapper): SpecialMessagesRepo {
        return  SpecialMessagesRepo(chatApi,database, SpecialMessageDtoMapper, specialMessageEntityMapper)

    }

    @Provides
    fun provideSpecialMessageDtoMapper(): SpecialMessageDtoMapper {
        return  SpecialMessageDtoMapper()

    }

    @Provides
    fun provideSpecialMessageEntityMapper(): SpecialMessageEntityMapper {
        return  SpecialMessageEntityMapper()

    }


}