package com.yawar.memo.ui.CallHistoryPage

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yawar.memo.BaseApp
import com.yawar.memo.databinding.ItemCallsBinding
import com.yawar.memo.domain.model.CallHistoryModel
import com.yawar.memo.sessionManager.ClassSharedPreferences
import com.yawar.memo.utils.checkThereIsOngoingCall
import java.util.*
import kotlin.collections.ArrayList

class CallAdapter(var context: CallHistoryFragment) :
    ListAdapter<CallHistoryModel, CallAdapter.ViewHolder?>(MyDiffUtilCall()), Filterable {
    var listsearch: MutableList<CallHistoryModel?> = ArrayList()
    var classSharedPreferences: ClassSharedPreferences?
    var mCallback: CallbackInterface? = null

    interface CallbackInterface {
        fun onHandleSelection(position: Int, callModel:
        CallHistoryModel?)
    }
    init {
        listsearch.addAll(currentList)
        classSharedPreferences = BaseApp.instance?.classSharedPreferences
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
        return searchFilter
    }

    private val searchFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList: MutableList<CallHistoryModel?> = ArrayList()
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
            submitList(results.values as List<CallHistoryModel?>)
        }
    }


    fun getTypeFilter() : Filter{
        return typeFilter
    }

    private val typeFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList: MutableList<CallHistoryModel?> = ArrayList()
            println(listsearch.size.toString() + "listsearch.size()")
            if (constraint == null || constraint.length == 0) {
                filteredList.addAll(listsearch)
            } else {
                val filterPattern = constraint.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
                for (item in listsearch) {
                    if (item!!.call_status == filterPattern){
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            submitList(results.values as List<CallHistoryModel?>)
        }
    }

    fun setData(newData: ArrayList<CallHistoryModel?>) {
        listsearch = newData
        submitList(newData)
    }

    //class View_Holder extends RecyclerView.ViewHolder implements View.OnClickListener{
     class ViewHolder(val binding: ItemCallsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(
            callModel: CallHistoryModel,
            context: CallHistoryFragment,
            classSharedPreferences : ClassSharedPreferences?,
            mCallback: CallbackInterface?


        ) {

            binding.callModel = callModel
            binding.executePendingBindings()
            binding.imageCallType.setOnClickListener {
                if (!checkThereIsOngoingCall()) {

                    mCallback?.onHandleSelection(position, callModel)
                }
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

    class MyDiffUtilCall : DiffUtil.ItemCallback<CallHistoryModel>() {
        override fun areItemsTheSame(oldItem: CallHistoryModel, newItem: CallHistoryModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CallHistoryModel, newItem: CallHistoryModel): Boolean {
            return oldItem == newItem
        }
    }




}