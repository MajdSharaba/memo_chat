package com.yawar.memo.utils

import android.os.Environment
import com.yawar.memo.BaseApp
import com.yawar.memo.domain.model.ChatMessage
import com.yawar.memo.domain.model.SpecialMessageModel
import java.io.File

fun getFile(chatMessage: SpecialMessageModel, myMsg: Boolean, type:String): File {
    return if (myMsg) {
        val d =
            BaseApp.instance?.baseContext?.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send${type}") // -> filename = maven.pdf
        File(d, chatMessage.orginalName)
    } else if(chatMessage.message_type == "imageWeb"){
        val d =
            BaseApp.instance?.baseContext?.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive${type}") // -> filename = maven.pdf
        File(d, chatMessage.message)
    }
    else {
        val d =
            BaseApp.instance?.baseContext?.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive${type}") // -> filename = maven.pdf
        File(d, chatMessage.message)
    }

}