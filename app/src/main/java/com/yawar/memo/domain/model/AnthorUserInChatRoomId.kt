package com.yawar.memo.domain.model


data class AnthorUserInChatRoomId private constructor(var id : String, var fcmToken : String, var blockedFor : String?,
                                                      var chatId: String = "", var imageUrl :String,  var specialNumber :String = "" , var userName :String, var messageId : String) {
    companion object {
        private var instance: AnthorUserInChatRoomId? = null

        fun getInstance(id: String,fcmToken : String,blockedFor : String,chatId: String,imageUrl :String,specialNumber :String,userName :String, messageId: String): AnthorUserInChatRoomId {
            if (instance == null) {
                instance = AnthorUserInChatRoomId(id,fcmToken,blockedFor,chatId,imageUrl,specialNumber,userName,messageId)
            }
            return instance as AnthorUserInChatRoomId
        }
    }
}




