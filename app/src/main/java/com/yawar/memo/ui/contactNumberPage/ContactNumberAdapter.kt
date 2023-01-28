package com.yawar.memo.ui.contactNumberPage

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yawar.memo.databinding.ItemContactNumberBinding
import com.yawar.memo.domain.model.SendContactNumberResponse
import java.util.*

class ContactNumberAdapter(
///Initialize variable
    var activity: Activity, arrayList: ArrayList<SendContactNumberResponse?>?,
) :
    RecyclerView.Adapter<ContactNumberAdapter.ViewHolder>() {
    var arrayList: MutableList<SendContactNumberResponse> =ArrayList()
    var listsearch: MutableList<SendContactNumberResponse> = ArrayList()
    var listsearch2: MutableList<SendContactNumberResponse> = ArrayList()
    var mCallback: CallbackInterface? = null


    interface CallbackInterface {
        fun onHandleSelection(position: Int, sendContactNumberResponse: SendContactNumberResponse?)
    }

    init {
        this.arrayList = arrayList as MutableList<SendContactNumberResponse>
        try {
            mCallback = activity as CallbackInterface
        } catch (ex: ClassCastException) {
        }
        listsearch2.addAll(arrayList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        ///Initialize view
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model : SendContactNumberResponse = arrayList[position]
        holder.bind(model, position,activity,mCallback)
    }



    override fun getItemCount(): Int {
        return arrayList.size
    }

    fun filter(charText: String) {
        var charText = charText
        println(charText + listsearch2.size)
        charText = charText.lowercase(Locale.getDefault())
        listsearch.clear()
        if (charText.length == 0) {
            listsearch.addAll(listsearch2)
        } else {
            for (wp in listsearch2) {
                if (wp.name!!.lowercase(Locale.getDefault()).contains(charText)) {
                    listsearch.add(wp)
                }
            }
        }
        arrayList.clear()
        arrayList.addAll(listsearch)
        notifyDataSetChanged()
    }

     class ViewHolder private  constructor (val binding: ItemContactNumberBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("UseCompatLoadingForDrawables")
        public fun bind(
            model: SendContactNumberResponse,
            position: Int,
            activity: Activity,
            mCallback : CallbackInterface?
        ) {
            binding.contactNumber = model
            binding.executePendingBindings()

            if (model.state == "false") {
                itemView.setOnClickListener { }

            } else {

                itemView.setOnClickListener {
                    if (mCallback != null) {
                        mCallback.onHandleSelection(position, model)
                    }
                }
            }
        }


        companion object {
            fun from( parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemContactNumberBinding.inflate(layoutInflater,parent, false)
                return ViewHolder(binding)
            }
        }
    }



}