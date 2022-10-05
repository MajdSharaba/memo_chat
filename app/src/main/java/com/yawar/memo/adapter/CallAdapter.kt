package com.yawar.memo.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yawar.memo.R
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.model.CallModel
import com.yawar.memo.sessionManager.ClassSharedPreferences
import com.yawar.memo.utils.BaseApp
import com.yawar.memo.utils.TimeProperties
import java.util.*
import kotlin.collections.ArrayList

class CallAdapter(var context: Context) :
    ListAdapter<CallModel, CallAdapter.ViewHolder?>(MyDiffUtilCall()), Filterable {
    var listsearch: MutableList<CallModel?> = ArrayList()
    var classSharedPreferences: ClassSharedPreferences?
    init {
        listsearch.addAll(currentList)
        classSharedPreferences = BaseApp.getInstance().classSharedPreferences
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Inflate the layout, initialize the View Holder
        return ViewHolder.from( parent)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        val callModel = getItem(position)
        holder.bind(callModel,context,classSharedPreferences)
    }



    override fun getFilter(): Filter {
        return exampleFilter
    }

    private val exampleFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList: MutableList<CallModel?> = ArrayList()
            println(listsearch.size.toString() + "listsearch.size()")
            if (constraint == null || constraint.length == 0) {
                filteredList.addAll(listsearch)
            } else {
                val filterPattern = constraint.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
                for (item in listsearch) {
                    if (item!!.username.lowercase(Locale.getDefault()).contains(filterPattern)) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            submitList(results.values as List<CallModel?>)
        }
    }

    fun setData(newData: ArrayList<CallModel?>) {
        listsearch = newData
        submitList(newData)
    }

    //class View_Holder extends RecyclerView.ViewHolder implements View.OnClickListener{
     class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById<View>(R.id.tv_name) as TextView
        var time: TextView = itemView.findViewById<View>(R.id.tv_time) as TextView
        var imageView: ImageView = itemView.findViewById<View>(R.id.iv_image) as ImageView
        var imageType: ImageView = itemView.findViewById<View>(R.id.image_call_type) as ImageView
        var imageStatuse: ImageView = itemView.findViewById<View>(R.id.image_call_status) as ImageView


        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(
            callModel: CallModel,
            context: Context,
            classSharedPreferences : ClassSharedPreferences?


        ) {
            name.text = callModel.username
            time.text = TimeProperties.getDate(callModel.createdAt.toLong(), "MMMM dd,h:mm aa")
            // Glide.with(holder.imageView.getContext()).load(model.getImage()).into(holder.imageView);
            if (!callModel.image.isEmpty()) {
                Glide.with(imageView.context).load(AllConstants.imageUrl + callModel.image)
                    .apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th))
                    .into(imageView)
            }
            if (callModel.call_type == "video") {
                imageType.setImageDrawable(context.getDrawable(R.drawable.ic_video_call))
            } else {
                imageType.setImageDrawable(context.getDrawable(R.drawable.ic_call_blue))
            }
            if (classSharedPreferences != null) {
                if (callModel.caller_id == classSharedPreferences.user.userId) {
                    imageStatuse.setImageDrawable(context.getDrawable(R.drawable.ic_out_going_call))
                } else {
                    imageStatuse.setImageDrawable(context.getDrawable(R.drawable.ic_incoming_call))
                }
            }
            when (callModel.call_status) {
                "missed call" -> {
                    imageStatuse.imageTintList =
                        ColorStateList.valueOf(context.resources.getColor(R.color.red))
                }
                "answer" -> {
                    imageStatuse.imageTintList =
                        ColorStateList.valueOf(context.resources.getColor(R.color.memo_background_color_new))
                }
                else -> {
                    imageStatuse.setImageDrawable(context.getDrawable(R.drawable.ic_close))
                    imageStatuse.imageTintList =
                        ColorStateList.valueOf(context.resources.getColor(R.color.red))
                }
            }
        }
        companion object {
            public fun from( parent: ViewGroup): ViewHolder {
                val v: View =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_calls, parent, false)
                //ChatRoomAdapter.View_Holder holder = new View_Holder(v,mOnClickListener);
                return ViewHolder(v)
            }
        }

    }

    class MyDiffUtilCall : DiffUtil.ItemCallback<CallModel>() {
        override fun areItemsTheSame(oldItem: CallModel, newItem: CallModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CallModel, newItem: CallModel): Boolean {
//        return 0 == oldItem.compareTo(newItem);
            return oldItem == newItem
        }
    }




}