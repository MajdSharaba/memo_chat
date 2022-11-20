package com.yawar.memo.ui.CallHistoryPage

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yawar.memo.databinding.ItemCallsBinding
import com.yawar.memo.model.CallModel
import com.yawar.memo.sessionManager.ClassSharedPreferences
import com.yawar.memo.utils.BaseApp
import java.util.*
import kotlin.collections.ArrayList

class CallAdapter(var context: CallHistoryFragment) :
    ListAdapter<CallModel, CallAdapter.ViewHolder?>(MyDiffUtilCall()), Filterable {
    var listsearch: MutableList<CallModel?> = ArrayList()
    var classSharedPreferences: ClassSharedPreferences?
    var mCallback: CallbackInterface? = null

    interface CallbackInterface {
        fun onHandleSelection(position: Int, callModel:
        CallModel?)
    }
    init {
        listsearch.addAll(currentList)
        classSharedPreferences = BaseApp.getInstance().classSharedPreferences
        try {
            mCallback = context as CallbackInterface
        } catch (ex: ClassCastException) {
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Inflate the layout, initialize the View Holder
        return ViewHolder.from(parent)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        val callModel = getItem(position)
        holder.bind(callModel,context,classSharedPreferences,mCallback)
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
     class ViewHolder(val binding: ItemCallsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(
            callModel: CallModel,
            context: CallHistoryFragment,
            classSharedPreferences : ClassSharedPreferences?,
            mCallback: CallbackInterface?


        ) {

            binding.callModel = callModel
            binding.executePendingBindings()
            binding.imageCallType.setOnClickListener {
                mCallback?.onHandleSelection(position, callModel)
            }
        }
        companion object {
            public fun from( parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCallsBinding.inflate(layoutInflater,parent, false)
               return ViewHolder(binding)
            }
        }

    }

    class MyDiffUtilCall : DiffUtil.ItemCallback<CallModel>() {
        override fun areItemsTheSame(oldItem: CallModel, newItem: CallModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CallModel, newItem: CallModel): Boolean {
            return oldItem == newItem
        }
    }




}