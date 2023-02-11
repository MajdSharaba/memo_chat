package com.yawar.memo.database.entity.callHistoryEntity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CallHistoryEntity (
    var id: String = "",

    var username: String = "",

    var caller_id: String? = "",

    var image: String = "",

    var call_type: String  = "",

    var answer_id: String  = "",

    var call_status: String = "",

    var duration: String = "",
    @PrimaryKey(autoGenerate = false)
    var createdAt: Long = 0,
        ){
}