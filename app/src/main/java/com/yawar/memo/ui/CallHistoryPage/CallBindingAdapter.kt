package com.yawar.memo.dataBindingAdapter

import android.content.res.ColorStateList
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yawar.memo.BaseApp
import com.yawar.memo.R
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.domain.model.CallHistoryModel
import com.yawar.memo.utils.TimeProperties
import de.hdodenhof.circleimageview.CircleImageView

@BindingAdapter("name")
fun TextView.setName(item : CallHistoryModel?) {
    item?.let {
        text = item.username
    }
}


    @BindingAdapter("userImage")
    fun CircleImageView.setUserImage(item: CallHistoryModel?) {
        item?.let {
            if (item.image?.isNotEmpty()!!) {
                Glide.with(this).load(AllConstants.imageUrl + item.image)
                    .apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th))
                    .into(this)
            } else {
                setImageDrawable(BaseApp.instance?.resources!!.getDrawable(R.drawable.th))
            }
        }
    }

        @BindingAdapter("time")
        fun TextView.setTime(item : CallHistoryModel?) {
            item?.let {
                if(item.createdAt.isNotEmpty())
                text = TimeProperties.getDate(item.createdAt.toLong(), "dd MMMM , h:mm")
            }
        }


        @BindingAdapter("imageType")
        fun ImageView.setImageType(item : CallHistoryModel?) {
            item?.let {

                if (item.call_type == "video") {
                    setImageDrawable(context.getDrawable(R.drawable.ic_video_call))


                } else {
                    setImageDrawable(context.getDrawable(R.drawable.ic_call_blue))

                }

            }}

        @BindingAdapter("imageStatus")
        fun ImageView.setImageStatus(item : CallHistoryModel?) {
            item?.let {

                if (BaseApp.instance?.classSharedPreferences != null) {
                    if (item.caller_id == BaseApp.instance?.classSharedPreferences!!.user.userId) {
                        setImageDrawable(context.getDrawable(R.drawable.ic_out_going_call))
                    } else {
                        setImageDrawable(context.getDrawable(R.drawable.ic_incoming_call))
                    }
                }
                when (item.call_status) {
                    "missed call" -> {
                        imageTintList =
                            ColorStateList.valueOf(context.resources.getColor(R.color.red))
                    }
                    "answer" -> {
                       imageTintList =
                            ColorStateList.valueOf(context.resources.getColor(R.color.memo_background_color_new))
                    }
                    else -> {
                        setImageDrawable(context.getDrawable(R.drawable.ic_close))
                       imageTintList =
                            ColorStateList.valueOf(context.resources.getColor(R.color.red))
                    }
                }

            }}
