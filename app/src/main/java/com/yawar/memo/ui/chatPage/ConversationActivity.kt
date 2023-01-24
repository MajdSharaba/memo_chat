package com.yawar.memo.ui.chatPage
import android.Manifest
import android.animation.Animator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.NotificationManager
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Rect
import android.location.LocationManager
import android.media.MediaPlayer.create
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.View.OnLayoutChangeListener
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.devlomi.record_view.OnRecordListener
import com.devlomi.record_view.RecordPermissionHandler
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import com.yawar.memo.BuildConfig
import com.yawar.memo.R
import com.yawar.memo.ui.requestCall.RequestCallActivity
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.databinding.ActivityConversationBinding
import com.yawar.memo.ui.forwardPage.ForwardDialogFragment
import com.yawar.memo.model.ChatMessage
import com.yawar.memo.model.ChatRoomModel
import com.yawar.memo.model.UserModel
import com.yawar.memo.permissions.Permissions
import com.yawar.memo.repositry.BlockUserRepo
import com.yawar.memo.repositry.ChatRoomRepoo
//import com.yawar.memo.repositry.ChatRoomRepoo
import com.yawar.memo.service.SocketIOService
import com.yawar.memo.sessionManager.ClassSharedPreferences
import com.yawar.memo.utils.*
import com.yawar.memo.ui.dashBoard.DashBord
import com.yawar.memo.ui.userInformationPage.UserInformationActivity
import com.yawar.memo.ui.chatPage.video.VideoActivity
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.util.*
import javax.inject.Inject
import kotlin.math.log

