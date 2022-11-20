package com.yawar.memo.ui.deviceLinkPage

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
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.model.DeviceLinkModel


class DeviceLinkAdapter(
    var activity: Activity, var arrayList: ArrayList<DeviceLinkModel>,
) :
    RecyclerView.Adapter<DeviceLinkAdapter.ViewHolder>() {
    private var mCallback: CallbackInterface? = null

    interface CallbackInterface {

        fun onHandleSelection(position: Int, deviceLinkModel: DeviceLinkModel?)
        fun onClickItem(position: Int, deviceLinkModel: DeviceLinkModel?)
    }
    init {
        try {
            mCallback = activity as CallbackInterface
        } catch (ex: ClassCastException) {
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = arrayList[position]
        holder.bind(model, position, mCallback)
    }



    override fun getItemCount(): Int {
        return arrayList.size
    }

     class ViewHolder private  constructor (itemView: View) : RecyclerView.ViewHolder(itemView) {
        ///Initialize variable
        var tvName: TextView = itemView.findViewById(R.id.tv_name)
        var imageView: ImageView = itemView.findViewById(R.id.iv_image)
        var tvNumber: TextView = itemView.findViewById(R.id.tv_number)
        var button: Button = itemView.findViewById(R.id.btn_add)



        fun bind(
            model: DeviceLinkModel,
            position: Int,
            mCallback: CallbackInterface?
        ) {
            tvName.text = model.name
            tvNumber.text = model.time
            println(model.image)

            if (model.image!!.isNotEmpty()) {
                Glide.with(imageView.context).load(AllConstants.imageUrl + model.image)
                    .apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th))
                    .into(imageView)
            }
            itemView.setOnClickListener {
                mCallback?.onClickItem(position, model)
            }
        }
         companion object {
             fun from(parent: ViewGroup): ViewHolder {
                 val view: View =
                     LayoutInflater.from(parent.context).inflate(R.layout.item_device_link, parent, false)
                 return ViewHolder(view)
             }
         }


    }



}