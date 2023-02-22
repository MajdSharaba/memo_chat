package com.yawar.memo.ui.searchPage

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentProviderOperation
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yawar.memo.ui.searchPage.SearchAdapter.ViewHolders
import com.yawar.memo.databinding.ItemSearchBinding
import com.yawar.memo.domain.model.SearchModel


class SearchAdapter(
///Initialize variable
    var searchFragment: SearchFragment, var activity: Activity,
) :
    PagingDataAdapter<SearchModel, ViewHolders>(SearchDiffUtilCallBack()) {

    private var mCallback: CallbackInterface? = null

    interface CallbackInterface {

        fun onHandleSelection(position: Int, searchRespone: SearchModel?)
        fun onClickItem(position: Int, searchRespone: SearchModel?)
    }
    init {
        try {
            mCallback = searchFragment
        } catch (ex: ClassCastException) {
            //.. should log the error or throw and exception
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolders {
        ///Initialize view
        return ViewHolders.from(parent)
    }

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(holder: ViewHolders, position: Int) {
        try {
//                if (arrayList.size() > 0) {
            val model = getItem(position)
            holder.bind(model!!,activity,mCallback)


        } catch (e: Exception) {
            println(e)
        }
    }



    fun setData(newData: ArrayList<SearchModel?>) {
        println("set data $newData")
    }//        submitList(newData)



    fun addToContact(name: String?, number: String?) {
        val ops = ArrayList<ContentProviderOperation>()
        ops.add(ContentProviderOperation.newInsert(
            ContactsContract.RawContacts.CONTENT_URI)
            .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
            .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
            .build())

        //------------------------------------------------------ Names
        if (name != null) {
            ops.add(ContentProviderOperation.newInsert(
                ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(
                    ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                    name).build())
        }

        //------------------------------------------------------ Mobile Number
        if (number != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build())
        }
        try {
            activity.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

     class ViewHolders private  constructor (val binding: ItemSearchBinding) : RecyclerView.ViewHolder(binding.root) {



        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(
            model: SearchModel,
            activity: Activity,
            mCallback: CallbackInterface?

        ) {

            binding.searchRespone = model
            binding.executePendingBindings()
            itemView.setOnClickListener {
                Log.d("searchFragment", "bind: ")
                mCallback?.onClickItem(adapterPosition, model)
            }
            binding.btnAdd.setOnClickListener {
                mCallback?.onHandleSelection(adapterPosition, model)
            }
        }


         private fun contactExists(number: String?, activity: Activity): Boolean {
             val lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                 Uri.encode(number))
             val mPhoneNumberProjection = arrayOf(ContactsContract.PhoneLookup._ID,
                 ContactsContract.PhoneLookup.NUMBER,
                 ContactsContract.PhoneLookup.DISPLAY_NAME)
             val cur =
                 activity.contentResolver.query(lookupUri, mPhoneNumberProjection, null, null, null)
             try {
                 if (cur!!.moveToFirst()) {
                     // if contact are in contact list it will return true
                     return true
                 }
             } finally {
                 cur?.close()
             }
             return false
         }


         companion object {
             fun from(parent: ViewGroup): ViewHolders {
                 val layoutInflater = LayoutInflater.from(parent.context)
                 val binding = ItemSearchBinding.inflate(layoutInflater,parent, false)

                 return ViewHolders(binding)
             }
         }

     }

    class SearchDiffUtilCallBack : DiffUtil.ItemCallback<SearchModel>() {
        override fun areItemsTheSame(oldItem: SearchModel, newItem: SearchModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SearchModel, newItem: SearchModel): Boolean {
            return oldItem == newItem
        }
    }




}

