package com.yawar.memo.database.dao

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.yawar.memo.database.entity.callHistoryEntity.CallHistoryEntity
import com.yawar.memo.database.entity.chatRoomEntity.ChatRoomEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Dao
interface ChatRoomDao {
    @Query("SELECT * FROM chatRoomEntity ORDER BY created_at DESC")
    fun  getChatRooms():LiveData<List<ChatRoomEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg chatRoomEntity: ChatRoomEntity)

    @Query("DELETE FROM chatRoomEntity WHERE other_id = :userId")
    fun deleteChatRoom(userId: String)

    @Query("UPDATE  chatRoomEntity SET last_message  = :lastMessage ,message_type = :messageType,mstate = :mState, created_at = :CreatedAt, msg_sender = :msgSender, num_msg = :numMsg  WHERE other_id = :userId")
    fun updateChatRoom(userId: String, lastMessage: String, messageType: String, mState: String, CreatedAt: Long, msgSender: String, numMsg: String)

    @Query("UPDATE  chatRoomEntity SET id  = :id , num_msg = :numMsg  WHERE other_id = :userId")
    fun updateChatRoomId(userId: String, id:String, numMsg: String)

    @Query("UPDATE  chatRoomEntity SET isTyping = :isTyping WHERE id = :chat_id")
    fun setTyping(chat_id: String, isTyping: Boolean )

    @Query("UPDATE  chatRoomEntity SET state = :state WHERE other_id = :userId")
    fun updateState(userId: String, state: String )

    @Query("UPDATE  chatRoomEntity SET mstate = :lastMessageState WHERE id = :chat_id")
    fun updateLastMessaageState(chat_id: String, lastMessageState: String )

    @Query("UPDATE  chatRoomEntity SET inChat = :inChat , num_msg = :numMsg  WHERE other_id = :userId")
    fun setInChat(userId: String, inChat: Boolean, numMsg: String )

    @Query("UPDATE  chatRoomEntity SET blocked_for = :blockedFor  WHERE other_id = :userId")
    fun setBlockState(userId: String, blockedFor: String )


    @Query("DELETE FROM chatRoomEntity")
    fun deleteChatRoomTable()

    ////for Call History

    @Query("SELECT * FROM CallHistoryEntity  ORDER BY createdAt DESC ")
    fun  getCallHistory():LiveData<List<CallHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCalls(vararg callHistoryEntity: CallHistoryEntity)

    @Query("DELETE FROM CallHistoryEntity")
    fun deleteCallHistoryTable()


}
@Database(entities = [ChatRoomEntity::class, CallHistoryEntity::class], version = 1)
abstract class ChatRoomDatabase : RoomDatabase() {
    abstract val chatRoomDao: ChatRoomDao

}
private lateinit var INSTANCE: ChatRoomDatabase

fun getDatabase(context: Context): ChatRoomDatabase {
    synchronized(ChatRoomDatabase::class.java){
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                ChatRoomDatabase::class.java,
                "chatRooms").build()
        }
    }

    return INSTANCE
}
@Module
@InstallIn(SingletonComponent::class)
object ChatRoomDatabaseModule {
    @Provides
    fun provideChatRoomDatabase(context: Context): ChatRoomDatabase {
        return getDatabase(context)
    }
}