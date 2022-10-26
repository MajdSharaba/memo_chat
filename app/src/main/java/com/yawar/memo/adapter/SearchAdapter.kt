package com.yawar.memo.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentProviderOperation
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yawar.memo.R
import com.yawar.memo.adapter.SearchAdapter.ViewHolders
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.fragment.SearchFragment
import com.yawar.memo.model.SearchRespone

class SearchAdapter(
///Initialize variable
    var searchFragment: SearchFragment, var activity: Activity,
) :
    ListAdapter<SearchRespone, ViewHolders?>(SearchDiffUtilCallBack()) {

    private var mCallback: CallbackInterface? = null

    interface CallbackInterface {

        fun onHandleSelection(position: Int, searchRespone: SearchRespone?)
        fun onClickItem(position: Int, searchRespone: SearchRespone?)
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
            holder.bind(model,activity,mCallback)


        } catch (e: Exception) {
            println(e)
        }
    }



    fun setData(newData: ArrayList<SearchRespone?>) {
        println("set data $newData")
        submitList(newData)
    }


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

     class ViewHolders private  constructor (itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView = itemView.findViewById(R.id.tv_name)
        var tvNumber: TextView = itemView.findViewById(R.id.tv_number)
        var imageView: ImageView = itemView.findViewById(R.id.iv_image)
        var button: Button = itemView.findViewById(R.id.btn_add)


        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(
            model: SearchRespone,
            activity: Activity,
            mCallback: CallbackInterface?

        ) {
            tvName.text = model.name
            tvNumber.text = model.phone
            println(model.image)

            if (model.image!!.isNotEmpty()) {
                Glide.with(imageView.context).load(AllConstants.imageUrl + model.image)
                    .apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th))
                    .into(imageView)
            } else {
                imageView.setImageDrawable(activity.resources.getDrawable(R.drawable.th))
            }
//            if (!contactExists(model.phone,activity)) {
//                button.visibility = View.VISIBLE
//            } else {
//                button.visibility = View.INVISIBLE
//            }
            itemView.setOnClickListener {
                Log.d("searchFragment", "bind: ")
                mCallback?.onClickItem(adapterPosition, model)
            }
            button.setOnClickListener {
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
                 val view: View =
                     LayoutInflater.from(parent.context).inflate(R.layout.item_search, parent, false)

                 return ViewHolders(view)
             }
         }

     }

    class SearchDiffUtilCallBack : DiffUtil.ItemCallback<SearchRespone>() {
        override fun areItemsTheSame(oldItem: SearchRespone, newItem: SearchRespone): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SearchRespone, newItem: SearchRespone): Boolean {
            return oldItem == newItem
        }
    }




}

