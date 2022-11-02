package com.yawar.memo.views

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.rxjava2.RxDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.AccessToken
import com.github.chrisbanes.photoview.PhotoView
import com.yawar.memo.Api.ServerApi
import com.yawar.memo.R
import com.yawar.memo.adapter.MediaAdapter
import com.yawar.memo.call.RequestCallActivity
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.model.MediaModel
import com.yawar.memo.model.UserModel
import com.yawar.memo.modelView.UserInformationViewModel
import com.yawar.memo.repositry.BlockUserRepo
import com.yawar.memo.service.SocketIOService
import com.yawar.memo.sessionManager.ClassSharedPreferences
import com.yawar.memo.utils.BaseApp
import com.yawar.memo.utils.TimeProperties
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONException
import org.json.JSONObject


class UserInformationActivity : AppCompatActivity() {
//    var EXAMPLE_COUNTER: Key<Int>
    private lateinit var recyclerView: RecyclerView
    private val recyclerDataArrayList = ArrayList<MediaModel>()
    lateinit var serverApi: ServerApi
    lateinit var circleImageView: CircleImageView
    lateinit var linerMore: LinearLayout
    lateinit var linerAudioCall: LinearLayout
    lateinit var linerVideoCall: LinearLayout
    lateinit var txtUserName: TextView
    lateinit var txtSpecialNumber: TextView
    lateinit var txtState: TextView
    lateinit var timeProperties: TimeProperties
    lateinit var dataStore: RxDataStore<Preferences>
//    lateinit var sharedPreferenceLiveData: SharedPreferenceStringLiveData
    var muteList: ArrayList<String?>? = ArrayList()
    lateinit var blockUserRepo: BlockUserRepo
    lateinit var userInformationViewModel: UserInformationViewModel
    lateinit var call: TextView
    lateinit var video: TextView
    lateinit var message: TextView
    lateinit var mute: TextView
    lateinit var more: TextView
    lateinit var special_number: TextView
    lateinit var media: TextView
    lateinit var userName: String
    lateinit var sn: String
    lateinit var chatId: String
    lateinit var another_user_id: String
    lateinit var my_id: String
    lateinit var fcm_token: String
    lateinit var imageUrl: String
    lateinit var classSharedPreferences: ClassSharedPreferences
    lateinit var myBase: BaseApp
    var adapter: MediaAdapter? = null
    var imgBtnMessage: ImageView? = null
    var p: PopupMenu? = null

