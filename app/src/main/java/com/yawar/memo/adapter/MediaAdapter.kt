package com.yawar.memo.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.chrisbanes.photoview.PhotoView
import com.yawar.memo.R
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.databinding.MediaItemBinding
import com.yawar.memo.model.MediaModel

class MediaAdapter(
    private val courseDataArrayList: ArrayList<MediaModel>,
    private val mcontext: Context,
) :
    RecyclerView.Adapter<MediaAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate Layout
        return from( parent)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imgid = courseDataArrayList[position]
        holder.bind(position,imgid,mcontext)
    }
    override fun getItemCount(): Int {
        // this method returns the size of recyclerview
        return courseDataArrayList.size
    }

    // View Holder Class to handle Recycler View.
     class ViewHolder(val binding: MediaItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int,model :MediaModel , mcontext: Context ) {
            binding.mediaModel = model
            binding.executePendingBindings()

            }
        }
        companion object {
            fun from( parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = MediaItemBinding.inflate(layoutInflater,parent, false)
                return ViewHolder(binding)
            }
        }

    }


