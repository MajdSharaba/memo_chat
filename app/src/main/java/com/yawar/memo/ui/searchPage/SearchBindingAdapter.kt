package com.yawar.memo.dataBindingAdapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yawar.memo.BaseApp
import com.yawar.memo.R
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.domain.model.ChatRoomModel
import com.yawar.memo.domain.model.SearchRespone
import de.hdodenhof.circleimageview.CircleImageView

@BindingAdapter("name")
fun TextView.setName(item : SearchRespone?) {
    item?.let {
        text = item.name
    }
}

    @BindingAdapter("userImage")
    fun CircleImageView.setUserImage(item: SearchRespone?) {
        item?.let {
            if (item.image?.isNotEmpty()!!) {
                Glide.with(this).load(AllConstants.imageUrl + item.image)
                    .apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th))
                    .into(this)
            } else {
                setImageDrawable(BaseApp.instance?.resources?.getDrawable(R.drawable.th))
            }
        }
    }

