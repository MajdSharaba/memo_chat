package com.yawar.memo.database.entity.specialMessageEntity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class SpecailMessageEntity (
    var  fullname:String  = "",

    var  other_id:String  = "",

    var  message:String  = "",

    var  profile_image:String  = "",

    @PrimaryKey(autoGenerate = false)
    var  message_id:String  = "",

    var  state:String  = "",

    var  user_token:String  = "",

    var  sn:String  = "",

    var  message_type:String  = "",

    var  created_at: Long? = 0 ,

    var  orginalName:String ,

    var  blocked_for:String? ,


    var  isChecked:Boolean = false,


    var  sender_id:String  = "",

    var isDownlod : Boolean = false


)