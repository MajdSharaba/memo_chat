package com.yawar.memo.ui.forwardPage

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yawar.memo.R
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.domain.model.SendContactNumberResponse

class GroupSelectorAdapter : RecyclerView.Adapter<GroupSelectorAdapter.ViewHolder> {
    var fragment: Fragment? = null
    var activity: Activity? = null
    var arrayList: ArrayList<SendContactNumberResponse>
    private var mCallback: CallbackInterface? = null

    interface CallbackInterface {

        fun onHandleSelection(
            position: Int,
            sendContactNumberResponse: SendContactNumberResponse?,
            isChecked: Boolean,
        )
    }

    ///Create constructor
    constructor(fragment: Fragment?, arrayList: ArrayList<SendContactNumberResponse>) {
        this.fragment = fragment
        this.arrayList = arrayList
        try {
            mCallback = fragment as CallbackInterface?
        } catch (ex: ClassCastException) {
        }
    }

    constructor(activity: Activity?, arrayList: ArrayList<SendContactNumberResponse>) {
        this.activity = activity
        this.arrayList = arrayList
        try {
            mCallback = activity as CallbackInterface?
        } catch (ex: ClassCastException) {
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        ///Initialize view
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val  model : SendContactNumberResponse = arrayList[position]
        holder.bind(model, position,mCallback)
    }



    @SuppressLint("NotifyDataSetChanged")
    fun updateList(updateList: ArrayList<SendContactNumberResponse>) {
        arrayList = updateList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

     class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        ///Initialize variable
        var tvName: TextView = itemView.findViewById(R.id.tv_name)
        var tvNumber: TextView = itemView.findViewById(R.id.tv_status)
        var imageView: ImageView = itemView.findViewById(R.id.image)
        var checkBox: CheckBox = itemView.findViewById(R.id.imb_check)



        fun bind(
            model: SendContactNumberResponse,
            position: Int,
            mCallback: CallbackInterface?
        ) {
            tvName.text = model.name
            tvNumber.text = model.number
            println(model.image)
            if (model.image!!.isNotEmpty()) {
                //        Glide.with(holder.imageView.getContext()).load(AllConstants.imageUrl+model.getImage()).into(holder.imageView);
                Glide.with(imageView.context).load(AllConstants.imageUrl + model.image)
                    .apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th))
                    .into(imageView)
            }
            checkBox.setOnClickListener {
                mCallback?.onHandleSelection(position,
                    model,
                    checkBox.isChecked)
            }
        }
         companion object {
             fun from(parent: ViewGroup): ViewHolder {
                 val view: View =
                     LayoutInflater.from(parent.context).inflate(R.layout.item_group_selector, parent, false)
                 return ViewHolder(view)
             }
         }

    }


}