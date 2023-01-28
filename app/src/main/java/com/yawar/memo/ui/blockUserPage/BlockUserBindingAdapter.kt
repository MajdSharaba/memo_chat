package com.yawar.memo.dataBindingAdapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yawar.memo.BaseApp
import com.yawar.memo.R
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.domain.model.UserModel
import de.hdodenhof.circleimageview.CircleImageView

@BindingAdapter("userImage")
fun CircleImageView.setUserImage(item: UserModel?) {
    item?.let {
        if (item.image?.isNotEmpty()!!) {
            Glide.with(this).load(AllConstants.imageUrl + item.image)
                .apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th))
                .into(this)
        } else {
            setImageDrawable(BaseApp.instance!!.resources.getDrawable(R.drawable.th))
        }
    }
}

@BindingAdapter("name")
fun TextView.setName(item : UserModel?) {
    item?.let {
        text = item.userName+ " "+ item.lastName
    }
}

@BindingAdapter("number")
fun TextView.setNumber(item: UserModel?) {
    item?.let {
        text = item.phone
    }
}