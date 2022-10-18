package com.yawar.memo.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
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
import com.yawar.memo.model.SendContactNumberResponse
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
//        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        ///Initialize view
        return ViewHolder.from( parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model :  SendContactNumberResponse = arrayList[position]
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

     class ViewHolder private  constructor (itemView: View) : RecyclerView.ViewHolder(itemView) {
        ///Initialize variable
        var tvName: TextView = itemView.findViewById(R.id.tv_name)
        var tvNumber: TextView = itemView.findViewById(R.id.tv_number)
        var imageView: ImageView = itemView.findViewById(R.id.iv_image)
        var button: Button = itemView.findViewById(R.id.btn_share)



        @SuppressLint("UseCompatLoadingForDrawables")
        public fun bind(
            model: SendContactNumberResponse,
            position: Int,
            activity: Activity,
            mCallback : CallbackInterface?
        ) {
            tvName.text = model.name
            tvNumber.text = model.number
            println(model.image)
            if (!model.image!!.isEmpty()) {
                //            Glide.with(holder.imageView.getContext()).load(AllConstants.imageUrl+model.getImage()).error(activity.getDrawable(R.drawable.th)).into(holder.imageView);
                Glide.with(imageView.context).load(AllConstants.imageUrl + model.image)
                    .apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th))
                    .into(imageView)
            } else {
                imageView.setImageDrawable(activity.resources.getDrawable(R.drawable.th))
            }

            // Glide.with(holder.imageView.getContext()).load(model.getImage()).into(holder.imageView);
            if (model.state == "false") {
                button.visibility = View.VISIBLE
                itemView.setOnClickListener { }
                button.setOnClickListener {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    //  String Body ="حمل هذا التطبيق";
                    //  String Body ="حمل هذا التطبيق";
                    val Sub = """
                        ${model.app_path}
                        ${activity.getString(R.string.download_app)}
                        """.trimIndent()
                    //     intent.putExtra(Intent.EXTRA_TEXT,Body);
                    intent.putExtra(Intent.EXTRA_TEXT, Sub)
                    activity.startActivity(Intent.createChooser(intent, "Share Using"))
                }
            } else {
                button.visibility = View.INVISIBLE
                itemView.setOnClickListener {
                    if (mCallback != null) {
                        mCallback!!.onHandleSelection(position, model)
                    }
                }
            }
        }


        companion object {
            fun from( parent: ViewGroup): ViewHolder {
                val view: View =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_contact_number, parent, false)
                return ViewHolder(view)
            }
        }
    }



}