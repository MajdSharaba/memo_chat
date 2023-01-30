package com.yawar.memo.dataBindingAdapter

import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yawar.memo.BaseApp
import com.yawar.memo.R
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.domain.model.ChatRoomModel
import com.yawar.memo.utils.TimeProperties
import de.hdodenhof.circleimageview.CircleImageView

@BindingAdapter("name")
fun TextView.setName(item : ChatRoomModel?){
    item?.let {
       text = item.username
    }
}
@BindingAdapter("time")
fun TextView.setTime(item : ChatRoomModel?) {
    item?.let {
        val timeProperties = TimeProperties()
        if(!item.created_at.equals("null"))
        text = item.created_at?.toLong()?.let { it1 -> timeProperties.getFormattedDate(it1) }
    }
}

    @BindingAdapter("lastMessage")
    fun TextView.setLastMessage(item : ChatRoomModel?) {
        item?.let {
            if (!item.isTyping) {
                setTextColor(BaseApp.instance!!.getColor(R.color.gray))
                when (item.message_type) {
                    "imageWeb" -> text = context.resources.getString(R.string.photo)
                    "voice" -> text = context.resources.getString(R.string.voice)
                    "video" -> text = context.resources.getString(R.string.video)
                    "file" -> text = context.resources.getString(R.string.file)
                    "contact" -> text = context.resources.getString(R.string.contact)
                    "location" -> text = context.resources.getString(R.string.location)
                    else -> text = item.last_message
                }
            }
            else{
                setTextColor(BaseApp.instance!!.getColor(R.color.green))
                text = context.resources.getString(R.string.writing_now)
            }
        }}

    @BindingAdapter("numberMessage")
    fun TextView.setNumberMessage(item : ChatRoomModel?) {
        item?.let {
            if (!item.isTyping) {
               if(item.num_msg == "0"){
                   visibility = View.GONE
               }
                else{
                   visibility = View.VISIBLE
                   text = item.num_msg
               }
            }
            else{
                visibility = View.GONE

            }
        }}

    @BindingAdapter("ImageType")
    fun ImageView.setImageType(item : ChatRoomModel?) {
        item?.let {
            if (!item.isTyping) {
                if(item.message_type!=null)
                when (item.message_type) {
                    "imageWeb" -> {
                        visibility = View.VISIBLE
                        setImageDrawable(BaseApp.instance!!.resources.getDrawable(R.drawable.ic_select_image))
                    }
                    "voice" ->{
                        visibility = View.VISIBLE
                        setImageDrawable(BaseApp.instance!!.resources.getDrawable(R.drawable.ic_voice))
                    }
                    "video" -> {
                        visibility = View.VISIBLE
                        setImageDrawable(BaseApp.instance!!.resources.getDrawable(R.drawable.ic_video))
                    }
                    "file" -> {
                        visibility = View.VISIBLE
                        setImageDrawable(BaseApp.instance!!.resources.getDrawable(R.drawable.ic_file))
                    }
                    "contact" ->{
                        visibility = View.VISIBLE
                        setImageDrawable(BaseApp.instance!!.resources.getDrawable(R.drawable.ic_person))
                    }
                    "location" -> {
                        visibility = View.VISIBLE
                        setImageDrawable(BaseApp.instance!!.resources.getDrawable(R.drawable.ic_location))
                    }
                    else -> {
                        visibility = View.GONE
                    }
                }
            }
            else{
                visibility = View.GONE

            }
        }}


    @BindingAdapter("imageState")
    fun ImageView.setImageState(item : ChatRoomModel?) {
        item?.let {
            if (!item.isTyping) {
                if (item.msg_sender == BaseApp.instance!!.classSharedPreferences!!.user.userId) {
                    visibility = View.VISIBLE

                    when (item.mstate) {
                        "1" -> {
                            setImageDrawable(BaseApp.instance!!.resources.getDrawable(R.drawable.ic_send_done))
                            imageTintList =
                                ColorStateList.valueOf(BaseApp.instance!!.resources.getColor(R.color.gray))

                        }
                        "2" -> {
                            setImageDrawable(BaseApp.instance!!.resources.getDrawable(R.drawable.ic_recive_done))
                            imageTintList =
                                ColorStateList.valueOf(BaseApp.instance!!.resources.getColor(R.color.gray))

                        }
                        "3" -> {
                            setImageDrawable(BaseApp.instance!!.resources.getDrawable(R.drawable.ic_recive_done_green))
                            imageTintList = null

                        }

                        else -> {
                            setImageDrawable(BaseApp.instance!!.resources.getDrawable(R.drawable.ic_not_send))
                            imageTintList =
                                ColorStateList.valueOf(BaseApp.instance!!.resources.getColor(R.color.gray))

                        }
                    }
                } else {
                    visibility = View.GONE

                }
            }
                else{
                visibility = View.GONE

            }
        }}


    @BindingAdapter("userImage")
    fun CircleImageView.setUserImage(item : ChatRoomModel?) {
        item?.let {
            if (item.image.isNotEmpty()){
                Glide.with(this).load(AllConstants.imageUrl + item.image)
                    .apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th))
                    .into(this)
            } else {
                setImageDrawable(BaseApp.instance!!.resources.getDrawable(R.drawable.th))
            }
        }
    }


