package com.yawar.memo.domain.model


data class AnthorUserInChatRoomId private constructor(var id : String) {
    companion object {
        private var instance: AnthorUserInChatRoomId? = null

        fun getInstance(id: String): AnthorUserInChatRoomId {
            if (instance == null) {
                instance = AnthorUserInChatRoomId(id)
            }
            return instance as AnthorUserInChatRoomId
        }
    }
}




