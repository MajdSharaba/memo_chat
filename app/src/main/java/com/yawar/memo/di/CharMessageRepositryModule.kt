package com.yawar.memo.di

import com.facebook.internal.Utility.logd
import com.yawar.memo.Api.ChatApi
import com.yawar.memo.database.dao.ChatRoomDatabase
import com.yawar.memo.database.entity.ChatMessageEntity.ChatMessageEntityMapper
import com.yawar.memo.database.entity.callHistoryEntity.CallHistoryEntityMapper
import com.yawar.memo.network.networkModel.callHistoryModel.CallHistoryDtoMapper
import com.yawar.memo.network.networkModel.chatMessageModel.ChatMessageDtoMapper
import com.yawar.memo.network.networkModel.chatRoomModel.ChatRoomDtoMapper
import com.yawar.memo.repositry.ChatMessageRepoo
import com.yawar.memo.repositry.UserInformationRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(ViewModelComponent::class)
@Module
object CharMessageRepositryModule {
    @ViewModelScoped
    @Provides
    fun provideMessageRepository(chatApi: ChatApi,database: ChatRoomDatabase
                                 ,chatMessageDtoMapper: ChatMessageDtoMapper,
                                 chatMessageEntityMapper: ChatMessageEntityMapper): ChatMessageRepoo {
        return  ChatMessageRepoo(chatApi,database,chatMessageDtoMapper,chatMessageEntityMapper)
    }
}




