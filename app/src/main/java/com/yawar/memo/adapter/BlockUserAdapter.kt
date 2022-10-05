package com.yawar.memo.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yawar.memo.R
import com.yawar.memo.adapter.BlockUserAdapter.ViewHolder.Companion.from
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.model.UserModel

class BlockUserAdapter(var activity: Activity, arrayList: List<UserModel>) :
    RecyclerView.Adapter<BlockUserAdapter.ViewHolder>() {
    var arrayList: List<UserModel> = emptyList()
    var mCallback: CallbackInterface? = null

    interface CallbackInterface {
        fun onHandleSelection(position: Int, userModel: UserModel?)
    }
    init {
        this.arrayList = arrayList
        try {
            mCallback = activity as CallbackInterface
        } catch (ex: ClassCastException) {
            //.. should log the error or throw and exception
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        ///Initialize view
        return from( parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = arrayList[position]
        holder.bind(model, position,mCallback,activity)
    }



    override fun getItemCount(): Int {
        return arrayList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(updateList: ArrayList<UserModel>) {
        arrayList = updateList
        notifyDataSetChanged()
    }

     class ViewHolder private  constructor (itemView: View) : RecyclerView.ViewHolder(itemView) {
        ///Initialize variable
        var tvName: TextView = itemView.findViewById(R.id.tv_name)
        var tvNumber: TextView = itemView.findViewById(R.id.tv_number)
        var imageView: ImageView = itemView.findViewById(R.id.iv_image)
        var button: Button = itemView.findViewById(R.id.btn_unBlock)


        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(
            model: UserModel,
            position: Int,
             mCallback: CallbackInterface?,
             activity: Activity
        ) {
            tvName.text = model.userName
            tvNumber.text = model.phone
            println(model.image)
            if (model.image!!.isNotEmpty()) {
                Glide.with(imageView.context).load(AllConstants.imageUrl + model.image)
                    .apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th))
                    .into(imageView)
            } else {
                imageView.setImageDrawable(activity.resources.getDrawable(R.drawable.th))
            }
            itemView.setOnClickListener {
                if (mCallback != null) {
                    mCallback!!.onHandleSelection(position,model)
                }
            }
        }

       companion object {
            fun from( parent: ViewGroup): ViewHolder {
                val view: View =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_block_user, parent, false)
                return ViewHolder(view)
            }
        }

    }

    ///Create constructor


}