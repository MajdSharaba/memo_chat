package com.yawar.memo.dataBindingAdapter

import android.app.Dialog
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.chrisbanes.photoview.PhotoView
import com.yawar.memo.R
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.domain.model.MediaModel
import com.yawar.memo.domain.model.SendContactNumberResponse
import de.hdodenhof.circleimageview.CircleImageView

@BindingAdapter("image")
fun ImageView.setImage(item: MediaModel?) {
    item?.let {

    }
    if (item?.imgid?.isNotEmpty()!!) {

        Glide.with(this).load(AllConstants.imageUrlInConversation + item.imgid)
            .centerCrop()
            .apply(RequestOptions.placeholderOf(R.color.black).error(R.color.black))
            .into(this)
        setOnClickListener {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.dialog_image_cht)
            dialog.setTitle("Title...")
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT
            )
            val image: PhotoView = dialog.findViewById(R.id.photo_view)
            Glide.with(image.context)
                .load(AllConstants.imageUrlInConversation + item.imgid).centerCrop()
                .apply(RequestOptions.placeholderOf(R.color.black).error(R.color.black))
                .into(image)
            dialog.show()
        }
    }
}