    //    ChatRoomRepo chatRoomRepo;
    lateinit var blockedFor: String
    var isBlockForMe = false
    lateinit var imageVideoCall: ImageView
    lateinit var imageAudioCall: ImageView
    lateinit var imageMore: ImageView
    lateinit var imageChat: ImageView
    lateinit var imageMute: ImageView
    private val check: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val check = intent.extras!!.getString("check")
            var checkObject: JSONObject? = null
            var checkConnect: String? = "false"
            val user_id: String
            try {
                checkObject = JSONObject(check)
                user_id = checkObject.getString("user_id")
                if (user_id == another_user_id) {
                    checkConnect = checkObject.getString("is_connect")
                    userInformationViewModel.setLastSeen(checkObject.getString("last_seen"))
                    userInformationViewModel.set_state(checkConnect)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun sendBlockFor(blocked: Boolean) {
        val userBlocked = JSONObject()
        val item = JSONObject()
        try {
            item.put("blocked_for", userInformationViewModel.blockedFor().value)
            item.put("Block", blocked)
            userBlocked.put("my_id", my_id)
            userBlocked.put("user_id", another_user_id)
            userBlocked.put("blocked_for", userInformationViewModel.blockedFor().value)
            userBlocked.put("userDoBlockName", classSharedPreferences.user.userName)
            userBlocked.put("userDoBlockSpecialNumber", classSharedPreferences.user.secretNumber)
            userBlocked.put("userDoBlockImage", classSharedPreferences.user.image)
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
            userUnBlocked.put("my_id", my_id)
            userUnBlocked.put("user_id", another_user_id)
            userUnBlocked.put("blocked_for", userInformationViewModel.blockedFor().value)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val service = Intent(this, SocketIOService::class.java)
        service.putExtra(SocketIOService.EXTRA_UN_BLOCK_PARAMTERS, userUnBlocked.toString())
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_UN_BLOCK)
        startService(service)
    }

    private fun checkConnect() {
        val service = Intent(this, SocketIOService::class.java)
        val `object` = JSONObject()
        try {
            `object`.put("my_id", my_id)
            `object`.put("your_id", another_user_id)
            //            socket.emit("check connect", object);
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        service.putExtra(SocketIOService.EXTRA_CHECK_CONNECT_PARAMTERS, `object`.toString())
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_CHECK_CONNECT)
        startService(service)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_user_information)
        LocalBroadcastManager.getInstance(this).registerReceiver(check, IntentFilter(CHEK))
        recyclerView = findViewById(R.id.idCourseRV)
        adapter = MediaAdapter(recyclerDataArrayList, this)
//        EXAMPLE_COUNTER = intPreferencesKey("key")
        dataStore = BaseApp.getInstance().dataStore
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // at last set adapter to recycler view.
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        initViews()
        initAction()
    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        myBase = BaseApp.getInstance()
        classSharedPreferences = myBase.classSharedPreferences
        userInformationViewModel = ViewModelProvider(this).get(
            UserInformationViewModel::class.java
        )
        timeProperties = TimeProperties()
        blockUserRepo = myBase.blockUserRepo
        val bundle = intent.extras
        userName = bundle!!.getString("name", "Default")
        sn = bundle.getString("special", "Default")
        chatId = bundle.getString("chat_id", "Default")
        another_user_id = bundle.getString("user_id", "Default")
        fcm_token = bundle.getString("fcm_token", "Default")
        imageUrl = bundle.getString("image", "Default")
        blockedFor = bundle.getString("blockedFor", "")
        my_id = classSharedPreferences.user.userId.toString()
        imageMute = findViewById(R.id.img_mute)
        serverApi = ServerApi(this)
        linerMore = findViewById(R.id.liner_more)
        linerVideoCall = findViewById(R.id.liner_video_call)
        linerAudioCall = findViewById(R.id.liner_audio_call)
        imageVideoCall = findViewById(R.id.img_video_call)
        imageAudioCall = findViewById(R.id.img_audio_call)
        imageMore = findViewById(R.id.img_more)
        imageChat = findViewById(R.id.img_message)
        checkConnect()
        userInformationViewModel.setBlockedFor(blockedFor)
        txtState = findViewById(R.id.last_seen)
        userInformationViewModel.state.observe(
            this
        ) { s ->
            println("stateee$s")
            if (s != null) {
                if (s == "true") {
                    txtState.setText(R.string.connect_now)
                } else if (s == "false") {
                    if (userInformationViewModel.getLastSeen() != "null") {
                        txtState.text = resources.getString(R.string.last_seen) + " " + timeProperties.getDateForLastSeen(
                            this,
                            userInformationViewModel.getLastSeen()!!
                                .toLong()
                        )
                    }
                }
            }
        }
        userInformationViewModel.getMedia().observe(
            this
        ) { mediaModelArrayList ->
            if (mediaModelArrayList != null) {
                Log.d("mediaModelArrayList", mediaModelArrayList.size.toString())
                for (media in mediaModelArrayList) {
                    recyclerDataArrayList.add(media)
                }
            }
            adapter!!.notifyDataSetChanged()
        }

        userInformationViewModel.getUserInfo().observe(
            this
        ) { userModel ->
            Log.d("initInfooo", userModel.userName.toString())
            if (userModel != null) {
                txtUserName.text = userModel.userName + " " + userModel.lastName
                if (userModel.phone != null ) {
                    if(userModel.phone!!.isNotEmpty()){

                    val firstString = userModel.phone!!.substring(0, 4)
                    val secondString = userModel.phone!!.substring(4, 7)
                    val thirtyString = userModel.phone!!.substring(7, 10)
                    val lastString = userModel.phone!!.substring(10)
                    txtSpecialNumber.text = "$firstString-$secondString-$thirtyString-$lastString"

//                    if (userModel.image != null) {
//                        Glide.with(circleImageView.context)
//                            .load(AllConstants.imageUrl + userModel.image)
//                            .apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th))
//                            .into(
//                                circleImageView
//                            )
                    }
                }
            }
        }
        ///////////////////check is mute
//        SharedPreferences prefs = getSharedPreferences("muteUsers", MODE_PRIVATE);
//        SharedPreferenceStringLiveData sharedPreferenceStringLiveData = new SharedPreferenceStringLiveData(prefs, "muteUsers", "");
//        sharedPreferenceStringLiveData.getStringLiveData("muteUsers", "").observe(this, cid -> {
////            ArrayList<String> response = new ArrayList<>();
//            Gson gson = new Gson();
//
//
//            String json = prefs.getString("muteUsers", "");
//            Type type = new TypeToken<ArrayList<String>>() {}.getType();
//            if (json != null) {
//                muteList  =  gson.fromJson(json,type );
//
//            }
////            if(response== null){
////                response = new ArrayList<String>();
////            }
//                    for (String s:
//                            muteList) {
//                        if (another_user_id.equals(s)) {
//                            userInformationViewModel.mute.setValue(true);
//                            break;
//                        }
//                    }
//
//        });
        //////////////////
        muteList = classSharedPreferences.muteUsers
        if (muteList != null) {
            for (s in muteList!!) {
                if (another_user_id == s) {
                    userInformationViewModel.setMute(true)
                    break
                }
            }
        }
        userInformationViewModel.loadingMutableLiveData.observe(
            this
        ) { aBoolean ->
            if (aBoolean) {
                imageMute.setImageDrawable(resources.getDrawable(R.drawable.ic_un_mute))
            } else {
                imageMute.setImageDrawable(resources.getDrawable(R.drawable.ic_bell))
            }
        }


//////////////////////////
        userInformationViewModel.isBlocked.observe(
            this
        ) { s ->
            if (s != null) {
                if (s) {
                    sendBlockFor(s)
                    userInformationViewModel.setBlocked(false)
                }
            }
        }
        ////////////
        userInformationViewModel.isUnBlocked().observe(
            this
        ) { s ->
            println("stateee$s")
            if (s != null) {
                if (s) {
                    sendUnBlockFor(s)
                    userInformationViewModel.setUnBlocked(false)
                }
            }
        }
        userInformationViewModel.blockedFor().observe(
            this
        ) { s ->
            val isAnyOneBlock = false
            if (s != null) {
                if (s == my_id || s == "0") {
                    isBlockForMe = true
                    imageAudioCall.isEnabled = false
                    imageVideoCall.isEnabled = false
                } else if (s == another_user_id) {
                    isBlockForMe = false
                    imageAudioCall.isEnabled = false
                    imageVideoCall.isEnabled = false
                } else {
                    isBlockForMe = false
                    imageAudioCall.isEnabled = true
                    imageVideoCall.isEnabled = true
                }
            } else {
                isBlockForMe = false
                imageAudioCall.isEnabled = true
                imageVideoCall.isEnabled = true
            }
        }
        circleImageView = findViewById(R.id.imageView)
        txtUserName = findViewById(R.id.txt_user_name)
        txtSpecialNumber = findViewById(R.id.txt_special_number)
        call = findViewById(R.id.call)
        video = findViewById(R.id.video)
        message = findViewById(R.id.message)
        mute = findViewById(R.id.mute)
        more = findViewById(R.id.more)
        special_number = findViewById(R.id.special_number)
        media = findViewById(R.id.media)
        if (imageUrl != null) {
            Glide.with(circleImageView.context)
                .load(AllConstants.imageUrl + imageUrl)
                .apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th))
                .into(
                    circleImageView
                )
        }

