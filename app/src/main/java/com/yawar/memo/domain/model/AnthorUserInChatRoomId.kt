package com.yawar.memo.domain.model


data class AnthorUserInChatRoomId private constructor(var id : String, var fcmToken : String, var blockedFor : String?,
                                                      var chatId: String, var imageUrl :String,  var specialNumber :String , var userName :String) {
    companion object {
        private var instance: AnthorUserInChatRoomId? = null

        fun getInstance(id: String,fcmToken : String,blockedFor : String,chatId: String,imageUrl :String,specialNumber :String,userName :String): AnthorUserInChatRoomId {
            if (instance == null) {
                instance = AnthorUserInChatRoomId(id,fcmToken,blockedFor,chatId,imageUrl,specialNumber,userName)
            }
            return instance as AnthorUserInChatRoomId
        }
    }
}