@AndroidEntryPoint
class ConversationActivity : AppCompatActivity(), ChatAdapter.CallbackInterface,
    PickiTCallbacks , CallbackListener {
    lateinit var  binding: ActivityConversationBinding
    var mediaControl: MediaController? = null
    private val requestcode = 1
    var first = true
    var timeProperties: TimeProperties? = null
    @Inject
    lateinit var blockUserRepo: BlockUserRepo
//    lateinit var textForBlock: TextView
    var blockedForMe = false
//    lateinit var serverApi: ServerApi
//    lateinit var conversationModelView: ConversationModelView
    val conversationModelView by viewModels<ConversationModelView>()

    //    lateinit var conversationViewModelFactory: ConversationViewModelFactory
    private var adapter: ChatAdapter? = null
    val myBase: BaseApp? = BaseApp.getInstance()
//    var chatRoomRepoo: ChatRoomRepoImp? = null
        var chatRoomRepoo: ChatRoomRepoo? = null

    var chat_id = ""
    var fcmToken: String? = null
    var isAllMessgeMe = true
    var supportMapFragment: SupportMapFragment? = null
    var client: FusedLocationProviderClient? = null
    var latLng: LatLng? = null
    lateinit var stringLatLng: String
    var locationLatLng: LatLng? = null
//    @Inject
//    var chatMessageRepoo: ChatMessageRepoo
    var lat: String? = null
    var viewVisability = false
    private var userName: String? = null
    private var imageUrl: String? = null
    var bitmap: Bitmap? = null
    var imageString: String? = null
    var audioPath: String? = null
    var audioName: String? = null
    var returnValue = ArrayList<String>()
    private var chatHistory: ArrayList<ChatMessage?>? = null
    private val deleteMessage = ArrayList<String?>()
    private val hasConnection = false
    private var timer: Timer? = Timer()
    private val DELAY: Long = 1000
    var user_id = "8"
    var anthor_user_id = "9"
    var specialNumber = ""
    private var permissions: Permissions? = null
    private var mediaRecorder: MediaRecorder? = null
    var classSharedPreferences: ClassSharedPreferences? = null


    private val check: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val check = intent.extras!!.getString("check")
            var checkObject: JSONObject? = null
            var checkConnect: String? = "false"
            val userId: String
            try {
                checkObject = JSONObject(check.toString())
                userId = checkObject.getString("user_id")
                Log.d(TAG, "onReceive:${check+anthor_user_id+ userId}")

                if (userId == anthor_user_id) {

                    checkConnect = checkObject.getString("is_connect")
                    conversationModelView.lastSeen = checkObject.getString("last_seen")
                    conversationModelView.set_state(checkConnect)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }
    private val reciveTyping: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            runOnUiThread {
                val typingString = intent.extras!!.getString("typing")
                var message: JSONObject? = null
                var isTyping: String? = "false"
                var anthor_id = ""
                try {
                    message = JSONObject(typingString.toString())
                    isTyping = message.getString("typing")
                    anthor_id = message.getString("my_id")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                if (anthor_user_id == anthor_id) {
                    conversationModelView!!.set_isTyping(isTyping!!)
                }
            }
        }
    }
    private val reciveBlockUser: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            runOnUiThread {
                val blockString = intent.extras!!.getString("block")
                var userDoBlock = ""
                var userBlock = ""
                var blockedFor = ""
                var name = ""
                var image = ""
                var special_number = ""
                try {
                    val jsonObject = JSONObject(blockString.toString())
                    userDoBlock = jsonObject.getString("my_id")
                    userBlock = jsonObject.getString("user_id")
                    blockedFor = jsonObject.getString("blocked_for")
                    name = jsonObject.getString("userDoBlockName")
                    special_number = jsonObject.getString("userDoBlockSpecialNumber")
                    image = jsonObject.getString("userDoBlockImage")
                    Log.d(TAG, blockString + "from here")
                    if (userDoBlock == anthor_user_id) {
                        conversationModelView!!.setBlockedFor(blockedFor)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }
    private val reciveUnBlockUser: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            runOnUiThread {
                val unBlockString = intent.extras!!.getString("unBlock")
                var userDoUnBlock = ""
                var userUnBlock = ""
                var unBlockedFor = ""
                try {
                    val jsonObject = JSONObject(unBlockString.toString())
                    userDoUnBlock = jsonObject.getString("my_id")
                    userUnBlock = jsonObject.getString("user_id")
                    unBlockedFor = jsonObject.getString("blocked_for")
                    if (userDoUnBlock == anthor_user_id) {
                        conversationModelView!!.setBlockedFor(unBlockedFor)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }
    private val reciveDeleteMessage: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            runOnUiThread {
                val deleteString = intent.extras!!.getString("delete message")
                val message: JSONObject? = null
                try {
                    val jsonObject = JSONObject(deleteString.toString())
                    val deleteMessage = jsonObject.getString("message_to_delete")
                    val jsonArray = JSONArray(deleteMessage)
                    val deleteChat_id = jsonObject.getString("chat_id")
                    val first_user_id = jsonObject.getString("first_id")
                    val second_user_id = jsonObject.getString("second_id")
                    if (anthor_user_id == first_user_id || anthor_user_id == second_user_id) {
                        conversationModelView!!.deleteMessageFromList(jsonArray)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }
    private val reciveUpdateMessage: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            runOnUiThread {
                val deleteString = intent.extras!!.getString("updateMessage")
                val message: JSONObject? = null
                println(deleteString + "deleteRespone")
                try {
                    val jsonObject = JSONObject(deleteString.toString())
                    val message_id = jsonObject.getString("message_id")
                    val updateMessage = jsonObject.getString("message")
                    val first_user_id = jsonObject.getString("reciver_id")
                    val second_user_id = jsonObject.getString("sender_id")
                    if (anthor_user_id == first_user_id || anthor_user_id == second_user_id) {
                        conversationModelView!!.ubdateMessage(message_id, updateMessage)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }
    private val reciveNwMessage: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("SuspiciousIndentation")
        override fun onReceive(context: Context, intent: Intent) {
            runOnUiThread {
                val objectString = intent.extras!!.getString("message")
                var message: JSONObject? = null
                try {
                    message = JSONObject(objectString.toString())
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                println("this is message $message")
                var text: String? = ""
                var type = ""
                var state = ""
                var senderId = ""
                var reciverId = ""
                var id = ""
                var recive_chat_id = ""
                val fileName = ""
                var MessageDate: String? = ""
                try {
                    text = message!!.getString("message")
                    type = message.getString("message_type")
                    state = message.getString("state")
                    senderId = message.getString("sender_id")
                    id = message.getString("message_id")
                    reciverId = message.getString("reciver_id")
                    if(!text.equals("welcome to memo"))
                    MessageDate = message.getString("dateTime")
                    recive_chat_id = message.getString("chat_id")
                    //                        fileName = message.getString("orginalName");
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                if (senderId == user_id && reciverId == anthor_user_id) {
                    if (state != "3" && chat_id == user_id + anthor_user_id) {
                        //                            myBase.getObserver().setLastMessage(text, recive_chat_id, user_id, anthor_user_id, type, state, MessageDate);
                        if (!recive_chat_id.isEmpty()) {
                            chatRoomRepoo!!.setLastMessage(
                                text,
                                recive_chat_id,
                                user_id,
                                anthor_user_id,
                                type,
                                state,
                                MessageDate,
                                user_id
                            )
                            chat_id = recive_chat_id
                        }
                    } else {
                        if (id != "0000") {
                            chatRoomRepoo!!.setLastMessage(
                                text,
                                recive_chat_id,
                                user_id,
                                anthor_user_id,
                                type,
                                state,
                                MessageDate,
                                senderId
                            )
                        } else {
                            chatRoomRepoo!!.updateLastMessageState(state, chat_id)
                        }
                    }
                    conversationModelView!!.setMessageState(id, state)
                } else if (senderId == anthor_user_id) {
                    val jsonObject = JSONObject()
                    try {
                        jsonObject.put("message_id", id)
                        jsonObject.put("sender_id", senderId)
                        jsonObject.put("reciver_id", reciverId)
                        jsonObject.put("message", text)
                        jsonObject.put("message_type", type)
                        jsonObject.put("state", "3")
                        jsonObject.put("chat_id", recive_chat_id.toInt())
                        jsonObject.put(
                            "dateTime",
                            Calendar.getInstance(TimeZone.getTimeZone("GMT")).timeInMillis.toString()
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    onSeen(jsonObject)
                    val chatMessage = ChatMessage()
                    chatMessage.id = id
                    when (type) {
                        "text", "location" -> {
                            chatMessage.message = text!!
                        }
                        "file", "voice", "video", "contact" -> {
                            chatMessage.message = text!!
                            try {
                                chatMessage.fileName = message!!.getString("orginalName")
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                        else -> {
                            chatMessage.image = text!!
                            try {
                                chatMessage.fileName = message!!.getString("orginalName")
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    }
                    chatMessage.type = type
                    chatMessage.state = state
                    chatMessage.dateTime = MessageDate!!
                    chatMessage.isUpdate = "0"
                    chatMessage.isMe = false
                    if (type == "text" || type == "location" || type == "contact" || type == "video") displayMessage(
                        chatMessage
                    ) else processSocketFile(chatMessage)
                }
            }
        }
    }

    ////////////////// end recive from socket
    ////// start send to socket
    private fun EnterRoom() {
        val service = Intent(this, SocketIOService::class.java)
        val userEnter = JSONObject()
        try {
            userEnter.put("my_id", user_id)
            userEnter.put("your_id", anthor_user_id)
            userEnter.put("state", "3")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        service.putExtra(SocketIOService.EXTRA_ENTER_PARAMTERS, userEnter.toString())
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_ENTER)
        startService(service)
    }

    ////// check connect
    private fun checkConnect() {
        println("myId=" + user_id + "your_id=" + anthor_user_id)
        val service = Intent(this, SocketIOService::class.java)
        val `object` = JSONObject()
        try {
            `object`.put("my_id", user_id)
            `object`.put("your_id", anthor_user_id)
            //            socket.emit("check connect", object);
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        service.putExtra(SocketIOService.EXTRA_CHECK_CONNECT_PARAMTERS, `object`.toString())
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_CHECK_CONNECT)
        startService(service)
    }

    //////////////////// onTyping
    private fun onTyping(typing: Boolean) {
        val service = Intent(this, SocketIOService::class.java)
        val onTyping = JSONObject()
        try {
            onTyping.put("id", anthor_user_id)
            onTyping.put("typing", typing)
            onTyping.put("my_id", user_id)
            onTyping.put("chat_id", chat_id)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        service.putExtra(SocketIOService.EXTRA_TYPING_PARAMTERS, onTyping.toString())
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_TYPING)
        startService(service)
    }

    //////////onNewMessage
    fun newMeesage(chatMessage: JSONObject) {
        var message: String? = ""
        var type = ""
        var time: String? = "1646028789098"
        var message_id: String? = ""
        try {
            type = chatMessage.getString("message_type")
            time = chatMessage.getString("dateTime")
            message_id = chatMessage.getString("message_id")
            message = if (type == "text") {
                chatMessage.getString("message")
            } else if (type == "imageWeb" || type == "location") {
                "photo"
            } else {
                chatMessage.getString("orginalName")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (chat_id.isEmpty()) {
            chat_id = user_id + anthor_user_id
            chatRoomRepoo!!.addChatRoom(
                ChatRoomModel(
                    userName!!, anthor_user_id, message!!,
                    imageUrl!!, false, "0", user_id + anthor_user_id, "null", "0",
                    true, fcmToken!!, specialNumber, type, "1", time!!, false, "null", user_id, ""
                )
            )
        }
//        serverApi!!.sendNotification(
//            message,
//            type,
//            fcmToken,
//            chat_id,
//            conversationModelView!!.blockedFor().value,
//            message_id,
//                    time
//        )
        println("contact $chatMessage")
        val service = Intent(this, SocketIOService::class.java)
        service.putExtra(SocketIOService.EXTRA_NEW_MESSAGE_PARAMTERS, chatMessage.toString())
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_MESSAGE)
        startService(service)
    }

    /////////////////onSeen
    private fun onSeen(chatMessage: JSONObject) {
        val service = Intent(this, SocketIOService::class.java)
        service.putExtra(SocketIOService.EXTRA_ON_SEEN_PARAMTERS, chatMessage.toString())
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_ON_SEEN)
        startService(service)
    }

    private fun deletForAll(chatMessage: JSONObject) {
        val service = Intent(this, SocketIOService::class.java)
        service.putExtra(SocketIOService.EXTRA_ON_DELETE_PARAMTERS, chatMessage.toString())
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_ON_DELETE)
        startService(service)
    }

    private fun sendBlockFor(blocked: Boolean) {
        //                    item.put("blocked_for",conversationModelView.blockedFor().getValue());
//                    item.put("Block",blocked);
        Log.d(TAG, "sendBlockFor: ")
        val userBlocked = JSONObject()
        val item = JSONObject()
        val userModel = classSharedPreferences!!.user
        try {
            userBlocked.put("my_id", user_id)
            userBlocked.put("user_id", anthor_user_id)
            userBlocked.put("blocked_for", conversationModelView!!.blockedFor().value)
            userBlocked.put("userDoBlockName", userModel.userName)
            userBlocked.put("userDoBlockSpecialNumber", userModel.secretNumber)
            userBlocked.put("userDoBlockImage", userModel.image)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val service = Intent(this, SocketIOService::class.java)
        service.putExtra(SocketIOService.EXTRA_BLOCK_PARAMTERS, userBlocked.toString())
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_BLOCK)
        startService(service)
    }

    private fun sendUnBlockFor(blocked: Boolean) {
        val userUnBlocked = JSONObject()
        try {
            userUnBlocked.put("my_id", user_id)
            userUnBlocked.put("user_id", anthor_user_id)
            userUnBlocked.put("blocked_for", conversationModelView!!.blockedFor().value)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val service = Intent(this, SocketIOService::class.java)
        service.putExtra(SocketIOService.EXTRA_UN_BLOCK_PARAMTERS, userUnBlocked.toString())
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_UN_BLOCK)
        startService(service)
    }

    private fun updateMessage(chatMessage: JSONObject) {
        val service = Intent(this, SocketIOService::class.java)
        service.putExtra(SocketIOService.EXTRA_ON_UPDTE_MESSAGE_PARAMTERS, chatMessage.toString())
        service.putExtra(
            SocketIOService.EXTRA_EVENT_TYPE,
            SocketIOService.EVENT_TYPE_ON_UPDATE_MESSAGE
        )
        startService(service)
    }

    ///////////////end
    var progressNew = 0
    var sharedPreferences: SharedPreferences? = null
    var pickiT: PickiT? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_conversation)
        setSupportActionBar(binding.toolbar)
        pickiT = PickiT(this, this, this)
        if (!isPermissionGranted) {
            askPermissions()
        }
        pickiT = PickiT(this, this, this)
        initViews()
        initAction()
//        checkConnect()
        LocalBroadcastManager.getInstance(this).registerReceiver(check, IntentFilter(CHEK))
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveTyping, IntentFilter(TYPING))
        LocalBroadcastManager.getInstance(this).registerReceiver(
            reciveNwMessage, IntentFilter(
                ON_MESSAGE_RECEIVED
            )
        )
        LocalBroadcastManager.getInstance(this).registerReceiver(
            reciveDeleteMessage, IntentFilter(
                ON_MESSAGE_DELETED
            )
        )
        LocalBroadcastManager.getInstance(this).registerReceiver(
            reciveUpdateMessage, IntentFilter(
                ON_MESSAGE_UPDATE
            )
        )
        LocalBroadcastManager.getInstance(this).registerReceiver(
            reciveBlockUser, IntentFilter(
                ON_BLOCK_USER
            )
        )
        LocalBroadcastManager.getInstance(this).registerReceiver(
            reciveUnBlockUser, IntentFilter(
                ON_UN_BLOCK_USER
            )
        )
    }

    override fun onResume() {
        super.onResume()
        checkConnect()
        EnterRoom()
        scroll()
        closeCurrentNotification()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("hasConnection", hasConnection)
    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        timeProperties = TimeProperties()
//        serverApi = ServerApi(this)
        chatRoomRepoo = myBase?.chatRoomRepoo
//        blockUserRepo = myBase?.blockUserRepo
        val bundle = intent.extras
        user_id = bundle!!.getString("sender_id", "1")
        anthor_user_id = bundle.getString("reciver_id", "2")
        userName = bundle.getString("name", "user")
        imageUrl = bundle.getString("image")
        specialNumber = bundle.getString("special", "")
        chat_id = bundle.getString("chat_id", "")
        if (chat_id.isEmpty()) {
            if (chatRoomRepoo != null) {
                chat_id = chatRoomRepoo!!.getChatId(anthor_user_id)
            }
        }
        fcmToken = bundle.getString("fcm_token", "")
        if (!imageUrl!!.isEmpty()) {
            Glide.with(binding.userImage).load(AllConstants.imageUrl + imageUrl)
                .apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th))
                .into(binding.userImage)
        }
        val blockedFor = bundle.getString("blockedFor", "null")
//        conversationViewModelFactory = ConversationViewModelFactory(anthor_user_id,blockedFor)
//        conversationModelView = ViewModelProvider(this,conversationViewModelFactory).get(
//            ConversationModelView::class.java
//        )
//        chatMessageRepoo = myBase?.chatMessageRepoo
        binding.messagesContainer.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.messagesContainer.layoutManager = linearLayoutManager
        permissions = Permissions()
        supportMapFragment = supportFragmentManager
            .findFragmentById(R.id.google_map) as SupportMapFragment?
//////////
//        val messageSwipeController = MessageSwipeController(this, object : SwipeControllerActions {
//            override fun showReplyUI(position: Int) {
//                showQuotedMessage(chatHistory!![position]!!)
//            }
//        })
//
//        val itemTouchHelper = ItemTouchHelper(messageSwipeController)
//        itemTouchHelper.attachToRecyclerView(binding.messagesContainer)
        /////////////
        client = LocationServices.getFusedLocationProviderClient(this)
        binding.recordButton.setRecordView(binding.recordView)
        ////////
        binding.cancelButton.setOnClickListener {
            hideReplyLayout()
        }
        /////////////
        binding.recordButton.isListenForRecord = false
        classSharedPreferences = BaseApp.getInstance().classSharedPreferences
        chatRoomRepoo?.setInChat(anthor_user_id, true)
        conversationModelView!!.blockedFor().observe(
            this
        ) { s ->
            var isAnyOneBlock = false
            if (s != null) {
                when (s) {
                    user_id -> {
                        binding.textForBlock.text = resources.getString(R.string.block_message)
                        binding.audioCall.isEnabled = false
                        binding.videoCall.isEnabled = false
                        binding.state.visibility = View.GONE
                        binding.textForBlock.visibility = View.VISIBLE
                        binding.liner.visibility = View.GONE
                        blockedForMe = true
                        isAnyOneBlock = true
                    }
                    anthor_user_id -> {
                        binding.textForBlock.visibility = View.VISIBLE
                        binding.liner.visibility = View.GONE
                        binding.audioCall.isEnabled = false
                        binding.videoCall.isEnabled = false
                        binding.state.visibility = View.GONE
                        binding.textForBlock.text = resources.getString(R.string.block_message2)
                        blockedForMe = false
                        isAnyOneBlock = true
                    }
                    "0" -> {
                        binding.textForBlock.visibility = View.VISIBLE
                        binding.liner.visibility = View.GONE
                        binding.audioCall.isEnabled = false
                        binding.videoCall.isEnabled = false
                        binding.state.visibility = View.GONE
                        binding.textForBlock.text = resources.getString(R.string.block_message2)
                        blockedForMe = true
                        isAnyOneBlock = true
                    }
                }
            } else {
                binding.textForBlock.visibility = View.GONE
                binding.liner.visibility = View.VISIBLE
                blockedForMe = false
                isAnyOneBlock = false
            }
            if (!isAnyOneBlock) {
                binding.textForBlock.visibility = View.GONE
                binding.liner.visibility = View.VISIBLE
                binding.audioCall.isEnabled = true
                binding.videoCall.isEnabled = true
                binding.state.visibility = View.VISIBLE
                blockedForMe = false
            }
        }
        ////
        conversationModelView!!.isBlocked.observe(
            this
        ) { s ->
            if (s != null) {
                if (s) {
                    sendBlockFor(s)
                    conversationModelView!!.setBlocked(false)
                }
            }
        }
        ////////////
        conversationModelView!!.isUnBlocked.observe(
            this
        ) { s ->
            println("stateee$s")
            if (s != null) {
                //                    conversationModelView.
                if (s) {
                    sendUnBlockFor(s)
                    conversationModelView!!.setUnBlocked(false)
                }
            }
        }

        /////////
        chatHistory = ArrayList()
        conversationModelView!!.state.observe(this) { s ->
            println("stateee$s")
            if (s != null) {
                if (s == "true") {
                    binding.state.visibility = View.VISIBLE
                    binding.state.setText(R.string.connect_now)
                } else if (s == "false") {
                    if (conversationModelView!!.lastSeen != "null") {
                        binding.state.visibility = View.VISIBLE
                        binding.state.text = resources.getString(R.string.last_seen) + " " + timeProperties!!.getDateForLastSeen(
                            this,
                            conversationModelView!!.lastSeen.toLong()
                        )
                    } else {
                        binding.state.visibility = View.GONE
                    }
                }
            } else {
                binding.state.visibility = View.GONE
            }
        }
        conversationModelView!!.isTyping.observe(
            this
        ) { s ->
            println("stateee$s")
            if (s != null) {
                if (s == "true") {
                    binding.state.setText(R.string.writing_now)
                } else if (conversationModelView.state.value == "true") {
                    binding.state.setText(R.string.connect_now)
                } else {
                    if (conversationModelView.lastSeen != "null") binding.state.text = resources.getString(
                        R.string.last_seen
                    ) + " " + timeProperties!!.getDateForLastSeen(
                        this,
                        conversationModelView!!.lastSeen.toLong()
                    )
                }
            }
        }
        conversationModelView!!.getChatMessaheHistory().observe(this,
            Observer<ArrayList<ChatMessage?>?> { chatMessages ->
                if (chatMessages != null) {
                    Log.d(TAG, "initViews: "+chatMessages)

                    if (chatMessages.isEmpty()) {
                        binding.linerNoMesssage.visibility = View.VISIBLE
                        binding.messagesContainer.visibility = View.GONE
                    } else {

                        val list = ArrayList<ChatMessage?>()
                        for (chatMessage in chatMessages) {
                            list.add(chatMessage?.clone())
                        }
                        chatHistory = list
                        //                    adapter.add(chatHistory);
                        adapter!!.setData(list)

                        if (conversationModelView!!.isFirst.value!!) {
                        conversationModelView!!.setIsFirst(false)
                            if (conversationModelView!!.getChatMessaheHistory().value!!.size > 0) {
                                binding.messagesContainer.scrollToPosition(conversationModelView!!.getChatMessaheHistory().value!!.size - 1)
                            }
                    }
                        binding.linerNoMesssage!!.visibility = View.GONE
                        binding.messagesContainer.visibility = View.VISIBLE
                    }
//                    if (!chatMessages.isEmpty() && conversationModelView!!.isFirst.value!!) {
//                        conversationModelView!!.setIsFirst(false)
//                        scroll()
//                    }
                }
            })
        //        scroll();
        conversationModelView.selectedMessage.observe(
            this
        ) { chatMessages ->
            if (chatMessages != null) {
                Log.d(TAG, "initViews: ${chatMessages.size}")

                if (chatMessages.isEmpty()) {
                    binding.toolsLinerLayout.visibility = View.GONE
                    binding.personInformationLiner.visibility = View.VISIBLE
                    binding.toolbar.setBackgroundColor(resources.getColor(R.color.memo_background_color))
                }
            }
        }
        conversationModelView!!.getLoading().observe(
            this
        ) { aBoolean ->
            if (aBoolean != null) {
                if (aBoolean) {
                    binding.progressCircular.visibility = View.VISIBLE
                    binding.messagesContainer.visibility = View.GONE
                    binding.linerNoMesssage.visibility = View.GONE
                } else {
                    binding.progressCircular.visibility = View.GONE
                    binding.messagesContainer.visibility = View.VISIBLE
                    binding.linerNoMesssage.visibility = View.VISIBLE
                }
            }
        }
        conversationModelView!!.getErrorMessage().observe(
            this
        ) { aBoolean ->
            if (aBoolean != null) {
                if (aBoolean) {
                    Toast.makeText(
                        this,
                        R.string.internet_message,
                        Toast.LENGTH_LONG
                    ).show()
                    conversationModelView!!.setErrorMessage(false)
                }
            }
        }
        adapter = ChatAdapter(this)
        binding.messagesContainer.adapter = adapter
        binding.messagesContainer.addOnLayoutChangeListener(OnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom) {
                binding.messagesContainer.postDelayed(Runnable {
                    if (adapter!!.currentList.size > 1)  binding.messagesContainer.smoothScrollToPosition(
                        adapter!!.currentList.size - 1
                    )
                }, 100)
            }
        })
        binding.pickLocation.setOnClickListener(View.OnClickListener {
            hideLayout()
            makeMapsAction()
            binding.container.visibility = View.GONE
            binding.relativeMaps.visibility = View.VISIBLE
            binding.sendLocation.visibility = View.VISIBLE

            //
        })
        binding.sendLocation.setOnClickListener(View.OnClickListener {
            if (stringLatLng == null) {
                return@OnClickListener
            }
            hideLayout()
            println(stringLatLng + "stringLatLng")
            val message_id = System.currentTimeMillis().toString() + "_" + user_id
            val jsonObject = JSONObject()
            val notification = JSONObject()

            try {
                jsonObject.put("sender_id", user_id)
                jsonObject.put("reciver_id", anthor_user_id)
                jsonObject.put("message", stringLatLng)
                jsonObject.put("message_type", "location")
                jsonObject.put("state", "0")
                jsonObject.put("message_id", message_id)
                jsonObject.put(
                    "dateTime",
                    Calendar.getInstance(TimeZone.getTimeZone("GMT")).timeInMillis.toString()
                )
                notification.put("token", fcmToken)
                notification.put("my_token", classSharedPreferences?.fcmToken)
                notification.put("image", classSharedPreferences?.user!!.image)
                notification.put("title", classSharedPreferences?.user?.userName +" "+ classSharedPreferences?.user?.lastName )
                notification.put("chat_id", chat_id)
                notification.put("blockedFor",conversationModelView!!.blockedFor().value,
                )
                jsonObject.put("notification",notification)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            binding.relativeMaps.visibility = View.GONE
            binding.container.visibility = View.VISIBLE
            val chatMessage = ChatMessage()
            chatMessage.id = message_id //dummy
            chatMessage.message = stringLatLng
            chatMessage.dateTime =
                Calendar.getInstance(TimeZone.getTimeZone("GMT")).timeInMillis.toString()
            chatMessage.isMe = true
            chatMessage.type = "location"
            chatMessage.state = "0"
            chatMessage.isChecked = false
            displayMessage(chatMessage)
            newMeesage(jsonObject)
        })
    }

    private fun initAction() {
        binding.name!!.text = userName
        //////////
        binding.messagesContainer.setOnClickListener {
            println("pressssed") }
        /////////
        binding.nameState!!.setOnClickListener { view ->
            val intent = Intent(view.context, UserInformationActivity::class.java)
            val bundle = Bundle()
            bundle.putString("user_id", anthor_user_id)
            bundle.putString("name", userName)
            bundle.putString("image", imageUrl)
            bundle.putString("fcm_token", fcmToken)
            bundle.putString("special", specialNumber)
            bundle.putString("chat_id", chat_id)
            bundle.putString("blockedFor", conversationModelView!!.blockedFor().value)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        binding.imageButtonBack.setOnClickListener { finish() }
        binding.messageEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (timer != null) {
                    timer!!.cancel()
                }
                //
                onTyping(true)
                if (charSequence.toString().trim { it <= ' ' }.isNotEmpty()) {
                    binding.btnSendMessageText.isEnabled = true
                    binding.recordButton.visibility = View.GONE
                    binding.btnSendMessageText.visibility = View.VISIBLE
                } else {
                    binding.btnSendMessageText.isEnabled = false
                    binding.btnSendMessageText.visibility = View.GONE
//                    binding.cardview.visibility =
//                        View.GONE
                    binding.recordButton.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(editable: Editable) {
                if (editable.length >= 0) {
                    timer = Timer()
                    timer!!.schedule(object : TimerTask() {
                        override fun run() {
                            // TODO: do what you need here (refresh list)
                            onTyping(false)
                        }
                    }, DELAY)
                }
            }
        })
        ///for send textMessage
        binding.btnSendMessageImage.setOnClickListener { // MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.song);
            if (!viewVisability) {
                showLayout()
            } else hideLayout()
        } ///////////////////

        /////////////////////////send btn
        binding.videoCall.setOnClickListener { //
            if(!checkThereIsOngoingCall()) {
                val intent = Intent(this, RequestCallActivity::class.java)
                //                Intent intent = new Intent(ConversationActivity.this, CompleteActivity.class);
                intent.putExtra("anthor_user_id", anthor_user_id)
                intent.putExtra("user_name", userName)
                intent.putExtra("isVideo", true)
                intent.putExtra("fcm_token", fcmToken)
                intent.putExtra("image_profile", imageUrl)
                startActivity(intent)
            }
        }
        binding.audioCall.setOnClickListener {
            if(!checkThereIsOngoingCall()) {

                val intent = Intent(this, RequestCallActivity::class.java)
                intent.putExtra("anthor_user_id", anthor_user_id)
                intent.putExtra("user_name", userName)
                intent.putExtra("isVideo", false)
                intent.putExtra("fcm_token", fcmToken)
                intent.putExtra("image_profile", imageUrl)
                startActivity(intent)
            }
        }
        binding.btnSendMessageText.setOnClickListener(View.OnClickListener {
//            binding.username.visibility = View.GONE
//            binding.reply.visibility = View.GONE
            val message_id = System.currentTimeMillis().toString() + "_" + user_id
            val messageText = binding.messageEdit.text.toString()
            if (TextUtils.isEmpty(messageText)) {
                return@OnClickListener
            }
            val jsonObject = JSONObject()
            val notification = JSONObject()


            try {
                jsonObject.put("sender_id", user_id)
                jsonObject.put("reciver_id", anthor_user_id)
                jsonObject.put("message", messageText)
                jsonObject.put("message_type", "text")
                jsonObject.put("state", "0")
                jsonObject.put("message_id", message_id)
                jsonObject.put(
                    "dateTime",
                    Calendar.getInstance(TimeZone.getTimeZone("GMT")).timeInMillis.toString()
                )
                notification.put("token", fcmToken)
                notification.put("my_token", classSharedPreferences?.fcmToken)
                notification.put("image", classSharedPreferences?.user!!.image)
                notification.put("title", classSharedPreferences?.user?.userName +" "+ classSharedPreferences?.user?.lastName )
                notification.put("chat_id", chat_id)
                notification.put("blockedFor",conversationModelView!!.blockedFor().value,
                )
                jsonObject.put("notification",notification)
                
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val chatMessage = ChatMessage()
            chatMessage.id = message_id //dummy
            chatMessage.message = messageText
            chatMessage.dateTime =
            Calendar.getInstance(TimeZone.getTimeZone("GMT")).timeInMillis.toString()
            chatMessage.isMe = true
            chatMessage.userId = user_id
            chatMessage.type = "text"
            chatMessage.state = "0"
            chatMessage.isChecked = false
            chatMessage.id = message_id
            chatMessage.isUpdate = "0"
            binding.messageEdit.setText("")
            displayMessage(chatMessage)
            newMeesage(jsonObject)
        })
        binding.searchCon.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
//                adapter.filter(newText);
                return false
            }
        })
        ////to pick image
        binding.lytCameraPick.setOnClickListener {
            hideLayout()
            val options = Options.init()
                .setRequestCode(PICK_IMAGE_VIDEO) //Request code for activity results
                .setCount(1)
                .setFrontfacing(false) //Front Facing camera on start
                .setSpanCount(4) //Span count for gallery min 1 & max 5
                .setMode(Options.Mode.All) //Option to select only pictures or videos or both
                .setVideoDurationLimitinSeconds(30)
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
            Pix.start(this, options)
        }
        ////pick from gallery
        binding.lytGallaryPick.setOnClickListener {
            hideLayout()
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
            startActivityForResult(
                intent,
                PICK_IMAGE_FROM_GALLERY
            )
        }
        //// to pick file
        binding.pickFile.setOnClickListener {
            hideLayout()
            println("file")
            askPermissionAndBrowseFile()
        }
        binding.pickContact.setOnClickListener {
            hideLayout()
            checkContactpermission()
        }
        //// for voice record
        binding.recordButton.isListenForRecord = true
        binding.recordButton.setOnClickListener { view: View? -> }
        binding.recordView.setLessThanSecondAllowed(false)
        binding.recordView.setRecordPermissionHandler(RecordPermissionHandler {
            if (permissions!!.isRecordingOk(this)) if (permissions!!.isStorageReadOk(
                    this
                )
            ) return@RecordPermissionHandler true else permissions!!.requestStorage(this) else permissions!!.requestRecording(
                this
            )
            false
        })
        binding.recordView.setSlideToCancelText(resources.getString(R.string.slide_to_cancel))
        binding.recordView.setCustomSounds(0, R.raw.record_finished, 0)
        binding.recordView.timeLimit = 60000 //30 sec
        binding.recordView.setOnRecordListener(object : OnRecordListener {
            override fun onStart() {
                //Start Recording..
                setUpRecording()
                try {
                    mediaRecorder!!.prepare()
                    mediaRecorder!!.start()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                binding.messageLayout.visibility = View.GONE
                binding.recordView.visibility = View.VISIBLE
            }

            override fun onCancel() {
                //On Swipe To Cancel
                mediaRecorder!!.reset()
                mediaRecorder!!.release()
                val file = File(audioPath.toString())
                if (file.exists()) file.delete()
                binding.recordView.visibility = View.GONE
                binding.messageLayout.visibility = View.VISIBLE
            }

            override fun onFinish(recordTime: Long, limitReached: Boolean) {
                //Stop Recording..
                try {
                    mediaRecorder!!.stop()
                    mediaRecorder!!.release()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                binding.recordView.visibility = View.GONE
                binding.messageLayout.visibility = View.VISIBLE
                val f = File(audioPath.toString())
                val chatMessage = FileUtil.uploadVoice(
                    audioName,
                    Uri.fromFile(f),
                    this@ConversationActivity,
                    user_id,
                    anthor_user_id
                ,conversationModelView!!.blockedFor().value,
                    fcmToken)
                displayMessage(chatMessage)
            }

            override fun onLessThanSecond() {
                //When the record time is less than One Second
                mediaRecorder!!.reset()
                mediaRecorder!!.release()
                val file = File(audioPath.toString())
                if (file.exists()) file.delete()
                binding.recordView.visibility = View.GONE
                binding.messageLayout.visibility = View.VISIBLE
            }
        })

        binding.imageButtonDelete.setOnClickListener { alertDeleteDialog() }
        binding.imageButtonFoword.setOnClickListener {
            val fm = supportFragmentManager
            val forwardDialogFragment = ForwardDialogFragment.newInstance(
                conversationModelView?.selectedMessage?.value!!, "jj"
            )
            forwardDialogFragment.show(fm, "fragment_edit_name")
        }
    }

    @SuppressLint("ResourceType")
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.conversation_menu, menu)
        return true
    }

    @SuppressLint("ResourceType")
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val copyItem = menu.findItem(R.id.item_copy)
        val blockItem = menu.findItem(R.id.item_block)
        val unblockItem = menu.findItem(R.id.item_unblock)
        val updateItem = menu.findItem(R.id.item_update)
        println("preparing message")
        if (conversationModelView!!.selectedMessage.value != null) {
            if (conversationModelView!!.selectedMessage.value!!.size > 1) {
                copyItem.isVisible = false
                updateItem.isVisible = false
            } else if (conversationModelView!!.selectedMessage.value!!.size == 1) {
                if (conversationModelView!!.selectedMessage.value!![0]!!.type == "text") {
                    copyItem.isVisible = true
                    updateItem.isVisible =
                        conversationModelView!!.selectedMessage.value!![0]!!.userId == user_id
                } else {
                    updateItem.isVisible = false
                    copyItem.isVisible = false
                }
            } else {
                copyItem.isVisible = false
                updateItem.isVisible = false
            }
        } else {
            copyItem.isVisible = false
            updateItem.isVisible = false
        }
        //// for block
        if (blockedForMe) {
            blockItem.isVisible = false
            unblockItem.isVisible = true
        } else {
            blockItem.isVisible = true
            unblockItem.isVisible = false
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val userModel = UserModel(anthor_user_id, userName, "", "", "", specialNumber, imageUrl, "")
        when (id) {
            R.id.item_copy -> {
                val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText(
                    "key",
                    conversationModelView!!.selectedMessage.value!![0]!!.message
                )
                clipboardManager.setPrimaryClip(clipData)
                conversationModelView!!.setMessageChecked(
                    conversationModelView!!.selectedMessage.value!![0]!!.id, false
                )
                conversationModelView!!.clearSelectedMessage()
                deleteMessage.clear()
                return true
            }
            R.id.item_update -> {
                showUpdateMessageDialog(conversationModelView!!.selectedMessage.value!![0]!!)
                return true
            }
            R.id.item_block -> {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle(R.string.alert_block_user)
                dialog.setPositiveButton(
                    R.string.block
                ) { _, _ ->
                    conversationModelView!!.sendBlockRequest(
                        user_id,
                        anthor_user_id
                    )
                }
                dialog.setNegativeButton(
                    R.string.cancel
                ) { dialog, _ -> dialog.dismiss() }
                val alertDialog = dialog.create()
                alertDialog.show()
            }
            R.id.item_unblock -> {
                val dialogUnBlock = AlertDialog.Builder(this)
                dialogUnBlock.setTitle(R.string.alert_unblock_user)
                dialogUnBlock.setPositiveButton(
                    R.string.Unblock
                ) { _, _ -> //                                serverApi.unbBlockUser(user_id, userModel);
                    conversationModelView!!.sendUnBlockRequest(user_id, anthor_user_id)
                }
                dialogUnBlock.setNegativeButton(
                    R.string.cancel
                ) { dialog, which -> dialog.dismiss() }
                val alertUnBlockDialog = dialogUnBlock.create()
                alertUnBlockDialog.show()
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val viewRect = Rect()
        binding.dataLayout.getGlobalVisibleRect(viewRect)
        if ( binding.dataLayout.visibility == View.VISIBLE && !viewRect.contains(
                ev.rawX.toInt(),
                ev.rawY.toInt()
            )
        ) {
            hideLayout()
            return true
        }
//        val v = currentFocus
//        if (v != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) &&
//            v is EditText &&
//            !v.javaClass.name.startsWith("android.webkit.")
//        ) {
//            val sourceCoordinates = IntArray(2)
//            v.getLocationOnScreen(sourceCoordinates)
//            val x = ev.rawX + v.getLeft() - sourceCoordinates[0]
//            val y = ev.rawY + v.getTop() - sourceCoordinates[1]
//            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
//                hideKeyboard(this)
//            }
//        }
        return super.dispatchTouchEvent(ev)
    }

    ///// End initialAction
    private fun setUpRecording() {
        mediaRecorder = MediaRecorder()
        mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.DEFAULT)
        mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
        mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        val file = File(
            getExternalFilesDir(Environment.DIRECTORY_DCIM)!!.absolutePath, "memo/send/voiceRecord"
        )
        if (!file.exists()) file.mkdirs()
        audioName = System.currentTimeMillis().toString() + ".mp3"
        audioPath = file.absolutePath + "/" + audioName
        mediaRecorder!!.setOutputFile(audioPath)
    }
    private fun askPermissionAndBrowseFile() {
        val permisson = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (permisson != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                MY_REQUEST_CODE_PERMISSION
            )
            return
        }
        doBrowseFile()
    }
    fun displayMessage(message: ChatMessage?) {
        conversationModelView!!.addMessage(message)
        scroll()
    }

    /////for browse file from device
    private fun doBrowseFile() {
        var chooseFileIntent = Intent(Intent.ACTION_GET_CONTENT)
        chooseFileIntent.type = "application/pdf"
        chooseFileIntent.addCategory(Intent.CATEGORY_OPENABLE)
        chooseFileIntent = Intent.createChooser(chooseFileIntent, "Choose a file")
        startActivityForResult(chooseFileIntent, MY_RESULT_CODE_FILECHOOSER)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_REQUEST_CODE_PERMISSION -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    doBrowseFile()
                } else {
                    DialogProperties.showPermissionDialog(
                        resources.getString(R.string.read_premission),
                        AllConstants.READ_STORAGE_PERMISSION_REJECT,
                        this
                    )
                }
            }
            AllConstants.RECORDING_REQUEST_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (this.permissions!!.isStorageReadOk(this)) return else this.permissions!!.requestStorage(
                        this
                    )
                } else DialogProperties.showPermissionDialog(
                    resources.getString(R.string.record_voice_premission),
                    AllConstants.RECORD_AUDIO_PERMISSION_REJECT,
                    this
                )
            }
            AllConstants.STORAGE_REQUEST_CODE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) return else  //                    showPermissionDialog(getResources().getString(R.string.read_premission), 1777);
                DialogProperties.showPermissionDialog(
                    resources.getString(R.string.read_premission),
                    AllConstants.STORAGE_PERMISSION_REJECT,
                    this
                )
            AllConstants.LOCATION_PERMISSION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                currentLocation
            } else {
                DialogProperties.showPermissionDialog(
                    resources.getString(R.string.location_premission),
                    AllConstants.LOCATION_PERMISSION_REJECT,
                    this
                )
            }
            AllConstants.OPEN_MAP_PERMISSION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                openMap()
            } else {
                DialogProperties.showPermissionDialog(
                    resources.getString(R.string.location_premission),
                    AllConstants.OPEN_MAP_PERMISSION_REJECT,
                    this
                )
            }
            AllConstants.OPEN_CAMERA_PERMISSION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val options = Options.init()
                    .setRequestCode(PICK_IMAGE_VIDEO) //Request code for activity results
                    .setCount(1) //Number of images to restict selection count
                    .setFrontfacing(false) //Front Facing camera on start
                    .setSpanCount(
                        4
                    ) //Span count for gallery min 1 & max 5
                    .setMode(Options.Mode.All) //Option to select only pictures or videos or both
                    .setVideoDurationLimitinSeconds(30) //Duration for video recording
                    .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT) //Orientaion
                //Custom Path For media Storage
                Pix.start(this, options)
            } else {
                DialogProperties.showPermissionDialog(
                    resources.getString(R.string.camera_premission),
                    AllConstants.CAMERA_PERMISSION_REJECT,
                    this
                )
            }
            AllConstants.CONTACTS_REQUEST_CODE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val `in` =
                    Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
                startActivityForResult(`in`, RESULT_PICK_CONTACT)
            } else {
                if ( !shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_CONTACTS
                    )
                ) {
                    DialogProperties.showPermissionDialog(
                        resources.getString(R.string.contact_permission),
                        AllConstants.READ_CONTACT_PERMISSION_REJECT,
                        this
                    )
                }
            }
        }
    }

    private fun showLayout() {
        val radius = Math.max( binding.dataLayout.width,  binding.dataLayout.height).toFloat()
        val animator =
            ViewAnimationUtils.createCircularReveal( binding.dataLayout, binding.dataLayout.left,  binding.dataLayout.top, 0f, radius * 2)
        animator.duration = 800
       binding.dataLayout.visibility = View.VISIBLE
        viewVisability = true
        animator.start()
    }

    private fun hideLayout() {
        val radius = Math.max( binding.dataLayout.width,  binding.dataLayout.height).toFloat()
        val animator =
            ViewAnimationUtils.createCircularReveal( binding.dataLayout,  binding.dataLayout.left,  binding.dataLayout.top, radius * 2, 0f)
        animator.duration = 800
        viewVisability = false
        binding.dataLayout.visibility = View.INVISIBLE
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                binding.dataLayout.visibility = View.INVISIBLE
                viewVisability = false
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })
    }

    private fun scroll() {
        binding.messagesContainer.postDelayed({
            if(conversationModelView?.getChatMessaheHistory()?.value?.size!=null)
            if (conversationModelView?.getChatMessaheHistory()?.value?.size!! > 0) {
                binding.messagesContainer.scrollToPosition(conversationModelView!!.getChatMessaheHistory().value!!.size - 1)
            }
        }, 500)
    }



    //// for get all message
    @Deprecated("Deprecated in Java")
    @SuppressLint("Range")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val message_id = System.currentTimeMillis().toString() + "_" + user_id
        println(requestCode.toString() + "requestCode")
        if (requestCode == AllConstants.READ_STORAGE_PERMISSION_REJECT) {
            if ( checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                DialogProperties.showPermissionDialog(
                    resources.getString(R.string.read_premission),
                    AllConstants.READ_STORAGE_PERMISSION_REJECT,
                    this
                )
            } else {
                doBrowseFile()
            }
        } else if (requestCode == AllConstants.RECORD_AUDIO_PERMISSION_REJECT) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
                DialogProperties.showPermissionDialog(
                    resources.getString(R.string.record_voice_premission),
                    AllConstants.RECORD_AUDIO_PERMISSION_REJECT,
                    this
                )
            } else {
                if (permissions!!.isStorageReadOk(this)) return else permissions!!.requestStorage(
                    this
                )
            }
        } else if (requestCode == AllConstants.STORAGE_PERMISSION_REJECT) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                DialogProperties.showPermissionDialog(
                    resources.getString(R.string.read_premission),
                    AllConstants.STORAGE_PERMISSION_REJECT,
                    this
                )
            } else {
                return
            }
        } else if (requestCode == AllConstants.CAMERA_PERMISSION_REJECT) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                DialogProperties.showPermissionDialog(
                    resources.getString(R.string.camera_premission),
                    AllConstants.CAMERA_PERMISSION_REJECT,
                    this
                )
            } else {
                val options = Options.init()
                    .setRequestCode(PICK_IMAGE_VIDEO) //Request code for activity results
                    .setCount(1) //Number of images to restict selection count
                    .setFrontfacing(false) //Front Facing camera on start
                    .setPreSelectedUrls(returnValue)
                    .setSpanCount(
                        1
                    ) //Span count for gallery min 1 & max 5
                    .setMode(Options.Mode.All) //Option to select only pictures or videos or both
                    .setVideoDurationLimitinSeconds(30) //Duration for video recording
                    .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT) //Orientaion
                //Custom Path For media Storage
                Pix.start(this, options)
            }
        } else if (requestCode == AllConstants.LOCATION_PERMISSION_REJECT) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                DialogProperties.showPermissionDialog(
                    resources.getString(R.string.location_premission),
                    AllConstants.LOCATION_PERMISSION_REJECT,
                    this
                )
            } else {
                currentLocation
            }
        } else if (requestCode == AllConstants.OPEN_MAP_PERMISSION_REJECT) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                DialogProperties.showPermissionDialog(
                    resources.getString(R.string.location_premission),
                    AllConstants.OPEN_MAP_PERMISSION_REJECT,
                    this
                )
            } else {
                openMap()
            }
        } else if (requestCode == AllConstants.READ_CONTACT_PERMISSION_REJECT) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
                DialogProperties.showPermissionDialog(
                    resources.getString(R.string.contact_permission),
                    AllConstants.READ_CONTACT_PERMISSION_REJECT,
                    this
                )
            } else {
                val `in` =
                    Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
                startActivityForResult(`in`, RESULT_PICK_CONTACT)
            }
        } else if (requestCode == OPEN_MAP) {
            openMap()
        } else if (requestCode == SEND_LOCATION) {
            currentLocation
        } else if (resultCode == RESULT_OK) {
            when (requestCode) {
                RESULT_PICK_CONTACT -> contactPicked(data)
                MY_RESULT_CODE_FILECHOOSER -> {
                    val uri = data!!.data
                    val myFile = File(uri!!.path.toString())
                    val pathh = myFile.absolutePath
                    val pathUri = Uri.fromFile(File(pathh))
                    pickiT!!.getPath(data.data, Build.VERSION.SDK_INT)
                    val path: Path? = null
                    val uriString = uri.toString()
                    var displayName: String? = null
                    if (uriString.startsWith("content://")) {
                        var cursor: Cursor? = null
                        try {
                            cursor = this.contentResolver.query(uri, null, null, null, null)
                            if (cursor != null && cursor.moveToFirst()) {
                                displayName =
                                    cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                                Log.d("nameeeee>>>>  ", displayName)
                                val chatMessage = FileUtil.uploadPDF(
                                    displayName,
                                    uri,
                                    this,
                                    user_id,
                                    anthor_user_id,
                                    conversationModelView!!.blockedFor().value
                                ,fcmToken)
                                displayMessage(chatMessage)
                            }
                        } finally {
                            cursor!!.close()
                        }
                    } else if (uriString.startsWith("file://")) {
                        displayName = myFile.name
                        Log.d("nameeeee>>>>  ", displayName)
                    }
                }
                PICK_IMAGE_VIDEO -> {
                    val returnValue = data!!.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                    val selectedMediaUri = Uri.parse(returnValue!![0])
                    if (!FileUtil.isVideoFile(selectedMediaUri.toString())) {
                        showImageBeforeSend(selectedMediaUri, "pix")
                    } else {
                        showVideoBeforeSend(selectedMediaUri, "pix")
//                        showDialogVideo(selectedMediaUri.toString())
                    }
                }
                PICK_IMAGE_FROM_GALLERY -> {
                    val selectedMediaUriGallery = data!!.data
                    val columns = arrayOf(
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.MIME_TYPE
                    )
                    val cursor1 =
                        contentResolver.query(selectedMediaUriGallery!!, columns, null, null, null)
                    cursor1!!.moveToFirst()
                    val pathColumnIndex = cursor1.getColumnIndex(columns[0])
                    val mimeTypeColumnIndex = cursor1.getColumnIndex(columns[1])
                    val contentPath = cursor1.getString(pathColumnIndex)
                    val mimeType = cursor1.getString(mimeTypeColumnIndex)
                    cursor1.close()
                    if (mimeType.startsWith("image")) {
                        showImageBeforeSend(selectedMediaUriGallery, "picker")
                    } else if (mimeType.startsWith("video")) {
                        showVideoBeforeSend(selectedMediaUriGallery, "picker")

                    }
                }
            }
        } else {
        }

    }

    @SuppressLint("Range")
    private fun contactPicked(data: Intent?) {
        val message_id = System.currentTimeMillis().toString() + "_" + user_id
        var cursor: Cursor? = null
        var name: String? = ""
        var phoneIndex: String? = ""
        try {
            val uri = data!!.data
            cursor = contentResolver.query(uri!!, null, null, null, null)
            cursor!!.moveToFirst()
            name =
                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            phoneIndex =
                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            val chatMessage = ChatMessage()
            chatMessage.id = message_id //dummy
            chatMessage.message = phoneIndex
            chatMessage.fileName = name
            chatMessage.dateTime =
                Calendar.getInstance(TimeZone.getTimeZone("GMT")).timeInMillis.toString()
            chatMessage.isMe = true
            chatMessage.type = "contact"
            chatMessage.state = "0"
            binding.messageEdit.setText("")
            chatMessage.isChecked = false
            displayMessage(chatMessage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val sendObject = JSONObject()
        val notification = JSONObject()

        try {
            sendObject.put("sender_id", user_id)
            sendObject.put("reciver_id", anthor_user_id)
            sendObject.put("message", phoneIndex)
            sendObject.put("message_type", "contact")
            sendObject.put("state", "0")
            sendObject.put("message_id", message_id)
            sendObject.put("chat_id", chat_id)
            sendObject.put("orginalName", name)
            sendObject.put(
                "dateTime",
                Calendar.getInstance(TimeZone.getTimeZone("GMT")).timeInMillis.toString()

            )
            notification.put("token", fcmToken)
            notification.put("my_token", classSharedPreferences?.fcmToken)
            notification.put("image", classSharedPreferences?.user!!.image)
            notification.put("title", classSharedPreferences?.user?.userName +" "+ classSharedPreferences?.user?.lastName )
            notification.put("chat_id", chat_id)
            notification.put("blockedFor",conversationModelView!!.blockedFor().value,
            )
            sendObject.put("notification",notification)


        } catch (e: JSONException) {
            e.printStackTrace()
        }
        newMeesage(sendObject)
    }

    override fun onDestroy() {
        super.onDestroy()
        chatRoomRepoo!!.setInChat(anthor_user_id, false)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(check)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveTyping)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveNwMessage)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveDeleteMessage)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveUpdateMessage)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveBlockUser)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveUnBlockUser)
    }

    override fun onBackPressed() {
        val intent = Intent(this, DashBord::class.java)

        if (binding.relativeMaps.visibility == View.VISIBLE) {
            binding.container.visibility = View.VISIBLE
            binding.relativeMaps.visibility = View.GONE
        } else if (conversationModelView!!.selectedMessage.value != null) {
            if (conversationModelView!!.selectedMessage.value!!.size > 0) {
                conversationModelView!!.clearSelectedMessage()
            } else if (viewVisability) {
                hideLayout()
            } else {
//                startActivity(intent)
                finish()
            }
        } else if (viewVisability) {
            hideLayout()
        } else {
//            startActivity(intent)
            finish()
        }
    }

    //// on click in message
    override fun onHandleSelection(position: Int, chatMessage: ChatMessage?, myMessage: Boolean) {
        val pdfFile: File
        if (!hasPermissions(this, *PERMISSIONS)) {
            Log.v(TAG, "download() Method DON'T HAVE PERMISSIONS ")
        } else {
            pdfFile = if (myMessage) {
                val d =
                    getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo" + File.separator + "send") // -> filename = maven.pdf
                File(d, chatMessage!!.fileName)
            } else {
                val d =
                    getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo" + File.separator + "recive") // -> filename = maven.pdf
                File(d, chatMessage!!.message)
            }
            val path = FileProvider.getUriForFile(
                this,
                BuildConfig.APPLICATION_ID + ".fileprovider",
                pdfFile
            )
            if (pdfFile.exists()) {
                Log.v(
                    TAG,
                    "view() Method path $path"
                )
                val pdfIntent = Intent(Intent.ACTION_VIEW)
                pdfIntent.setDataAndType(path, "application/pdf")
                pdfIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                try {
                    startActivity(pdfIntent)
                } catch (e: ActivityNotFoundException) {
                }
            } else {
            }
        }
        Log.v(TAG, "view() Method completed ")
    }

    ///// on click on download
    override fun downloadFile(position: Int, chatMessage: ChatMessage?, myMessage: Boolean) {
        val pdfFile: File
        //        System.out.println(chatMessage.message + "onDownload");
        Log.v(TAG, "download() Method invoked ")
        if (!hasPermissions(this, *PERMISSIONS)) {
        } else {
            Log.v(TAG, "download() Method HAVE PERMISSIONS ")
            if (myMessage) {
                val d =
                    getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send")
                pdfFile = File(d, chatMessage!!.fileName)
                if (!pdfFile.exists()) {
                    download(
                        chatMessage,
                        d,
                        AllConstants.download_url + chatMessage.message,
                        chatMessage.fileName
                    )
                } else {
                    Log.v(TAG, "File already download ")
                }
            } else {
                val d =
                    getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive")
                pdfFile = File(d, chatMessage!!.message)
                if (!pdfFile.exists()) {
                    download(
                        chatMessage,
                        d,
                        AllConstants.download_url + chatMessage.message,
                        chatMessage.message
                    )

//                 DownloadRequest downloadID = PRDownloader.download(AllConstants.download_url + chatMessage.getMessage(), pdfFile.getPath(), "recive")
//                            .build();
                } else {
                    Log.v(TAG, "File already download ")
                }
            }
        }
        Log.v(TAG, "download() Method completed ")
    }

    override fun downloadVoice(position: Int, chatMessage: ChatMessage?, myMessage: Boolean) {
        val audioFile: File

        if (!hasPermissions(this, *PERMISSIONS)) {
            Log.v(TAG, "download() Method DON'T HAVE PERMISSIONS ")
        } else {
            Log.v(TAG, "download() Method HAVE PERMISSIONS ")
            if (myMessage) {
                val d =
                    getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send/voiceRecord")
                audioFile = File(d, chatMessage!!.fileName)
                if (!audioFile.exists()) {
                    download(
                        chatMessage,
                        d,
                        AllConstants.download_url + chatMessage.message,
                        chatMessage.fileName
                    )
                } else {
                    val mediaPlayer = create(this, Uri.parse(audioFile.absolutePath))
                    mediaPlayer.start()
                    Log.v(TAG, "File already download ") }
            } else {
                val d =
                    getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive/voiceRecord")
                audioFile = File(d, chatMessage!!.message)
                if (!audioFile.exists()) {
                    download(
                        chatMessage,
                        d,
                        AllConstants.download_url + chatMessage.message,
                        chatMessage.message
                    )
                } else {
                    Log.v(TAG, "File already download ")
                }
            }
        }
        Log.v(TAG, "download() Method completed ")
    }

    override fun downloadVideo(position: Int, chatMessage: ChatMessage?, myMessage: Boolean) {
        val videoFile: File
        if (!hasPermissions(this, *PERMISSIONS)) {
            Log.v(TAG, "download() Method DON'T HAVE PERMISSIONS ")
        } else {
            Log.v(TAG, "download() Method HAVE PERMISSIONS ")
            if (myMessage) {
                val d =
                    getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send/video")
                videoFile = File(d, chatMessage!!.fileName)
                if (!videoFile.exists()) {
                    download(
                        chatMessage,
                        d,
                        AllConstants.download_url + chatMessage.message,
                        chatMessage.fileName
                    )
                } else {
                    Log.v(TAG, "File already download ")
                }
            } else {
                val d =
                    getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive/video")
                videoFile = File(d, chatMessage!!.message)
                if (!videoFile.exists()) {
                    download(
                        chatMessage,
                        d,
                        AllConstants.download_url + chatMessage.message,
                        chatMessage.message
                    )
                } else {
                    Log.v(TAG, "File already download ")
                }
            }
        }
        Log.v(TAG, "download() Method completed ")
    }

    override fun downloadImage(position: Int, chatMessage: ChatMessage?, myMessage: Boolean) {
        val imageFile: File
        if (!hasPermissions(this, *PERMISSIONS)) {
            Log.v(TAG, "download() Method DON'T HAVE PERMISSIONS ")
        } else {
            Log.v(TAG, "download() Method HAVE PERMISSIONS ")
            if (myMessage) {
                val d =
                    getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send/video")
                imageFile = File(d, chatMessage!!.fileName)
                if (!imageFile.exists()) {
                    download(
                        chatMessage,
                        d,
                        AllConstants.imageUrlInConversation + chatMessage.image,
                        chatMessage.fileName
                    )
                } else {
                    Log.v(TAG, "File already download ")
                }
            } else {
                val d =
                    getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive/video")
                imageFile = File(d, chatMessage!!.image)
                if (!imageFile.exists()) {
                    download(
                        chatMessage,
                        d,
                        AllConstants.imageUrlInConversation + chatMessage.image,
                        chatMessage.image
                    )
                } else {
                    Log.v(TAG, "File already download ")
                }
            }
        }
        Log.v(TAG, "download() Method completed ")
    }

    override fun onClickLocation(position: Int, chatMessage: ChatMessage?, myMessage: Boolean) {
        if (myMessage) {
            binding.sendLocation.visibility = View.VISIBLE
        } else {
            binding.sendLocation.visibility = View.GONE
        }
        val latlong = chatMessage!!.message.split(",").toTypedArray()
        val latitude = latlong[0].toDouble()
        val longitude = latlong[1].toDouble()
        locationLatLng = LatLng(latitude, longitude)
        binding.container.visibility = View.GONE
        binding.relativeMaps.visibility = View.VISIBLE
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                AllConstants.OPEN_MAP_PERMISSION
            )
        } else {
            openMap()
        }
    }

    fun openMap() {
        client = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val taskd = client!!.lastLocation
            taskd.addOnSuccessListener { location ->
                //When Success
                if (location != null) {
                    //Sync Map
                    supportMapFragment!!.getMapAsync { googleMap -> // Create Marker Option                                                             //  We need a small icon to represent our company
                        val options = MarkerOptions().position(locationLatLng!!)
                            .title("He is there") // .icon(BitmapDescriptorFactory.fromResource(R.drawable.logolocation))

                        //Zoom Map
                        googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                locationLatLng!!,
                                15f
                            )
                        )

                        //Add Marker On Map
                        googleMap.addMarker(options)
                    }
                }
            }
        } else {
            showGPSDisabledAlertToUser(OPEN_MAP)
        }
    }

    @SuppressLint("ResourceType")
    override fun onLongClick(position: Int, chatMessage: ChatMessage?, isChecked: Boolean) {
        println(isChecked)
        binding.personInformationLiner.visibility = View.GONE
        binding.toolbar.setBackgroundColor(resources.getColor(R.color.memo_background_color))
        conversationModelView?.setMessageChecked(chatMessage!!.id, isChecked)
        binding.toolsLinerLayout.visibility = View.VISIBLE
        if (isChecked) {
            conversationModelView!!.addSelectedMessage(chatMessage)
            if (chatMessage != null) {
                deleteMessage.add("\"" + chatMessage.id + "\"")
            }
        } else {
            conversationModelView!!.removeSelectedMessage(chatMessage)
            if (chatMessage != null) {
                deleteMessage.remove("\"" + chatMessage.id + "\"")
            }

        }
    }

    override fun playVideo(path: Uri?) {
        val bundle = Bundle()
        bundle.putString("path", path.toString())
        val intent = Intent(this@ConversationActivity, VideoActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun PickiTonUriReturned() {}
    override fun PickiTonStartListener() {}
    override fun PickiTonProgressUpdate(progress: Int) {}
    override fun PickiTonCompleteListener(
        path: String,
        wasDriveFile: Boolean,
        wasUnknownProvider: Boolean,
        wasSuccessful: Boolean,
        Reason: String
    ) {
        val fileNmae = System.currentTimeMillis().toString() + "_" + user_id

        FileUtil.copyPdfFileOrDirectory(
            path, getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send")!!
                .absolutePath
        )
    }

    override fun PickiTonMultipleCompleteListener(
        paths: ArrayList<String>,
        wasSuccessful: Boolean,
        Reason: String
    ) {
    }

    private fun showUpdateMessageDialog(chatMessage: ChatMessage) {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.update_message_dialog, null)
        dialogBuilder.setView(dialogView)
        val edt = dialogView.findViewById<EditText>(R.id.edit1)
        edt.setText(chatMessage.message)
        dialogBuilder.setTitle(R.string.update_message_note)
        dialogBuilder.setPositiveButton(
            R.string.update_message
        ) { dialog, whichButton ->
            if (chatMessage.message != edt.text.toString()) {
                val data = JSONObject()
                try {
                    data.put("message_id", chatMessage.id)
                    data.put("message", edt.text.toString())
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                updateMessage(data)
                conversationModelView!!.setMessageChecked(
                    conversationModelView!!.selectedMessage.value!![0]!!.id, false
                )
                conversationModelView!!.clearSelectedMessage()
                deleteMessage.clear()
            }
        }
        dialogBuilder.setNegativeButton(
            R.string.cancel
        ) { dialog, whichButton -> dialog.dismiss() }
        val b = dialogBuilder.create()
        b.show()
    }
    private fun makeMapsAction() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            currentLocation
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                AllConstants.LOCATION_PERMISSION
            )
        }
    }
    private val currentLocation: Unit
        private get() {
            //Initialize Task Location
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                val task = client!!.lastLocation
                task.addOnSuccessListener { location ->
                    //When Success
                    println(location.toString() + "Location")
                    if (location != null) {
                        //Sync Map
                        supportMapFragment!!.getMapAsync { googleMap -> //Initialize Lat And Long
                            latLng = LatLng(location.latitude, location.longitude)
                            Log.d("latLng", latLng.toString())
                            //                            System.out.println(String.valueOf(latLng) + "latln");
                            stringLatLng =
                                location.latitude.toString() + "," + location.longitude

                            // Create Marker Option                                                            //  We need a small icon to represent our company
                            val options = MarkerOptions().position(latLng!!)
                                .title("I am There") // .icon(BitmapDescriptorFactory.fromResource(R.drawable.logolocation))

                            //Zoom Map
                            googleMap.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    latLng!!,
                                    15f
                                )
                            )
                            googleMap.addMarker(options)
                        }
                    }
                }
            } else {
                showGPSDisabledAlertToUser(SEND_LOCATION)
            }
        }
    private fun alertDeleteDialog() {
        for (message in conversationModelView!!.selectedMessage.value!!) {
            if (!message!!.isMe) {
                isAllMessgeMe = false
                break
            } else {
                isAllMessgeMe = true
            }
        }
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(R.string.alert_delete_message)
        dialog.setPositiveButton(
            R.string.delete_for_me
        ) { dialog, which ->
            conversationModelView!!.deleteMessageForMe(deleteMessage, user_id)
            deleteMessage.clear()

        }
        dialog.setNegativeButton(
            R.string.cancel
        ) { dialog, which -> dialog.dismiss() }
        if (isAllMessgeMe) {
            dialog.setNeutralButton(
                R.string.delete_for_all
            ) { dialogInterface, i ->
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("message_to_delete", deleteMessage.toString())
                    jsonObject.put("anthor_user_id", anthor_user_id)
                    jsonObject.put("my_id", user_id)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                deletForAll(jsonObject)
                conversationModelView!!.clearSelectedMessage()
                deleteMessage.clear()
            }
        }
        val alertDialog = dialog.create()
        alertDialog.show()
    }
    private fun checkContactpermission() {
        if (permissions!!.isContactOk(this)) {
            val `in` =
                Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            startActivityForResult(`in`, RESULT_PICK_CONTACT)
        } else {
            permissions!!.requestContact(this)
        }
    }
    private fun askPermissions() {
        ActivityCompat.requestPermissions(this, callPermissions, requestcode)
    }
    private val isPermissionGranted: Boolean
        private get() {
            for (permission in callPermissions) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) return false
            }
            return true
        }
    fun closeCurrentNotification() {
        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.cancel(anthor_user_id.toInt())
    }
    private fun showGPSDisabledAlertToUser(requestcode: Int) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage(getString(R.string.gps_message))
            .setCancelable(false)
            .setPositiveButton(
                getString(R.string.enable_gps)
            ) { dialog, id ->
                val callGPSSettingIntent = Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS
                )
                val `in` =
                    Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
                startActivityForResult(callGPSSettingIntent, requestcode)
            }
        alertDialogBuilder.setNegativeButton(
            getString(R.string.cancel)
        ) { dialog, id -> dialog.cancel() }
        val alert = alertDialogBuilder.create()
        alert.show()
    }
    fun download(chatMessage: ChatMessage?, d: File?, downloadUrl: String?, fileName: String?) {
        conversationModelView!!.setMessageDownload(chatMessage!!.id, true)
        val downloadID = PRDownloader.download(downloadUrl, d!!.path, fileName)
            .build().setOnStartOrResumeListener {

            }
        val id = downloadID.start(object : OnDownloadListener {
            override fun onDownloadComplete() {
                println("completed")
                conversationModelView!!.setMessageDownload(chatMessage.id, false)
            }

            override fun onError(error: Error) {
                conversationModelView!!.setMessageDownload(chatMessage.id, false)
                println("errror")
            }
        })
        println("downloadID" + downloadID.downloadId + "" + id)
    }
    fun processSocketFile(chatMessage: ChatMessage): Void? {
        displayMessage(chatMessage)
        if (!hasPermissions(this, *PERMISSIONS)) {
            Log.v(TAG, "download() Method DON'T HAVE PERMISSIONS ")
        } else {
            when (chatMessage.type) {
                "imageWeb" -> {
                    val imageFile: File
                    val d =
                        getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive/video")
                    imageFile = File(d, chatMessage.image)
                    if (!imageFile.exists()) {

//                            FileDownloader.downloadFile(AllConstants.imageUrlInConversation + chatMessage.getImage(), imageFile);
                        downloadSocket(
                            chatMessage,
                            d,
                            AllConstants.imageUrlInConversation + chatMessage.image,
                            chatMessage.image
                        )
                        Log.v(TAG, "doInBackground() file download completed")
                    } else {
                        Log.v(TAG, "File already download ")
                    }
                }
                "voice" -> {
                    val voiceFile: File
                    val dV =
                        getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive/voiceRecord")
                    voiceFile = File(dV, chatMessage.message)
                    if (!voiceFile.exists()) {

                        downloadSocket(
                            chatMessage,
                            dV,
                            AllConstants.download_url + chatMessage.message,
                            chatMessage.message
                        )
                        Log.v(TAG, "doInBackground() file download completed")
                    } else {
                        Log.v(TAG, "File already download ")
                    }
                }
                "video" -> {
                    val videoFile: File
                    val dVideo =
                        getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive/video")
                    videoFile = File(dVideo, chatMessage.message)
                    if (!videoFile.exists()) {

                        downloadSocket(
                            chatMessage,
                            dVideo,
                            AllConstants.download_url + chatMessage.message,
                            chatMessage.message
                        )
                        Log.v(TAG, "doInBackground() file download completed")
                    } else {
                        Log.v(TAG, "File already download ")
                    }
                }
                "file" -> {
                    val pdfFile: File
                    if (!hasPermissions(this, *PERMISSIONS)) {
                    } else {
                        val dFile =
                            getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive")
                        pdfFile = File(dFile, chatMessage.message)
                        if (!pdfFile.exists()) {

                            downloadSocket(
                                chatMessage,
                                dFile,
                                AllConstants.download_url + chatMessage.message,
                                chatMessage.message
                            )
                            Log.v(TAG, "doInBackground() file download completed")

                        } else {
                            Log.v(TAG, "File already download ")
                        }
                    }
                }
                else -> {}
            }
        }
        return null
    }
    fun downloadSocket(
        chatMessage: ChatMessage,
        d: File?,
        downloadUrl: String?,
        fileName: String?
    ) {
        conversationModelView!!.setMessageDownload(chatMessage.id, true)
        val downloadID = PRDownloader.download(downloadUrl, d!!.path, fileName)
            .build().setOnStartOrResumeListener {
            }
        val id = downloadID.start(object : OnDownloadListener {
            override fun onDownloadComplete() {
                conversationModelView!!.setMessageDownload(chatMessage.id, false)
            }

            override fun onError(error: Error) {
                conversationModelView!!.setMessageDownload(chatMessage.id, false)
            }
        })
    }
    @SuppressLint("Range")
    fun showImageBeforeSend(uri: Uri?, type: String) {

        val fileNmae = System.currentTimeMillis().toString() + "_" + user_id
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_image_before_send)
        dialog.setTitle("Title...")
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.FILL_PARENT,
            ViewGroup.LayoutParams.FILL_PARENT
        )
        val image = dialog.findViewById<PhotoView>(R.id.photo_view)
        image.setImageURI(uri)
        val fab = dialog.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            var displayNamee: String? = null
            val pathImage: Uri?
            pathImage =
                if (type == "pix") Uri.fromFile(File(uri.toString())) else {
                    uri
                }
            val myFileImage = File(pathImage.toString())
            FileUtil.copyFileOrDirectory(
                FileUtil.getPath(this, pathImage),
                getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send/video")!!
                    .absolutePath
            ,fileNmae)
            if (pathImage.toString().startsWith("content://")) {
                var cursor: Cursor? = null
                try {
                    cursor = this.contentResolver.query(
                        uri!!,
                        null,
                        null,
                        null,
                        null
                    )
                    if (cursor != null && cursor.moveToFirst()) {
                        displayNamee =
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        val chatMessage = FileUtil.uploadImage(
                            fileNmae,
                            uri,
                            this,
                            user_id,
                            anthor_user_id,
                            conversationModelView!!.blockedFor().value
                        ,fcmToken)
                        displayMessage(chatMessage)
                    }
                } finally {
                    cursor!!.close()
                }
            } else if (pathImage.toString().startsWith("file://")) {
                displayNamee = myFileImage.name
                val chatMessage = FileUtil.uploadImage(
                    fileNmae,
                    pathImage,
                    this,
                    user_id,
                    anthor_user_id,
                    conversationModelView!!.blockedFor().value
                ,fcmToken)
                displayMessage(chatMessage)
            }
            dialog.dismiss()
        }
        dialog.show()
    }
    @SuppressLint("Range")
    fun showVideoBeforeSend(uri: Uri?, type: String) {
        val fileNmae = System.currentTimeMillis().toString() + "_" + user_id

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_video_before_send)
        dialog.setTitle("Title...")
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.FILL_PARENT,
            ViewGroup.LayoutParams.FILL_PARENT
        )
        val videoView = dialog.findViewById<VideoView>(R.id.simpleVideoView)
        mediaControl = MediaController(dialog.context)
        videoView.requestFocus()
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.setOnVideoSizeChangedListener { mp, width, height ->

                videoView.setMediaController(mediaControl)

                mediaControl!!.setAnchorView(videoView)
                mediaControl!!.setMediaPlayer(videoView)
            }
        }
        videoView.setMediaController(mediaControl)
        mediaControl!!.setAnchorView(videoView)
        mediaControl!!.setMediaPlayer(videoView)
        videoView.setMediaController(mediaControl)
        videoView.setVideoURI(uri)
        videoView.start()
        videoView.setOnCompletionListener {
        }
        videoView.setOnErrorListener { mp, what, extra -> //                finish();
            false
        }
        val fab = dialog.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val pathhh: Uri?
            var compressPath: Uri
            pathhh = if (type == "pix") {
                Uri.fromFile(File(uri.toString()))
            } else {
                uri
            }
            val myFilee = File(pathhh.toString())
            FileUtil.copyFileOrDirectory(
                FileUtil.getPath(this, pathhh),
                getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send/video")!!
                    .absolutePath
            ,fileNmae)
            var displayNamee: String? = null
            if (pathhh.toString().startsWith("content://")) {
                var cursor: Cursor? = null
                try {
                    cursor = this.contentResolver.query(
                        uri!!,
                        null,
                        null,
                        null,
                        null
                    )
                    if (cursor != null && cursor.moveToFirst()) {
                        displayNamee =
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        println(displayNamee)
                        val chatMessage = FileUtil.uploadVideo(
                            fileNmae,
                            uri,
                            this,
                            user_id,
                            anthor_user_id,
                            conversationModelView!!.blockedFor().value
                        ,fcmToken)
                        displayMessage(chatMessage)
                        //                                                    compressVideo(compressPath,displayNamee);
                    }
                } finally {
                    cursor!!.close()
                }
            } else if (pathhh.toString().startsWith("file://")) {
                displayNamee = myFilee.name
                val chatMessage = FileUtil.uploadVideo(
                    fileNmae,
                    pathhh,
                    this,
                    user_id,
                    anthor_user_id,
                    conversationModelView!!.blockedFor().value
                ,fcmToken)
                displayMessage(chatMessage)
            }
            dialog.dismiss()
        }
        dialog.show()
    }
    private fun showDialogVideo(path: String) {
//        val dialogFragment = DialogFragmentVideoBeforeSend(this@ConversationActivity,path)
//        dialogFragment.show(supportFragmentManager, "signature")


    }
    override fun onDataReceived(data: String) {
        Log.d(TAG, "onDataReceived: ${data}")
    }
    companion object {
        private val callPermissions =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
        private const val PICK_IMAGE_FROM_GALLERY = 8396
        private const val RESULT_PICK_CONTACT = 1
        private const val PICK_IMAGE_VIDEO = 1111
        private const val MY_REQUEST_CODE_PERMISSION = 1000
        private const val MY_RESULT_CODE_FILECHOOSER = 2200
        private const val OPEN_MAP = 12121212
        private const val SEND_LOCATION = 13131313
        const val CHEK = "ConversationActivity.CHECK_CONNECT"
        const val TYPING = "ConversationActivity.ON_TYPING"
        const val ON_MESSAGE_RECEIVED = "ConversationActivity.ON_MESSAGE_RECEIVED"
        const val ON_MESSAGE_DELETED = "ConversationActivity.ON_MESSAGE_DELETED"
        const val ON_MESSAGE_UPDATE = "ConversationActivity.ON_MESSAGE_UPDATE"
        const val ON_BLOCK_USER = "ConversationActivity.ON_BLOCK_USER"
        const val ON_UN_BLOCK_USER = "ConversationActivity.ON_UN_BLOCK_USER"
        private const val TAG = "MainActivity2"
        private val PERMISSIONS = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        ////////download() Method  PERMISSIONS
        private fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
            if (context != null && permissions != null) {
                for (permission in permissions) {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            permission
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return false
                    }
                }
            }
            return true
        }


    }

    private fun showQuotedMessage(message: ChatMessage) {
//        binding.edit_message.requestFocus()
//        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputMethodManager?.showSoftInput(edit_message, InputMethodManager.SHOW_IMPLICIT)
        binding.txtQuotedMsg.text = message.message
        binding.replyLayout.visibility = View.VISIBLE

    }

    private fun hideReplyLayout() {
        binding.replyLayout.visibility = View.GONE
    }

///////////////////////////////for hide keyboard

//    private fun hideKeyboard(activity: Activity?) {
//        if (activity != null && activity.window != null) {
//            activity.window.decorView
//            val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//            imm?.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
//        }
//    }
}