        userInformationViewModel.mediaRequest(my_id, another_user_id)
        userInformationViewModel.userInfoRequest( another_user_id)

    }

    private fun initAction() {
        imageChat.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("reciver_id", another_user_id)
            bundle.putString("sender_id", my_id)
            bundle.putString("fcm_token", fcm_token)

            //        bundle.putString("reciver_id",chatRoomModel.reciverId);
            bundle.putString("name", userName)
            bundle.putString("image", imageUrl)
            bundle.putString("chat_id", chatId)
            bundle.putString("blockedFor", userInformationViewModel.blockedFor().value)
            val intent = Intent(this@UserInformationActivity, ConversationActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        //////
        imageMore.setOnClickListener { //                block();
            popupMenuExample()
        }
        imageAudioCall.setOnClickListener { //                block();
            val intent = Intent(this@UserInformationActivity, RequestCallActivity::class.java)
            intent.putExtra("anthor_user_id", another_user_id)
            intent.putExtra("user_name", userName)
            intent.putExtra("isVideo", false)
            intent.putExtra("fcm_token", fcm_token)
            intent.putExtra("image_profile", imageUrl)
            startActivity(intent)
        }
        imageVideoCall.setOnClickListener { //                block();
            val intent = Intent(this@UserInformationActivity, RequestCallActivity::class.java)
            //                Intent intent = new Intent(ConversationActivity.this, CompleteActivity.class);
            intent.putExtra("anthor_user_id", another_user_id)
            intent.putExtra("user_name", userName)
            intent.putExtra("isVideo", true)
            intent.putExtra("fcm_token", fcm_token)
            intent.putExtra("image_profile", imageUrl)
            startActivity(intent)
        }
        imageMute.setOnClickListener { //                System.out.println("muteeeeeeeeeeeee");
            //                JSONObject jsonObject = new JSONObject();
            if (userInformationViewModel.loadingMutableLiveData.value!!) {
                muteList!!.remove(another_user_id)
                userInformationViewModel.setMute(false)
            } else {
                muteList!!.add(another_user_id)
                userInformationViewModel.setMute(true)
            }
            classSharedPreferences.muteUsers = muteList
        }

        circleImageView.setOnClickListener {
                val dialog = Dialog(this)
                dialog.setContentView(R.layout.dialog_image_cht)
                dialog.setTitle("Title...")
                dialog.window!!
                    .setLayout(
                        ViewGroup.LayoutParams.FILL_PARENT,
                        ViewGroup.LayoutParams.FILL_PARENT
                    )
                val image: PhotoView = dialog.findViewById(R.id.photo_view)
                Glide.with(image.context).load(AllConstants.imageUrl + imageUrl).centerCrop()
                    .into(image)
                dialog.show()
            }

    }

    private fun popupMenuExample() {
        val p = PopupMenu(this, linerMore)
        p.menuInflater.inflate(R.menu.main_menu, p.menu)
        if (isBlockForMe) {
            p.menu.findItem(R.id.block).isVisible = false
            p.menu.findItem(R.id.unBlock).isVisible = true
        } else {
            p.menu.findItem(R.id.block).isVisible = true
            p.menu.findItem(R.id.unBlock).isVisible = false
        }
        p.setOnMenuItemClickListener { item ->
            val userModel = UserModel(another_user_id, userName, userName, "", "", sn, imageUrl, "")
            when (item.itemId) {
                R.id.block -> {
                    val dialog = AlertDialog.Builder(this@UserInformationActivity)
                    dialog.setTitle(R.string.alert_block_user)
                    dialog.setPositiveButton(
                        R.string.block
                    ) { dialog, which -> //                                        serverApi.block(my_id,userModel);
                        userInformationViewModel.sendBlockRequest(
                            my_id,
                            another_user_id
                        )
                    }
                    dialog.setNegativeButton(
                        R.string.cancel
                    ) { dialog, which -> dialog.dismiss() }
                    val alertDialog = dialog.create()
                    alertDialog.show()
                }
                R.id.unBlock -> {
                    val dialogUnBlock =
                        AlertDialog.Builder(this)
                    dialogUnBlock.setTitle(R.string.alert_unblock_user)
                    dialogUnBlock.setPositiveButton(
                        R.string.Unblock
                    ) { dialog, which -> //                                    serverApi.unbBlockUser(my_id,userModel);
                        userInformationViewModel.sendUnBlockRequest(
                            my_id,
                            another_user_id
                        )
                    }
                    dialogUnBlock.setNegativeButton(
                        R.string.cancel
                    ) { dialog, which -> dialog.dismiss() }
                    val alertUnBlockDialog = dialogUnBlock.create()
                    alertUnBlockDialog.show()
                }
            }
            true
        }
        p.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(check)
    }

    companion object {
        const val CHEK = "ConversationActivity.CHECK_CONNECT"
    }


}