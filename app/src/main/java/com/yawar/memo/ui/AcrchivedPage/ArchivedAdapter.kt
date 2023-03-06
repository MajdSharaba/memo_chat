package com.yawar.memo.ui.AcrchivedPage

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.chrisbanes.photoview.PhotoView
import com.yawar.memo.BaseApp
import com.yawar.memo.R
import com.yawar.memo.ui.requestCall.RequestCallActivity
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.databinding.ChatRoomRowBinding
import com.yawar.memo.domain.model.AnthorUserInChatRoomId
import com.yawar.memo.domain.model.ChatRoomModel
import com.yawar.memo.sessionManager.ClassSharedPreferences
import com.yawar.memo.utils.MyDiffUtilCallBack
import com.yawar.memo.utils.TimeProperties
import com.yawar.memo.ui.chatPage.ConversationActivity
import com.yawar.memo.ui.userInformationPage.UserInformationActivity
import com.yawar.memo.utils.checkThereIsOngoingCall
import java.util.*

class ArchivedAdapter(var context: Activity) :
    ListAdapter<ChatRoomModel?, ArchivedAdapter.ViewHolder?>(MyDiffUtilCallBack()),
    Filterable {
    var listsearch: MutableList<ChatRoomModel?> = ArrayList()
    var mCallback: CallbackInterfac? = null
    var timeProperties = TimeProperties()
    var classSharedPreferences: ClassSharedPreferences
    interface CallbackInterfac {
        fun onHandleSelection(position: Int, chatRoomModel: ChatRoomModel?)
    }
    init {
        try {
            mCallback = context as CallbackInterfac
        } catch (_: ClassCastException) {
        }
        listsearch.addAll(currentList)
        classSharedPreferences = BaseApp.instance?.classSharedPreferences!!
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        return ViewHolder.from(parent)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatRoomModel = getItem(position)

        holder.bind(chatRoomModel, position,context, classSharedPreferences,mCallback)
    }



    override fun getFilter(): Filter {
        return exampleFilter
    }

    private val exampleFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList: MutableList<ChatRoomModel?> = java.util.ArrayList()
            if (constraint.isEmpty()) {
                filteredList.addAll(listsearch)
            } else {
                val filterPattern = constraint.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
                for (item in listsearch) {
                    if (item!!.username?.lowercase(Locale.getDefault())?.contains(filterPattern)!!) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {

            submitList(results.values   as List<ChatRoomModel?>?)
        }
    }
    fun setData(newData: ArrayList<ChatRoomModel?>) {
        listsearch = newData
        submitList(newData)
    }

     class ViewHolder private  constructor(val binding: ChatRoomRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
         val  anthorUserInChatRoomId = AnthorUserInChatRoomId.getInstance("","","","","","","")

         @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(
            chatRoomModel: ChatRoomModel?,
            position: Int,
            context: Activity,
            classSharedPreferences: ClassSharedPreferences?,
            mCallback: CallbackInterfac?
        ) {

            binding.chatRoom = chatRoomModel
            binding.executePendingBindings()
            itemView.setOnClickListener {
                if (mCallback != null) {
                    mCallback.onHandleSelection(position, chatRoomModel)
                }
            }

            binding.image.setOnClickListener { view ->
                val my_id = classSharedPreferences!!.user.userId
                val mBuilder = AlertDialog.Builder(view.context)
                val mView =
                    LayoutInflater.from(view.context).inflate(R.layout.dialog_user_image_layout, null)
                val photoView: PhotoView = mView.findViewById(R.id.imageView)
                if (chatRoomModel?.image?.isNotEmpty()!!) {
                    Glide.with(photoView.context).load(AllConstants.imageUrl + chatRoomModel?.image)
                        .apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th))
                        .into(photoView)
                }
                mBuilder.setView(mView)
                val mDialog = mBuilder.create()
                mDialog.show()
                val imgBtnInfo = mView.findViewById<ImageButton>(R.id.btimg_info)
                imgBtnInfo.setOnClickListener { view ->
                    val intent = Intent(view.context, UserInformationActivity::class.java)
                    val bundle = Bundle()
                    bundle.putString("user_id", chatRoomModel?.other_id)
                    bundle.putString("name", chatRoomModel?.username)
                    bundle.putString("image", chatRoomModel?.image)
                    bundle.putString("fcm_token", chatRoomModel?.user_token)
                    bundle.putString("special", chatRoomModel?.sn)
                    bundle.putString("chat_id", chatRoomModel?.id)
                    bundle.putString("blockedFor", chatRoomModel?.blocked_for)
                    intent.putExtras(bundle)
                    view.context.startActivity(intent)
                    mDialog.dismiss()
                }
                val imgBtnChat = mView.findViewById<ImageButton>(R.id.btimg_chat)
                imgBtnChat.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("reciver_id", chatRoomModel?.other_id)
                    bundle.putString("sender_id", my_id)
                    bundle.putString("fcm_token", chatRoomModel?.user_token)
                    bundle.putString("name", chatRoomModel?.username)
                    bundle.putString("image", chatRoomModel?.image)
                    bundle.putString("chat_id", chatRoomModel?.id)
                    bundle.putString("blockedFor", chatRoomModel?.blocked_for)
//                    Log.d("blockedForrrrrrrrr",chatRoomModel.blocked_for!!)
                    anthorUserInChatRoomId.id = chatRoomModel?.other_id!!
                    anthorUserInChatRoomId.fcmToken = chatRoomModel?.user_token!!
                    anthorUserInChatRoomId.blockedFor = chatRoomModel?.blocked_for!!
                    anthorUserInChatRoomId.specialNumber = chatRoomModel?.sn!!
                    anthorUserInChatRoomId.userName = chatRoomModel.username
                    anthorUserInChatRoomId.chatId = chatRoomModel.id
                    anthorUserInChatRoomId.imageUrl = chatRoomModel.image

                    val intent = Intent(context, ConversationActivity::class.java)
                    intent.putExtras(bundle)
                    context.startActivity(intent)
                    mDialog.dismiss()
                }
                val imgBtnCall = mView.findViewById<ImageButton>(R.id.btimg_call)
                imgBtnCall.setOnClickListener { view ->
                    if (!checkThereIsOngoingCall()) {

                        val intent = Intent(context, RequestCallActivity::class.java)
                        intent.putExtra("anthor_user_id", chatRoomModel?.other_id)
                        intent.putExtra("user_name", chatRoomModel?.username)
                        intent.putExtra("isVideo", false)
                        intent.putExtra("fcm_token", chatRoomModel?.user_token)
                        intent.putExtra("image_profile", chatRoomModel?.image)
                        view.context.startActivity(intent)
                        mDialog.dismiss()
                    }
                }
                //                if(chatRoomModel.blocked_for.toString().equals(classSharedPreferences.getUser().getUserId())||chatRoomModel.blocked_for.toString().equals(chatRoomModel.getOther_id())||chatRoomModel.blocked_for.toString().equals("0")){
                imgBtnCall.isEnabled = chatRoomModel?.blocked_for === "null"
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ChatRoomRowBinding.inflate(layoutInflater,parent, false)
                return ViewHolder(binding)
            }
        }
    }





}