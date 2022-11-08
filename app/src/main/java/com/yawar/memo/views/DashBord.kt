package com.yawar.memo.views

import CallHistoryFragment
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.yawar.memo.R
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.databinding.ActivityDashBordBinding
import com.yawar.memo.fragment.ChatRoomFragment
import com.yawar.memo.fragment.SearchFragment
import com.yawar.memo.fragment.SettingsFragment
import com.yawar.memo.language.helper.LocaleHelper
import com.yawar.memo.model.ChatRoomModel
import com.yawar.memo.permissions.Permissions
import com.yawar.memo.repositry.AuthRepo
import com.yawar.memo.repositry.ChatRoomRepoo
import com.yawar.memo.service.SocketIOService
import com.yawar.memo.sessionManager.ClassSharedPreferences
import com.yawar.memo.utils.BaseApp
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class DashBord : AppCompatActivity(), Observer {
    lateinit var bottomNavigation: BottomNavigationView
    private lateinit var permissions: Permissions
    lateinit var myBase: BaseApp
    lateinit var binding: ActivityDashBordBinding
    lateinit var chatRoomRepoo: ChatRoomRepoo
    lateinit var fragment : Fragment
    lateinit var classSharedPreferences: ClassSharedPreferences
    lateinit var myId: String
    lateinit var authRepo: AuthRepo
    private fun connectSocket() {
        val service = Intent(this, SocketIOService::class.java)
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_JOIN)
        startService(service)
    }

    private val reciveNewChat: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val objectString = intent.extras!!.getString("new chat")
            var jsonObject: JSONObject? = null
            try {
                jsonObject = JSONObject(objectString.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            var message: JSONObject? = null
            var user: JSONObject? = null
            var text: String? = ""
            var chatId: String? = null
            try {
                message = jsonObject!!.getJSONObject("message")
                text = message.getString("message")
                user = jsonObject.getJSONObject("user")
                chatId = jsonObject.getString("chat_id")
                if (user.getString("id") != myId) {
                    chatRoomRepoo.addChatRoom(
                        ChatRoomModel(
                            user.getString("first_name")+" "+user.getString("last_name"),
                            user.getString("id"),
                            text,
                            user.getString("profile_image"),
                            false,
                            "0",
                            chatId,
                            "null",
                            "1",
                            false,
                            user.getString("user_token"),
                            user.getString("sn"),
                            message.getString("message_type"),
                            message.getString("state"),
                            message.getString("created_at"),
                            false,
                            "null",
                            user.getString("id"), ""
                        )
                    )
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } //

    }
    private val reciveNwMessage: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("SuspiciousIndentation")
        override fun onReceive(context: Context, intent: Intent) {
            val objectString = intent.extras!!.getString("message")
            var message: JSONObject? = null
            try {
                message = JSONObject(objectString.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            var text: String? = ""
            var type: String? = ""
            var state: String? = ""
            var senderId = ""
            var reciverId: String? = ""
            var id = ""
            val fileName = ""
            var chatId: String? = ""
            var dateTime: String? = ""
            var id_user: String? = ""
            try {

                /// JSONObject jsonObject= (JSONObject) messageJson.get("data");
                id = message!!.getString("message_id")
//                id_user = message.getString("id")
                text = message.getString("message")
                type = message.getString("message_type")
                state = message.getString("state")
                senderId = message.getString("sender_id")
                //                        id = message.getString("message_id");
                reciverId = message.getString("reciver_id")
                chatId = message.getString("chat_id")
                if(!text.equals("welcome to memo"))
                dateTime = message.getString("dateTime")
                //                        fileName = message.getString("orginalName");
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            var anthor_id: String? = ""
            anthor_id = if (senderId == myId) {
                reciverId
            } else {
                senderId
            }
            if (id != "0000") {
                chatRoomRepoo.setLastMessage(
                    text,
                    chatId!!, myId, anthor_id!!, type!!, state, dateTime, senderId
                )
            } else {
                chatRoomRepoo.updateLastMessageState(state, chatId!!)
            }
        }
    }
    private val reciveTyping: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val typingString = intent.extras!!.getString("typing")
            var message: JSONObject? = null
            var isTyping = "false"
            var anthor_id: String? = ""
            var chat_id: String? = ""
            try {
                message = JSONObject(typingString.toString())
                isTyping = message.getString("typing")
                anthor_id = message.getString("my_id")
                chat_id = message.getString("chat_id")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            chatRoomRepoo.setTyping(chat_id!!, isTyping == "true")
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
                    chatRoomRepoo.setBlockedState(userDoBlock, blockedFor)
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
                    chatRoomRepoo.setBlockedState(userDoUnBlock, unBlockedFor)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = LocaleHelper.setLocale(this, LocaleHelper.getLanguage(this))
        val myLocale = Locale(LocaleHelper.getLanguage(this))
        val res = context.resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
        baseContext.resources.updateConfiguration(
            conf,
            baseContext.resources.displayMetrics
        )
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dash_bord)

        connectSocket()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            reciveNwMessage, IntentFilter(
                ON_MESSAGE_RECEIVED
            )
        )
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveTyping, IntentFilter(TYPING))
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

////// send Fcm Token
        myBase = BaseApp.getInstance()
        authRepo = myBase.authRepo
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("kk", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                Log.w("kk", "Fetching FCM registration token sucess", task.exception)
                val token = task.result
                authRepo.sendFcmToken(myId, token!!)
                Log.d("jjj", token)
            })

////////////////////////////////////


        ///////////////////

/////////////////////
        classSharedPreferences = BaseApp.getInstance().classSharedPreferences
        Log.d("onCreate:", classSharedPreferences.number)
        permissions = Permissions()
        checkPermission()
        myId = classSharedPreferences.user.userId.toString()
        chatRoomRepoo = myBase.chatRoomRepoo
        LocalBroadcastManager.getInstance(this).registerReceiver(
            reciveNewChat, IntentFilter(
                NEW_MESSAGE
            )
        )
        if (savedInstanceState == null) {
            binding.navigationChip.selectedItemId = R.id.chat
            supportFragmentManager.beginTransaction()
                .replace(R.id.dashboardContainer, ChatRoomFragment()).commit()
        }
        binding.navigationChip.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.chat -> fragment = ChatRoomFragment()
                R.id.searchSn -> fragment = SearchFragment()
                R.id.block -> fragment = SettingsFragment()
                R.id.calls -> fragment = CallHistoryFragment()
            }
            supportFragmentManager.beginTransaction().replace(
                R.id.dashboardContainer,
                fragment
            ).commit()
            //            }
            true
        })
    }


    @Deprecated("Deprecated in Java")
    override fun update(observable: Observable, o: Any) {}
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.navigationChip.selectedItemId != R.id.chat) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.dashboardContainer, ChatRoomFragment()).commit()
            binding.navigationChip.selectedItemId = R.id.chat
        } else {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveNewChat)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveNwMessage)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveTyping)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveBlockUser)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveUnBlockUser)
    }

    fun checkPermission() {
        if (permissions.isStorageWriteOk(this)) {
            createDirectory("memo")
            createDirectory("memo/send")
            createDirectory("memo/recive")
            createDirectory("memo/send/voiceRecord")
            createDirectory("memo/recive/voiceRecord")
            createDirectory("memo/send/video")
            createDirectory("memo/recive/video")
        } else permissions.requestStorage(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            AllConstants.STORAGE_REQUEST_CODE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createDirectory("memo")
                createDirectory("memo/send")
                createDirectory("memo/recive")
                createDirectory("memo/send/voiceRecord")
                createDirectory("memo/recive/voiceRecord")
                createDirectory("memo/send/video")
                createDirectory("memo/recive/video")
                //                    chatRoomRepo.callAPI(myId);
            } else if (!shouldShowRequestPermissionRationale(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                showPermissionDialog(
                    resources.getString(R.string.write_premission),
                    STORAGE_PERMISSION_CODE
                )
            }
            AllConstants.CONTACTS_REQUEST_CODE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPermission()
            } else {
                if ( !shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_CONTACTS
                    )
                ) {
                    showPermissionDialog(
                        resources.getString(R.string.contact_permission),
                        Contact_PERMISSION_CODE
                    )
                }
            }
        }
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )
    }

    fun createDirectory(dName: String) {
//        File yourAppDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + dName);
        val yourAppDir =
            File(getExternalFilesDir(Environment.DIRECTORY_DCIM).toString() + File.separator + dName)
        if (!yourAppDir.exists() && !yourAppDir.isDirectory) {
            try {
                Files.createDirectory(Paths.get(yourAppDir.absolutePath))
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(applicationContext, "problem", Toast.LENGTH_LONG).show()
            }
        } else {
            Log.i("CreateDir", "App dir already exists")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Contact_PERMISSION_CODE -> if (checkSelfPermission(
                    Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_DENIED
            ) {
                showPermissionDialog(
                    resources.getString(R.string.contact_permission),
                    Contact_PERMISSION_CODE
                )
            } else {
//
                checkPermission()
            }
            STORAGE_PERMISSION_CODE -> if (checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_DENIED
            ) {

            } else {
                createDirectory("memo")
                createDirectory("memo/send")
                createDirectory("memo/recive")
                createDirectory("memo/send/voiceRecord")
                createDirectory("memo/recive/voiceRecord")
                createDirectory("memo/send/video")
                createDirectory("memo/recive/video")
                //                    chatRoomRepo.callAPI(myId);
            }
        }
    }

    fun showPermissionDialog(message: String, RequestCode: Int) {
        println(message + "message")
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setCancelable(true)
        alertBuilder.setTitle(resources.getString(R.string.permission_necessary))
        alertBuilder.setMessage(resources.getString(R.string.contact_permission))
        alertBuilder.setMessage(message)
        alertBuilder.setPositiveButton(
            R.string.settings
        ) { dialog, which ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivityForResult(intent, RequestCode)
        }
        val alert = alertBuilder.create()
        alert.show()
    }

    companion object {
        const val NEW_MESSAGE = "new Message"
        const val ON_MESSAGE_RECEIVED = "ConversationActivity.ON_MESSAGE_RECEIVED"
        const val TYPING = "ConversationActivity.ON_TYPING"
        const val ON_BLOCK_USER = "ConversationActivity.ON_BLOCK_USER"
        const val ON_UN_BLOCK_USER = "ConversationActivity.ON_UN_BLOCK_USER"
        private const val STORAGE_PERMISSION_CODE = 2000
        private const val Contact_PERMISSION_CODE = 1000
    }
}