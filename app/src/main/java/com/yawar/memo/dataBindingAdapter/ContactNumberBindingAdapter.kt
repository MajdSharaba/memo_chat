package com.yawar.memo.dataBindingAdapter

import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yawar.memo.R
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.model.SendContactNumberResponse
import com.yawar.memo.model.UserModel
import com.yawar.memo.utils.BaseApp
import de.hdodenhof.circleimageview.CircleImageView

@BindingAdapter("userImage")
fun CircleImageView.setUserImage(item: SendContactNumberResponse?) {
    item?.let {
        if (item.image?.isNotEmpty()!!) {
            Glide.with(this).load(AllConstants.imageUrl + item.image)
                .apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th))
                .into(this)
        } else {
            setImageDrawable(BaseApp.getInstance().resources.getDrawable(R.drawable.th))
        }
    }
}

@BindingAdapter("name")
fun TextView.setName(item : SendContactNumberResponse?) {
    item?.let {
        text = item.name
    }
}

@BindingAdapter("number")
fun TextView.setNumber(item: SendContactNumberResponse?) {
    item?.let {
        text = item.number
    }
}

@BindingAdapter("shareButton")
fun TextView.setShareButton(item: SendContactNumberResponse?) {
    item?.let {

        if (item.state == "false") {
            visibility = View.VISIBLE
            setOnClickListener {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"

                val Sub = """
                        ${"https://play.google.com/store/apps/details?id=com.yawar.memo&pli=1"}
                        ${context.getString(R.string.download_app)}
                        """.trimIndent()
                intent.putExtra(Intent.EXTRA_TEXT, Sub)
                context.startActivity(Intent.createChooser(intent, "Share Using"))
            }
        } else {
            visibility = View.INVISIBLE


        }
    }
}