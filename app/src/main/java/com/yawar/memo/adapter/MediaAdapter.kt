package com.yawar.memo.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.chrisbanes.photoview.PhotoView
import com.yawar.memo.R
import com.yawar.memo.adapter.MediaAdapter.RecyclerViewHolder.Companion.from
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.model.MediaModel

class MediaAdapter(
    private val courseDataArrayList: ArrayList<MediaModel>,
    private val mcontext: Context,
) :
    RecyclerView.Adapter<MediaAdapter.RecyclerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        // Inflate Layout
        return from( parent)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val imgid = courseDataArrayList[position]

        holder.bind(position,imgid,mcontext)
    }



    override fun getItemCount(): Int {
        // this method returns the size of recyclerview
        return courseDataArrayList.size
    }

    // View Holder Class to handle Recycler View.
     class RecyclerViewHolder private  constructor (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseIV: ImageView = itemView.findViewById(R.id.idIVcourseIV)

        fun bind(position: Int,model :MediaModel , mcontext: Context ) {
            if (model.imgid!!.isNotEmpty()) {

                Glide.with(courseIV.context).load(AllConstants.imageUrlInConversation + model.imgid)
                    .centerCrop()
                    .apply(RequestOptions.placeholderOf(R.color.black).error(R.color.black))
                    .into(courseIV)
                courseIV.setOnClickListener {
                    val dialog = Dialog(mcontext)
                    dialog.setContentView(R.layout.dialog_image_cht)
                    dialog.setTitle("Title...")
                    dialog.window!!.setLayout(ViewGroup.LayoutParams.FILL_PARENT,
                        ViewGroup.LayoutParams.FILL_PARENT)
                    val image: PhotoView = dialog.findViewById(R.id.photo_view)
                    Glide.with(image.context)
                        .load(AllConstants.imageUrlInConversation + model.imgid).centerCrop()
                        .apply(RequestOptions.placeholderOf(R.color.black).error(R.color.black))
                        .into(image)
                    dialog.show()
                }
            }
        }
        companion object {
            fun from( parent: ViewGroup): RecyclerViewHolder {
                val view: View =
                    LayoutInflater.from(parent.context).inflate(R.layout.media_item, parent, false)
                return RecyclerViewHolder(view)
            }
        }

    }


}