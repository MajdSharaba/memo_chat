package com.yawar.memo.di

import com.yawar.memo.Api.ChatApi
import com.yawar.memo.database.dao.ChatRoomDatabase
import com.yawar.memo.database.entity.callHistoryEntity.CallHistoryEntityMapper
import com.yawar.memo.database.entity.chatRoomEntity.ChatRoomEntityMapper
import com.yawar.memo.network.networkModel.callHistoryModel.CallHistoryDtoMapper
import com.yawar.memo.network.networkModel.chatRoomModel.ChatRoomDtoMapper
import com.yawar.memo.repositry.CallHistoryRepo
import com.yawar.memo.repositry.ChatRoomRepoo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object CallHistoryRepositryModule {
    @Provides
    fun provideCallHistoryRepository(chatApi: ChatApi, database: ChatRoomDatabase,callHistoryDtoMapper: CallHistoryDtoMapper, callHistoryEntityMapper: CallHistoryEntityMapper): CallHistoryRepo {
        return  CallHistoryRepo(chatApi,database, callHistoryDtoMapper, callHistoryEntityMapper)

    }

    @Provides
    fun provideCallHistoryDtoMapper(): CallHistoryDtoMapper {
        return  CallHistoryDtoMapper()

    }

    @Provides
    fun provideCallHistoryEntityMapper(): CallHistoryEntityMapper {
        return  CallHistoryEntityMapper()

    }


}