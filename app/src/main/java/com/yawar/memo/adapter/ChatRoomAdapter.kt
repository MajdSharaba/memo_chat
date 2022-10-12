package com.yawar.memo.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.chrisbanes.photoview.PhotoView
import com.yawar.memo.R
import com.yawar.memo.call.RequestCallActivity
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.fragment.ChatRoomFragment
import com.yawar.memo.model.ChatRoomModel
import com.yawar.memo.sessionManager.ClassSharedPreferences
import com.yawar.memo.utils.BaseApp
import com.yawar.memo.utils.MyDiffUtilCallBack
import com.yawar.memo.utils.TimeProperties
import com.yawar.memo.views.ConversationActivity
import com.yawar.memo.views.UserInformationActivity
import java.util.*

class ChatRoomAdapter(
    var context: ChatRoomFragment,
) : ListAdapter<ChatRoomModel, ChatRoomAdapter.ViewHolder>(MyDiffUtilCallBack()),Filterable {

    var listsearch: List<ChatRoomModel> = currentList
    var mCallback: CallbackInterfac? = null
    var classSharedPreferences: ClassSharedPreferences? = null


    interface CallbackInterfac {
        fun onHandleSelection(
            position: Int,
            chatRoomModel: ChatRoomModel?,
        )
    }
    init {
        try {
            mCallback = context
        } catch (ex: ClassCastException) {
            //.. should log the error or throw and exception
        }
        listsearch = currentList
        classSharedPreferences = BaseApp.getInstance().classSharedPreferences
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        val chatRoomModel = getItem(position)
        holder.bind(chatRoomModel, position,context, classSharedPreferences,mCallback)
    }

    override fun getFilter(): Filter? {
        return exampleFilter
    }

    private val exampleFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList: MutableList<ChatRoomModel> = ArrayList()
            if (constraint.isEmpty()) {
                filteredList.addAll(listsearch)
            } else {
                val filterPattern = constraint.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
                for (item in listsearch) {
                    if (item.username.lowercase(Locale.getDefault()).contains(filterPattern)) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {

            submitList(results.values  as ArrayList<ChatRoomModel>)
        }
    }



    fun setData(newData: ArrayList<ChatRoomModel?>) {
        listsearch = newData as List<ChatRoomModel>
        submitList(newData)
    }


    class ViewHolder private  constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById<View>(R.id.name) as TextView
        var lastMessage: TextView = itemView.findViewById<View>(R.id.lastMessage) as TextView
        var numUMessage: TextView = itemView.findViewById(R.id.num_message)
        var imageView: ImageView = itemView.findViewById<View>(R.id.image) as ImageView
        var imageType: ImageView = itemView.findViewById(R.id.img_type)
        var imageLasrMessageType: ImageView = itemView.findViewById<View>(R.id.img_state) as ImageView
        var textTime: TextView = itemView.findViewById(R.id.time)
        var linearLayout: LinearLayout = itemView.findViewById(R.id.liner_chat_room_row)
        var timeProperties = TimeProperties()


        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(
            chatRoomModel: ChatRoomModel,
            position: Int,
            context: ChatRoomFragment,
            classSharedPreferences: ClassSharedPreferences?,
            mCallback: CallbackInterfac?
        ) {
            var lastMessage = ""
            name.text = chatRoomModel.username

            textTime.text =
                timeProperties.getFormattedDate(context.activity, chatRoomModel.created_at.toLong())
            if (!chatRoomModel.isTyping) {
                this.lastMessage.setTextColor(context.resources.getColor(R.color.gray))
                when (chatRoomModel.message_type) {
                    "imageWeb" -> {
                        lastMessage = context.resources.getString(R.string.photo)
                        imageType.visibility = View.VISIBLE
                        imageType.setImageDrawable(context.resources.getDrawable(R.drawable.ic_select_image))
                    }
                    "voice" -> {
                        lastMessage = context.resources.getString(R.string.voice)
                        imageType.visibility = View.VISIBLE
                        imageType.setImageDrawable(context.resources.getDrawable(R.drawable.ic_voice))
                    }
                    "video" -> {
                        lastMessage = context.resources.getString(R.string.video)
                        imageType.visibility = View.VISIBLE
                        imageType.setImageDrawable(context.resources.getDrawable(R.drawable.ic_video))
                    }
                    "file" -> {
                        lastMessage = context.resources.getString(R.string.file)
                        imageType.visibility = View.VISIBLE
                        imageType.setImageDrawable(context.resources.getDrawable(R.drawable.ic_file))
                    }
                    "contact" -> {
                        lastMessage = context.resources.getString(R.string.contact)
                        imageType.visibility = View.VISIBLE
                        imageType.setImageDrawable(context.resources.getDrawable(R.drawable.ic_person))
                    }
                    "location" -> {
                        lastMessage = context.resources.getString(R.string.location)
                        imageType.visibility = View.VISIBLE
                        imageType.setImageDrawable(context.resources.getDrawable(R.drawable.ic_location))
                    }
                    else -> {
                        imageType.visibility = View.GONE
                        lastMessage = chatRoomModel.last_message
                    }
                }
                this.lastMessage.text = lastMessage
                if (chatRoomModel.num_msg == "0") numUMessage.visibility = View.GONE else {
                    numUMessage.visibility = View.VISIBLE
                    numUMessage.text = chatRoomModel.num_msg
                }
                if (chatRoomModel.msg_sender == classSharedPreferences!!.user.userId) {
                    imageLasrMessageType.visibility = View.VISIBLE
                    when (chatRoomModel.mstate) {
                        "1" -> {
                            imageLasrMessageType.setImageDrawable(context.resources.getDrawable(R.drawable.ic_send_done))
                            imageLasrMessageType.imageTintList =
                                ColorStateList.valueOf(context.resources.getColor(R.color.gray))
                        }
                        "2" -> {
                            imageLasrMessageType.setImageDrawable(context.resources.getDrawable(R.drawable.ic_recive_done))
                            imageLasrMessageType.imageTintList =
                                ColorStateList.valueOf(context.resources.getColor(R.color.gray))
                        }
                        "3" -> {
                            imageLasrMessageType.setImageDrawable(context.resources.getDrawable(R.drawable.ic_recive_done_green))
                            imageLasrMessageType.imageTintList = null
                        }
                        else -> {
                            imageLasrMessageType.setImageDrawable(context.resources.getDrawable(R.drawable.ic_not_send))
                            imageLasrMessageType.imageTintList =
                                ColorStateList.valueOf(context.resources.getColor(R.color.gray))
                        }
                    }
                } else {
                    imageLasrMessageType.visibility = View.GONE
                }
            } else {
                this.lastMessage.setTextColor(context.resources.getColor(R.color.green))
                imageType.visibility = View.GONE
                imageLasrMessageType.visibility = View.GONE
                this.lastMessage.text = context.resources.getString(R.string.writing_now)
                numUMessage.visibility = View.GONE
            }
            if (chatRoomModel.image.isNotEmpty()) {
                Glide.with(imageView.context).load(AllConstants.imageUrl + chatRoomModel.image)
                    .apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th))
                    .into(imageView)
            } else {
                imageView.setImageDrawable(context.resources.getDrawable(R.drawable.th))
            }
            itemView.setOnClickListener {
                if (mCallback != null) {
                    mCallback!!.onHandleSelection(position, chatRoomModel)
                }
            }

            imageView.setOnClickListener { view ->
                val my_id = classSharedPreferences!!.user.userId
                val mBuilder = AlertDialog.Builder(view.context)
                val mView =
                    LayoutInflater.from(view.context).inflate(R.layout.dialog_user_image_layout, null)
                val photoView: PhotoView = mView.findViewById(R.id.imageView)
                if (chatRoomModel.image.isNotEmpty()) {
                    Glide.with(photoView.context).load(AllConstants.imageUrl + chatRoomModel.image)
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
                    bundle.putString("user_id", chatRoomModel.other_id)
                    bundle.putString("name", chatRoomModel.username)
                    bundle.putString("image", chatRoomModel.image)
                    bundle.putString("fcm_token", chatRoomModel.user_token)
                    bundle.putString("special", chatRoomModel.sn)
                    bundle.putString("chat_id", chatRoomModel.id)
                    bundle.putString("blockedFor", chatRoomModel.blocked_for)
                    intent.putExtras(bundle)
                    view.context.startActivity(intent)
                    mDialog.dismiss()
                }
                val imgBtnChat = mView.findViewById<ImageButton>(R.id.btimg_chat)
                imgBtnChat.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("reciver_id", chatRoomModel.other_id)
                    bundle.putString("sender_id", my_id)
                    bundle.putString("fcm_token", chatRoomModel.user_token)
                    bundle.putString("name", chatRoomModel.username)
                    bundle.putString("image", chatRoomModel.image)
                    bundle.putString("chat_id", chatRoomModel.id)
                    bundle.putString("blockedFor", chatRoomModel.blocked_for)
//                    Log.d("blockedForrrrrrrrr",chatRoomModel.blocked_for!!)
                    val intent = Intent(context.activity, ConversationActivity::class.java)
                    intent.putExtras(bundle)
                    context.startActivity(intent)
                    mDialog.dismiss()
                }
                val imgBtnCall = mView.findViewById<ImageButton>(R.id.btimg_call)
                imgBtnCall.setOnClickListener { view ->
                    val intent = Intent(context.activity, RequestCallActivity::class.java)
                    intent.putExtra("anthor_user_id", chatRoomModel.other_id)
                    intent.putExtra("user_name", chatRoomModel.username)
                    intent.putExtra("isVideo", false)
                    intent.putExtra("fcm_token", chatRoomModel.user_token)
                    intent.putExtra("image_profile", chatRoomModel.image)
                    view.context.startActivity(intent)
                    mDialog.dismiss()
                }
                //                if(chatRoomModel.blocked_for.toString().equals(classSharedPreferences.getUser().getUserId())||chatRoomModel.blocked_for.toString().equals(chatRoomModel.getOther_id())||chatRoomModel.blocked_for.toString().equals("0")){
                imgBtnCall.isEnabled = chatRoomModel.blocked_for === "null"
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val v =
                    LayoutInflater.from(parent.context).inflate(R.layout.chat_room_row, parent, false)
                return ViewHolder(v)
            }
        }
    }




}