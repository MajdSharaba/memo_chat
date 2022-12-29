package com.yawar.memo.di

import com.yawar.memo.Api.ChatApi
import dagger.Module
import dagger.Provides
import com.yawar.memo.repositry.UserInformationRepo
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object UserInfornationRepositryModule {
    @Singleton
    @Provides
     fun providesUserInformationRepository(chatApi: ChatApi): UserInformationRepo{
         return  UserInformationRepo(chatApi)
     }

}

//@Module
////Repositories will live same as the activity that requires them
//@InstallIn(SingletonComponent::class)
//object  ChatRoomRepositryModule {
//    @Provides
//    @Singleton
//     fun providesChannelRepository(): ChatRoomRepo{
//         return  ChatRoomRepoImp()
//     }
//}