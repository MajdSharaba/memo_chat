package com.yawar.memo.dataBindingAdapter

import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.yawar.memo.domain.model.ChatMessage
import com.yawar.memo.domain.model.SendContactNumberResponse
import java.io.File

//////Voice Adapter Binding

@BindingAdapter("currentTime")
fun TextView.setName(item : ChatMessage?) {
    item?.let {
        val voiceFile: File = if (item.isMe) {
            val d =
                context.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send/voiceRecord") // -> filename = maven.pdf
            File(d, item.fileName)
        } else {
            val d =
                context.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive/voiceRecord") // -> filename = maven.pdf
            File(d, item.message)
        }
        if (!voiceFile.exists()) {
          text = "0.0"
        }
        else{
            text = "0.0"

        }
    }
}

@BindingAdapter("image_for_special")
fun ImageView.setVisability(item : ChatMessage?) {
    item?.let {
        if(item.favoriteFor!!){
            visibility = View.VISIBLE
        }
        else{
            visibility = View.GONE
        }
    }
}



//////end Voice Adapter Binding