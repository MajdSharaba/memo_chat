package com.yawar.memo.ui.searchPage

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yawar.memo.BaseApp
import com.yawar.memo.R
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.domain.model.SearchModel
import de.hdodenhof.circleimageview.CircleImageView

@SuppressLint("SetTextI18n")
@BindingAdapter("name")
fun TextView.setName(item : SearchModel?) {
    item?.let {
        text = item.first_name + " "+item.last_name
    }
}

    @BindingAdapter("userImage")
    fun CircleImageView.setUserImage(item: SearchModel?) {
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

