package com.yawar.memo.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.yawar.memo.database.entity.ChatMessageEntity.ChatMessageEntity
import com.yawar.memo.database.entity.chatRoomEntity.ChatRoomEntity

data class ChatRoomWithChatMessages (
     @Embedded val chatRoomEntity: ChatRoomEntity,
     @Relation( parentColumn = "other_id",
     entityColumn = "senderId")

   val chatMessages : List<ChatMessageEntity>
)
