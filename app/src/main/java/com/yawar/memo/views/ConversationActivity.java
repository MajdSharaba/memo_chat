package com.yawar.memo.views;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.service.notification.StatusBarNotification;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordPermissionHandler;
import com.devlomi.record_view.RecordView;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.request.DownloadRequest;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.hbisoft.pickit.PickiT;
import com.hbisoft.pickit.PickiTCallbacks;
//import com.yawar.memo.call.CompleteActivity;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.Api.ServerApi;
import com.yawar.memo.BuildConfig;
import com.yawar.memo.R;
import com.yawar.memo.adapter.ChatAdapter;
import com.yawar.memo.call.RequestCallActivity;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.fragment.ForwardDialogFragment;
import com.yawar.memo.model.ChatMessage;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.modelView.ConversationModelView;
import com.yawar.memo.repositry.BlockUserRepo;
import com.yawar.memo.repositry.ChatMessageRepo;
import com.yawar.memo.repositry.ChatRoomRepo;
import com.yawar.memo.service.SocketIOService;
import com.yawar.memo.utils.BaseApp;
import com.yawar.memo.utils.DialogProperties;
import com.yawar.memo.utils.FileDownloader;
import com.yawar.memo.utils.FileUtil;
import com.yawar.memo.utils.TimeProperties;
import com.yawar.memo.permissions.Permissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.Socket;


public class ConversationActivity extends AppCompatActivity implements ChatAdapter.CallbackInterface, PickiTCallbacks {

    private EditText messageET;
    private TextView tv_name;
    private TextView tv_state;
    private AppCompatTextView gallery;
    private AppCompatTextView pdf;
    private AppCompatTextView contact;
    private AppCompatTextView location;
    private ImageView fowordImageBtn;
    private ImageView videoCallBtn;
    private ImageView audioCallBtn;
    private LinearLayout linerNoMessage;
    private LinearLayout linerNameState;




    WebView webView;
    private final int requestcode = 1;
    String peerId = null;
    boolean first=true;


    private ImageView backImageBtn;
    private CircleImageView personImage;
    private RecyclerView messagesContainer;
    private ImageButton sendMessageBtn;
    private ImageButton sendImageBtn;
    TimeProperties timeProperties;
    BlockUserRepo blockUserRepo;
    private Menu menu;
    TextView textForBlock;
    boolean blockedForMe = false;
    ServerApi serverApi;
    ConversationModelView conversationModelView;


    private ImageButton deletImageBtn;
    private ChatAdapter adapter;
    BaseApp myBase;
    ChatRoomRepo chatRoomRepo;
    String chat_id = "";
    String fcmToken;
    boolean isAllMessgeMe = true;
    boolean inChatRoom;
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    LatLng latLng;
    String stringLatLng;
    private LinearLayout openMaps;
    private Button sendLocation;
    private RelativeLayout relativeMaps;
    private RelativeLayout container;
    LatLng locationLatLng;
    //    Calendar cal;
    ChatMessageRepo chatMessageRepo;
    private static final String[] callPermissions = {android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO};


    private CardView cardOpenItLocation;

    String lat, longg;
    RelativeLayout view;
    int IMAGE_PICKER_SELECT = 600;
    private static final int PICK_IMAGE_FROM_GALLERY = 8396;
    private static final int RESULT_PICK_CONTACT = 1;

    ProgressDialog mProgressDialog;

    boolean viewVisability = false;
//  private String senderId;
//  private String reciverId;
    private String userName;
    private String imageUrl;
    Bitmap bitmap;
    String imageString;
    MediaController mediaControls;
    Toolbar toolbar;


    String audioPath, audioName;
//    String lastSeen = "";
    private static final int REQUEST_PERMISSIONS = 100;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_IMAGE_VIDEO = 1111;
    private String filePath;

    private static final int PICK_IMAGE = 100;
    private static final int MY_REQUEST_CODE_PERMISSION = 1000;
    private static final int MY_RESULT_CODE_FILECHOOSER = 2200;
    private static final int OPEN_MAP = 12121212;
    private static final int SEND_LOCATION = 13131313;



    ArrayList<String> returnValue = new ArrayList<>();
    private ArrayList<ChatMessage> chatHistory;
    private ArrayList<ChatMessage> selectedMessage = new ArrayList<>();
    private ArrayList<JSONObject> unSendMessage = new ArrayList<>();
    private final ArrayList<String> deleteMessage = new ArrayList<>();


    SearchView searchView;
    private final Boolean hasConnection = false;
    private Socket socket;
    private Timer timer = new Timer();
    private final long DELAY = 1000;
    String user_id = "8";
    String anthor_user_id = "9";
    String specialNumber = "";
    private Permissions permissions;
    private MediaRecorder mediaRecorder;
    RecordView recordView;
    RecordButton recordButton;
    LinearLayout messageLayout;
    LinearLayout personInformationLiner;
    LinearLayout toolsLiner;
    ClassSharedPreferences classSharedPreferences;
    LinearLayout messageLiner;


    public static final String CHEK = "ConversationActivity.CHECK_CONNECT";
    public static final String TYPING = "ConversationActivity.ON_TYPING";
    public static final String ON_MESSAGE_RECEIVED = "ConversationActivity.ON_MESSAGE_RECEIVED";
    public static final String ON_MESSAGE_DELETED = "ConversationActivity.ON_MESSAGE_DELETED";
    public static final String ON_MESSAGE_UPDATE = "ConversationActivity.ON_MESSAGE_UPDATE";
    public static final String ON_BLOCK_USER = "ConversationActivity.ON_BLOCK_USER";
    public static final String ON_UN_BLOCK_USER = "ConversationActivity.ON_UN_BLOCK_USER";


    String filepath = "";


    private RequestQueue rQueue;
    private ArrayList<HashMap<String, String>> arraylist;
    private static final String TAG = "MainActivity2";
    LinearLayout imageLiner;
    LinearLayout gallaryLiner;
    ImageButton moreOption;

    LinearLayout fileLiner;
    LinearLayout contactLiner;

    private static final String[] PERMISSIONS = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    ////////download() Method  PERMISSIONS

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /////////start recive from socket
///// for check if anthor user is connect
    private final BroadcastReceiver check = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String check = intent.getExtras().getString("check");
            JSONObject checkObject = null;
            String checkConnect = "false";
            String userId ;

            try {
                checkObject = new JSONObject(check);
                userId = checkObject.getString("user_id");
                if (userId.equals(anthor_user_id)) {
                    checkConnect = checkObject.getString("is_connect");

                    conversationModelView.setLastSeen( checkObject.getString("last_seen"));
                    conversationModelView.set_state(checkConnect);
                }
                } catch(JSONException e){
                    e.printStackTrace();
                }


        }
    };

    private final BroadcastReceiver reciveTyping = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String typingString = intent.getExtras().getString("typing");
                    JSONObject message = null;
                    String isTyping = "false";
                    String anthor_id = "";

                    try {
                        message = new JSONObject(typingString);
                        isTyping = message.getString("typing");
                        anthor_id = message.getString("my_id");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (anthor_user_id.equals(anthor_id)) {

                        conversationModelView.set_isTyping(isTyping);



                    }
                }
            });
        }
    };

    private final BroadcastReceiver reciveBlockUser = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String blockString = intent.getExtras().getString("block");

                    String userDoBlock = "";
                    String userBlock = "";
                    String blockedFor = "";
                    String name = "";
                    String image = "";
                    String special_number = "";
                    try {
                        JSONObject jsonObject = new JSONObject(blockString);
                        userDoBlock = jsonObject.getString("my_id");
                        userBlock = jsonObject.getString("user_id");
                        blockedFor = jsonObject.getString("blocked_for");
                        name = jsonObject.getString("userDoBlockName");
                        special_number = jsonObject.getString("userDoBlockSpecialNumber");
                        image = jsonObject.getString("userDoBlockImage");

                        System.out.println(blockString + "from here");


                        if(userDoBlock.equals(anthor_user_id)){

                            conversationModelView.setBlockedFor(blockedFor);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private final BroadcastReceiver reciveUnBlockUser = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String unBlockString = intent.getExtras().getString("unBlock");

                    String userDoUnBlock = "";
                    String userUnBlock = "";
                    String unBlockedFor = "";

                    try {
                        JSONObject jsonObject = new JSONObject(unBlockString);
                        userDoUnBlock = jsonObject.getString("my_id");
                        userUnBlock = jsonObject.getString("user_id");
                        unBlockedFor = jsonObject.getString("blocked_for");

                        if(userDoUnBlock.equals(anthor_user_id)){
                            System.out.println("doooo it");
//                            blockUserRepo.deleteBlockUser(userDoUnBlock,unBlockedFor);
                            conversationModelView.setBlockedFor(unBlockedFor);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };


    private final BroadcastReceiver reciveDeleteMessage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    String deleteString = intent.getExtras().getString("delete message");
                    JSONObject message = null;
                    System.out.println("recive delete message ");

                    try {
                        JSONObject jsonObject = new JSONObject(deleteString);
                        String deleteMessage = jsonObject.getString("message_to_delete");
                        JSONArray jsonArray = new JSONArray(deleteMessage);
                        String deleteChat_id = jsonObject.getString("chat_id");
                        String first_user_id = jsonObject.getString("first_id");
                        String second_user_id = jsonObject.getString("second_id");



                        if (anthor_user_id.equals(first_user_id) || anthor_user_id.equals(second_user_id)) {
                            conversationModelView.deleteMessageFromList(jsonArray);


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });
        }
    };
    private final BroadcastReceiver reciveUpdateMessage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String deleteString = intent.getExtras().getString("updateMessage");
                    JSONObject message = null;
                    System.out.println(deleteString + "deleteRespone");

                    try {
                        JSONObject jsonObject = new JSONObject(deleteString);
                        String message_id = jsonObject.getString("message_id");
                        String updateMessage = jsonObject.getString("message");


                        String first_user_id = jsonObject.getString("reciver_id");
                        String second_user_id = jsonObject.getString("sender_id");


                        if (anthor_user_id.equals(first_user_id) || anthor_user_id.equals(second_user_id)) {
                                conversationModelView.ubdateMessage(message_id,updateMessage);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });
        }
    };
    private final BroadcastReceiver reciveNwMessage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String objectString = intent.getExtras().getString("message");
                    JSONObject message = null;
                    try {
                        message = new JSONObject(objectString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.println("this is message " + message);

                    String text = "";
                    String type = "";
                    String state = "";
                    String senderId = "";
                    String reciverId = "";
                    String id = "";
                    String recive_chat_id = "";
                    String fileName = "";
                    String MessageDate = "";
                    try {

                        /// JSONObject jsonObject= (JSONObject) messageJson.get("data");
                        text = message.getString("message");
                        type = message.getString("message_type");
                        state = message.getString("state");
                        senderId = message.getString("sender_id");
                        id = message.getString("message_id");
                        reciverId = message.getString("reciver_id");
                        MessageDate = message.getString("dateTime");
                        recive_chat_id = message.getString("chat_id");
//                        fileName = message.getString("orginalName");


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    if (senderId.equals(user_id) && reciverId.equals(anthor_user_id)) {
                        if (!state.equals("3") && chat_id.equals(user_id + anthor_user_id)) {
//                            myBase.getObserver().setLastMessage(text, recive_chat_id, user_id, anthor_user_id, type, state, MessageDate);
                            if (!recive_chat_id.isEmpty()) {
                                chatRoomRepo.setLastMessage(text, recive_chat_id, user_id, anthor_user_id, type, state, MessageDate,user_id);

                                chat_id = recive_chat_id;
                            }
                        }
                        ////set last message
                        else{
                            if (!id.equals("0000")){
                                System.out.println("not id equels true");
                                chatRoomRepo.setLastMessage(text, recive_chat_id, user_id, anthor_user_id, type, state, MessageDate, senderId);
                            }
                            else{
                                System.out.println("elseeeeeeeeeeeeeeeeeeeee");

                                chatRoomRepo.updateLastMessageState(state,chat_id);
                            }
                        }


                        conversationModelView.setMessageState(id,state);

                    } else if (senderId.equals(anthor_user_id)) {

                        JSONObject jsonObject = new JSONObject();


                        try {
                            jsonObject.put("message_id", id);
                            jsonObject.put("sender_id", senderId);
                            jsonObject.put("reciver_id", reciverId);
                            jsonObject.put("message", text);
                            jsonObject.put("message_type", type);
                            jsonObject.put("state", "3");
                            jsonObject.put("chat_id", Integer.parseInt(recive_chat_id));

                            ////DateFormat.getDateTimeInstance().format(new Date())
                            jsonObject.put("dateTime", String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        onSeen(jsonObject);


                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setId(id);
                        if (type.equals("text") || type.equals("location")) {
                            chatMessage.setMessage(text);
                        } else if (type.equals("file") || type.equals("voice") || type.equals("video") || type.equals("contact")) {
                            chatMessage.setMessage(text);
                            try {
                                chatMessage.setFileName(message.getString("orginalName"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        } else {
                            chatMessage.setImage(text);
                            try {
                                chatMessage.setFileName(message.getString("orginalName"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        chatMessage.setType(type);
                        chatMessage.setState(state);
                        chatMessage.setDate(MessageDate);
                        chatMessage.setIsUpdate("0");
                        chatMessage.setMe(false);
                        try {
                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                            r.play();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (type.equals("text") || type.equals("location") || type.equals("contact") || type.equals("video"))
                            displayMessage(chatMessage);
//                        else new DownloadFileFromSocket(chatMessage).execute();
                        else processSocketFile(chatMessage);


                    }
                }
            });
        }
    };
////////////////// end recive from socket

    ////// start send to socket
    private void EnterRoom() {

        Intent service = new Intent(this, SocketIOService.class);
        JSONObject userEnter = new JSONObject();

        try {
            userEnter.put("my_id", user_id);
            userEnter.put("your_id", anthor_user_id);
            userEnter.put("state", "3");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        service.putExtra(SocketIOService.EXTRA_ENTER_PARAMTERS, userEnter.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_ENTER);
        startService(service);
    }

    ////// check connect
    private void checkConnect() {
        System.out.println("myId=" + user_id + "your_id=" + anthor_user_id);
        Intent service = new Intent(this, SocketIOService.class);
        JSONObject object = new JSONObject();
        try {
            object.put("my_id", user_id);
            object.put("your_id", anthor_user_id);
//            socket.emit("check connect", object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        service.putExtra(SocketIOService.EXTRA_CHECK_CONNECT_PARAMTERS, object.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_CHECK_CONNECT);
        startService(service);
    }

    //////////////////// onTyping
    private void onTyping(boolean typing) {
        Intent service = new Intent(this, SocketIOService.class);
        JSONObject onTyping = new JSONObject();
        try {
            onTyping.put("id", anthor_user_id);
            onTyping.put("typing", typing);
            onTyping.put("my_id", user_id);
            onTyping.put("chat_id", chat_id);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        service.putExtra(SocketIOService.EXTRA_TYPING_PARAMTERS, onTyping.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_TYPING);
        startService(service);

    }

    //////////onNewMessage
    public void newMeesage(JSONObject chatMessage) {


        String message = "";
        String type = "";
        String time = "1646028789098";
        try {
            type = chatMessage.getString("message_type");
            time = chatMessage.getString("dateTime");
            if (type.equals("text")) {
                message = chatMessage.getString("message");
            } else if (type.equals("imageWeb") || type.equals("location")) {
                message = "photo";
            } else {
                message = chatMessage.getString("orginalName");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (chat_id.isEmpty()) {
            chat_id = user_id + anthor_user_id;
            chatRoomRepo.getChatRoomModelList().add(new ChatRoomModel(userName, anthor_user_id, message, imageUrl, false, "0", user_id + anthor_user_id, "null", "0", true, fcmToken, specialNumber, type, "1", time, false, "null",user_id));
        }

        serverApi.sendNotification(message, type,fcmToken,chat_id, conversationModelView.blockedFor().getValue());
        Intent service = new Intent(this, SocketIOService.class);
        service.putExtra(SocketIOService.EXTRA_NEW_MESSAGE_PARAMTERS, chatMessage.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_MESSAGE);
        startService(service);

    }

    /////////////////onSeen
    private void onSeen(JSONObject chatMessage) {
        Intent service = new Intent(this, SocketIOService.class);

        service.putExtra(SocketIOService.EXTRA_ON_SEEN_PARAMTERS, chatMessage.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_ON_SEEN);
        startService(service);

    }

    private void deletForAll(JSONObject chatMessage) {
        Intent service = new Intent(this, SocketIOService.class);

        service.putExtra(SocketIOService.EXTRA_ON_DELETE_PARAMTERS, chatMessage.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_ON_DELETE);
        startService(service);

    }
    private void sendBlockFor(Boolean blocked) {
        //                    item.put("blocked_for",conversationModelView.blockedFor().getValue());
//                    item.put("Block",blocked);
                 JSONObject userBlocked = new JSONObject();
                JSONObject item = new JSONObject();
                UserModel userModel = classSharedPreferences.getUser();


                try {
                    System.out.println("before rBlocked"+userBlocked.toString());
                    userBlocked.put("my_id", user_id);
                    userBlocked.put("user_id",anthor_user_id );
                    userBlocked.put("blocked_for",conversationModelView.blockedFor().getValue());
                    userBlocked.put("userDoBlockName",userModel.getUserName());
                    userBlocked.put("userDoBlockSpecialNumber",userModel.getSecretNumber());
                    userBlocked.put("userDoBlockImage",userModel.getImage());
                 System.out.println("userBlocked"+userBlocked.toString());

                } catch (JSONException e) {
                    System.out.println(e+"errorrrrrrrr");
                    e.printStackTrace();
                }

        Intent service = new Intent(this, SocketIOService.class);


        service.putExtra(SocketIOService.EXTRA_BLOCK_PARAMTERS, userBlocked.toString());
                service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_BLOCK);

        startService(service);


    }

    private void sendUnBlockFor(Boolean blocked) {

                        JSONObject userUnBlocked = new JSONObject();

                try {
                    userUnBlocked.put("my_id", user_id);
                    userUnBlocked.put("user_id",anthor_user_id );
                    userUnBlocked.put("blocked_for",conversationModelView.blockedFor().getValue());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
        Intent service = new Intent(this, SocketIOService.class);


        service.putExtra(SocketIOService.EXTRA_UN_BLOCK_PARAMTERS, userUnBlocked.toString());
                service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_UN_BLOCK);
                startService(service);


    }

    private void updateMessage(JSONObject chatMessage) {
        Intent service = new Intent(this, SocketIOService.class);

        service.putExtra(SocketIOService.EXTRA_ON_UPDTE_MESSAGE_PARAMTERS, chatMessage.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_ON_UPDATE_MESSAGE);
        startService(service);

    }

    ///////////////end

    float textSize = 14.0F;
    int progressNew = 0;

    SharedPreferences sharedPreferences;
    PickiT pickiT;

    public static TextView reply;
    public static TextView username;
    public static ImageButton close;
    public static CardView cardview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        sharedPreferences = getSharedPreferences("txtFontSize", Context.MODE_PRIVATE);
        pickiT = new PickiT(this, this, this);
        if (!isPermissionGranted()) {
            askPermissions();
        }


         reply    = (TextView)findViewById(R.id.reply);
         username = (TextView)findViewById(R.id.username);
         close    = (ImageButton)findViewById(R.id.close);
         cardview = (CardView) findViewById(R.id.cardview);
         linerNoMessage = findViewById(R.id.liner_no_messsage);
        linerNameState = findViewById(R.id.name_state);
         pickiT   = new PickiT(this, this, this);


        initViews();
        initAction();
        EnterRoom();
        checkConnect();
        LocalBroadcastManager.getInstance(this).registerReceiver(check, new IntentFilter(CHEK));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveTyping, new IntentFilter(TYPING));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveNwMessage, new IntentFilter(ON_MESSAGE_RECEIVED));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveDeleteMessage, new IntentFilter(ON_MESSAGE_DELETED));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveUpdateMessage, new IntentFilter(ON_MESSAGE_UPDATE));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveBlockUser, new IntentFilter(ON_BLOCK_USER));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveUnBlockUser, new IntentFilter(ON_UN_BLOCK_USER));




    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("hasConnection", hasConnection);
    }


    private void initViews() {

        timeProperties = new TimeProperties();
        conversationModelView = new ViewModelProvider(this).get(ConversationModelView.class);
        messageLiner = findViewById(R.id.liner);
        videoCallBtn = findViewById(R.id.video_call);
        audioCallBtn = findViewById(R.id.audio_call);
        myBase = BaseApp.getInstance();
        textForBlock = findViewById(R.id.text_for_block);
        serverApi = new ServerApi(this);
        chatRoomRepo = myBase.getChatRoomRepo();
        blockUserRepo = myBase.getBlockUserRepo();
        Bundle bundle = getIntent().getExtras();
        user_id = bundle.getString("sender_id", "1");
        anthor_user_id = bundle.getString("reciver_id", "2");
        userName = bundle.getString("name", "user");
        System.out.println("userrrrNAme"+userName);
        imageUrl = bundle.getString("image");
        specialNumber = bundle.getString("special", "");
        chat_id = bundle.getString("chat_id", "");
        if (chat_id.isEmpty()) {
            if(chatRoomRepo!=null) {
                chat_id = chatRoomRepo.getChatId(anthor_user_id);
            }
        }
        fcmToken = bundle.getString("fcm_token", "");
        personImage = findViewById(R.id.user_image);
        if (!imageUrl.isEmpty()) {
            Glide.with(personImage).load(AllConstants.imageUrl+imageUrl).apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th)).into(personImage);
        }
        String blockedFor
         = bundle.getString("blockedFor", null);
        conversationModelView.setBlockedFor(blockedFor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            closeCurrentNotification();
        }

        chatMessageRepo = myBase.getChatMessageRepo();
        chatMessageRepo.getChatHistory(user_id, anthor_user_id);
        backImageBtn = findViewById(R.id.image_button_back);
        LinearLayout linearLayout = findViewById(R.id.liner_conversation);
        messagesContainer = findViewById(R.id.messagesContainer);
        messagesContainer.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        messagesContainer.setLayoutManager(linearLayoutManager);
        messageET = findViewById(R.id.messageEdit);
        sendMessageBtn = findViewById(R.id.btn_send_message_text);
        sendImageBtn = findViewById(R.id.btn_send_message_image);
        searchView = findViewById(R.id.search_con);
        fowordImageBtn = findViewById(R.id.image_button_foword);

        tv_name = findViewById(R.id.name);


        tv_state = findViewById(R.id.state);


        gallery = findViewById(R.id.gallery);
        gallery.setTextSize(textSize);

        pdf = findViewById(R.id.pdf);
        pdf.setTextSize(textSize);

        contact = findViewById(R.id.contact);
        contact.setTextSize(textSize);

        location = findViewById(R.id.location);
        location.setTextSize(textSize);


        CharSequence charSequence = searchView.getQuery();
        view = findViewById(R.id.dataLayout);
        imageLiner = findViewById(R.id.lytCameraPick);
        fileLiner = findViewById(R.id.pickFile);
        gallaryLiner = findViewById(R.id.lytGallaryPick);
        contactLiner = findViewById(R.id.pick_contact);
        permissions = new Permissions();
        messageLayout = findViewById(R.id.messageLayout);
        personInformationLiner = findViewById(R.id.person_information_liner);
        toolsLiner = findViewById(R.id.tools_liner_layout);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);

        //Initialize fused location
        client = LocationServices.getFusedLocationProviderClient(this);


        recordView = findViewById(R.id.recordView);
        recordButton = findViewById(R.id.recordButton);
        deletImageBtn = findViewById(R.id.image_button_delete);
        recordButton.setRecordView(recordView);

        recordButton.setListenForRecord(false);
        deletImageBtn = findViewById(R.id.image_button_delete);
        classSharedPreferences = BaseApp.getInstance().getClassSharedPreferences();
//
//        if (classSharedPreferences.getList() != null) {
//            unSendMessage = classSharedPreferences.getList();
//
//        }

        chatRoomRepo.setInChat(anthor_user_id, true);
        container = findViewById(R.id.container);
        openMaps = findViewById(R.id.pick_location);
        sendLocation = findViewById(R.id.sendLocation);
        relativeMaps = findViewById(R.id.relativeMaps);
        cardOpenItLocation = findViewById(R.id.cardOpenItLocation);


        conversationModelView.blockedFor().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                boolean isAnyOneBlock = false;
                if (s != null) {
                    if (s.equals(user_id)) {
                        textForBlock.setText(getResources().getString(R.string.block_message));
                        audioCallBtn.setEnabled(false);
                        videoCallBtn.setEnabled(false);
                        tv_state.setVisibility(View.GONE);
                        textForBlock.setVisibility(View.VISIBLE);
                        messageLiner.setVisibility(View.GONE);
                        blockedForMe = true;
                        isAnyOneBlock = true;
                    } else if (s.equals(anthor_user_id)) {
                        textForBlock.setVisibility(View.VISIBLE);
                        messageLiner.setVisibility(View.GONE);
                        audioCallBtn.setEnabled(false);
                        videoCallBtn.setEnabled(false);
                        tv_state.setVisibility(View.GONE);
                        textForBlock.setText(getResources().getString(R.string.block_message2));
                        blockedForMe = false;
                        isAnyOneBlock = true;


                    } else if (s.equals("0")) {
                        textForBlock.setVisibility(View.VISIBLE);
                        messageLiner.setVisibility(View.GONE);
                        audioCallBtn.setEnabled(false);
                        videoCallBtn.setEnabled(false);
                        tv_state.setVisibility(View.GONE);
                        textForBlock.setText(getResources().getString(R.string.block_message2));
                        blockedForMe = true;
                        isAnyOneBlock = true;


                    }

                } else {
                    textForBlock.setVisibility(View.GONE);
                    messageLiner.setVisibility(View.VISIBLE);
                    blockedForMe = false;
                    isAnyOneBlock = false;
                }


                if (!isAnyOneBlock) {
                    textForBlock.setVisibility(View.GONE);
                    messageLiner.setVisibility(View.VISIBLE);
                    audioCallBtn.setEnabled(true);
                    videoCallBtn.setEnabled(true);
                    tv_state.setVisibility(View.VISIBLE);
                    blockedForMe = false;
                }
            }

        });
        ////
        conversationModelView.isBlocked().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
                System.out.println("stateee"+s);
                if(s!=null){
//                    conversationModelView.
                    sendBlockFor(s);
                    conversationModelView.setBlocked(null);


                }
            }
        });
        ////////////
        conversationModelView.isUnBlocked().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
                System.out.println("stateee"+s);
                if(s!=null){
//                    conversationModelView.
                    sendUnBlockFor(s);
                    conversationModelView.setUnBlocked(null);


                }
            }
        });

        /////////
        chatHistory = new ArrayList<ChatMessage>();
        conversationModelView.state.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                System.out.println("stateee"+s);
                if(!s.equals(null)){
                    if (s.equals("true")) {
//                        isCoonect = true;
                        System.out.println("dialay"+s);
                        tv_state.setText(R.string.connect_now);
                    } else if (s.equals("false")) {
//                        isCoonect = false;

                        if (!conversationModelView.getLastSeen().equals("null")) {
                            tv_state.setText(getResources().getString(R.string.last_seen) + " " + timeProperties.getDateForLastSeen(ConversationActivity.this, Long.parseLong(conversationModelView.getLastSeen())));
                        }


                    }

                }
            }
        });
        conversationModelView.isTyping.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                System.out.println("stateee"+s);
                if(!s.equals(null)){
                    if (s.equals("true")) {
                        tv_state.setText(R.string.writing_now);
                    } else if (conversationModelView.state.getValue().equals("true")) {
                        tv_state.setText(R.string.connect_now);
                    } else {
                        tv_state.setText(getResources().getString(R.string.last_seen) + " " + timeProperties.getDateForLastSeen(ConversationActivity.this, Long.parseLong(conversationModelView.getLastSeen())));


                    }

                }
            }
        });

        conversationModelView.getChatMessaheHistory().observe(this, new Observer<ArrayList<ChatMessage>>() {
            @Override
            public void onChanged(ArrayList<ChatMessage> chatMessages) {
                if (chatMessages != null) {
                    if(chatMessages.isEmpty()){
                        linerNoMessage.setVisibility(View.VISIBLE);
                        messagesContainer.setVisibility(View.GONE);
                    }
                    else {
                        linerNoMessage.setVisibility(View.GONE);
                        messagesContainer.setVisibility(View.VISIBLE);
                        ArrayList<ChatMessage> list = new ArrayList<>();
                        System.out.println("chatmessage.size" + chatMessages.size() + "" + chatHistory.size());
                        for (ChatMessage chatMessage : chatMessages) {
                            list.add(chatMessage.clone());
                        }

                        chatHistory = list;
//                    adapter.add(chatHistory);
                        adapter.setData(list);
                    }
                    if(!chatMessages.isEmpty() && conversationModelView.isFirst.getValue()){
                        conversationModelView.isFirst.setValue(false);
                    scroll();
                    }


                }


            }
        });
//        scroll();
        conversationModelView.getSelectedMessage().observe(this, new Observer<ArrayList<ChatMessage>>() {
            @Override
            public void onChanged(ArrayList<ChatMessage> chatMessages) {
                if (chatMessages != null) {
//                    selectedMessage = chatMessages;
                    if(chatMessages.isEmpty()){
                        toolsLiner.setVisibility(View.GONE);
                        personInformationLiner.setVisibility(View.VISIBLE);
                        toolbar.setBackgroundColor(getResources().getColor(R.color.memo_background_color));
                    }

                }
                //adapter.notifyDataSetChanged();

            }
        });
        adapter = new ChatAdapter(ConversationActivity.this);
        messagesContainer.setAdapter(adapter);
        messagesContainer.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    messagesContainer.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (adapter.getCurrentList().size() > 1)
                                messagesContainer.smoothScrollToPosition(
                                        adapter.getCurrentList().size() - 1);
                        }
                    }, 100);
                }
            }
        });



        openMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideLayout();
                makeMapsAction();
                container.setVisibility(View.GONE);
                relativeMaps.setVisibility(View.VISIBLE);

//
            }
        });
        sendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stringLatLng==null){
//                    Toast.makeText(ConversationActivity.this, getString(R.string.gps), Toast.LENGTH_SHORT).show();

                    return;
                }
                hideLayout();

                System.out.println(stringLatLng+"stringLatLng");
                String message_id = System.currentTimeMillis() + "_" + user_id;

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("sender_id", user_id);
                    jsonObject.put("reciver_id", anthor_user_id);
                    jsonObject.put("message", stringLatLng);
                    jsonObject.put("message_type", "location");
                    jsonObject.put("state", "0");
                    jsonObject.put("message_id", message_id);
                    jsonObject.put("dateTime", String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("locationnnnn"+jsonObject);
//                newMeesage(jsonObject);
                relativeMaps.setVisibility(View.GONE);
                container.setVisibility(View.VISIBLE);
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setId(message_id);//dummy
                chatMessage.setMessage(stringLatLng);
                chatMessage.setDate(String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis()));
                chatMessage.setMe(true);
                chatMessage.setType("location");

                chatMessage.setState("0");
                chatMessage.setChecked(false);
//                messageET.setText("");
                displayMessage(chatMessage);
                newMeesage(jsonObject);


            }
        });

    }

    private void initAction() {
        tv_name.setText(userName);


        linerNameState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), UserInformationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("user_id", anthor_user_id);
                bundle.putString("name", userName);
                bundle.putString("image", imageUrl);
                bundle.putString("fcm_token", fcmToken);
                bundle.putString("special", specialNumber);
                bundle.putString("chat_id", chat_id);
                bundle.putString("blockedFor", conversationModelView.blockedFor().getValue());


                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        backImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        messageET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (timer != null) {
                    timer.cancel();
                }
//
                onTyping(true);

                if (charSequence.toString().trim().length() > 0) {
                    sendMessageBtn.setEnabled(true);
                    recordButton.setVisibility(View.GONE);
                    sendMessageBtn.setVisibility(View.VISIBLE);
                } else {
                    sendMessageBtn.setEnabled(false);
                    sendMessageBtn.setVisibility(View.GONE);
                    cardview.setVisibility(View.GONE);
                    recordButton.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() >= 0) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // TODO: do what you need here (refresh list)

                            onTyping(false);
                        }


                    }, DELAY);
                }


            }
        });
///for send textMessage
        sendImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.song);

                if (!viewVisability) {
                    System.out.println("show dialog");
                    showLayout();
                } else
                    hideLayout();
            }

            ///////////////////
        });
        /////////////////////////send btn
        videoCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                System.out.println("clickeddddddd");
//                startCall();

                Intent intent = new Intent(ConversationActivity.this, RequestCallActivity.class);
//                Intent intent = new Intent(ConversationActivity.this, CompleteActivity.class);
                intent.putExtra("anthor_user_id", anthor_user_id);
                intent.putExtra("user_name", userName);
                intent.putExtra("isVideo", true);
                intent.putExtra("fcm_token", fcmToken);
                intent.putExtra("image_profile", imageUrl);





                startActivity(intent);
            }
        });
        audioCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                System.out.println("clickeddddddd");
//                startCall();

                Intent intent = new Intent(ConversationActivity.this, RequestCallActivity.class);
                intent.putExtra("anthor_user_id", anthor_user_id);
                intent.putExtra("user_name", userName);
                intent.putExtra("isVideo", false);
                intent.putExtra("fcm_token", fcmToken);
                intent.putExtra("image_profile", imageUrl);



                startActivity(intent);
            }
        });


        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username.setVisibility(View.GONE);
                reply.setVisibility(View.GONE);

                String message_id = System.currentTimeMillis() + "_" + user_id;

//                if (classSharedPreferences.getList() != null) {
//                    unSendMessage = classSharedPreferences.getList();
//
//                }
                String messageText = messageET.getText().toString();

                if (TextUtils.isEmpty(messageText)) {
                    return;
                }
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("sender_id", user_id);
                    jsonObject.put("reciver_id", anthor_user_id);
                    jsonObject.put("message", messageText);
                    jsonObject.put("message_type", "text");
                    jsonObject.put("state", "0");
                    jsonObject.put("message_id", message_id);
                    jsonObject.put("dateTime", String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setId(message_id);//dummy
                chatMessage.setMessage(messageText);
                chatMessage.setDate(String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis()));
                chatMessage.setMe(true);
                chatMessage.setUserId(user_id);

                chatMessage.setType("text");
                chatMessage.setState("0");
                chatMessage.setChecked(false);
                chatMessage.setId(message_id);
                chatMessage.setIsUpdate("0");


                messageET.setText("");
                displayMessage(chatMessage);
//                unSendMessage.clear();
//
//                unSendMessage.add(jsonObject);
//              unSendMessage.clear();




                newMeesage(jsonObject);

            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                adapter.filter(newText);
                return false;
            }
        });
        ////to pick image
        imageLiner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideLayout();
                Options options = Options.init()
                        .setRequestCode(PICK_IMAGE_VIDEO)                                           //Request code for activity results
                        .setCount(1)


                        //Number of images to restict selection count
                        .setFrontfacing(false)                                         //Front Facing camera on start
//                        .setPreSelectedUrls(returnValue)
                        .setSpanCount(4)                                               //Span count for gallery min 1 & max 5
                        .setMode(Options.Mode.All)                                     //Option to select only pictures or videos or both
                        .setVideoDurationLimitinSeconds(30)

                        //Duration for video recording
                        .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT);     //Orientaion
                //Custom Path For media Storage

                Pix.start(ConversationActivity.this, options);

            }
        });
        ////pick from gallery
        gallaryLiner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideLayout();
//                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                pickIntent.setType("image/* video/*");
//                startActivityForResult(pickIntent, PICK_IMAGE_FROM_GALLERY);
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
                startActivityForResult(intent, PICK_IMAGE_FROM_GALLERY);
            }
        });
        //// to pick file
        fileLiner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideLayout();
                System.out.println("file");
                askPermissionAndBrowseFile();

            }
        });
        contactLiner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideLayout();
                checkContactpermission();
            }
        });


        //// for voice record
        recordButton.setListenForRecord(true);

        recordButton.setOnClickListener(view -> {

        });
        recordView.setLessThanSecondAllowed(false);
        recordView.setRecordPermissionHandler(new RecordPermissionHandler() {
            @Override

            public boolean isPermissionGranted() {
                System.out.println("permission");
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    return true;
                }

                if (permissions.isRecordingOk(ConversationActivity.this))
                    if (permissions.isStorageReadOk(ConversationActivity.this))
                        return true;
                    else permissions.requestStorage(ConversationActivity.this);
                else permissions.requestRecording(ConversationActivity.this);


                return false;

            }
        });


        recordView.setSlideToCancelText(getResources().getString(R.string.slide_to_cancel));


        recordView.setCustomSounds(0, R.raw.record_finished, 0);
        recordView.setTimeLimit(30000);//30 sec


        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                //Start Recording..
                Log.d("RecordView", "onStart");

                setUpRecording();

                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                messageLayout.setVisibility(View.GONE);
                recordView.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancel() {
                //On Swipe To Cancel
                Log.d("RecordView", "onCancel");

                mediaRecorder.reset();
                mediaRecorder.release();
                File file = new File(audioPath);
                if (file.exists())
                    file.delete();

                recordView.setVisibility(View.GONE);
                messageLayout.setVisibility(View.VISIBLE);


            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                //Stop Recording..
                Log.d("RecordView", "onFinish");

                try {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                recordView.setVisibility(View.GONE);
                messageLayout.setVisibility(View.VISIBLE);

                File f = new File(audioPath);

              ChatMessage chatMessage = FileUtil.uploadVoice(audioName, Uri.fromFile(f),ConversationActivity.this, user_id,anthor_user_id);
              displayMessage(chatMessage);

            }


            @Override
            public void onLessThanSecond() {
                //When the record time is less than One Second
                Log.d("RecordView", "onLessThanSecond");

                mediaRecorder.reset();
                mediaRecorder.release();

                File file = new File(audioPath);
                if (file.exists())
                    file.delete();


                recordView.setVisibility(View.GONE);
                messageLayout.setVisibility(View.VISIBLE);
            }
        });

        /////////// for deleteMessage
        deletImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDeleteDialog();
            }
        });
        ///// for forward message
        fowordImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                ForwardDialogFragment forwardDialogFragment = ForwardDialogFragment.newInstance(conversationModelView.getSelectedMessage().getValue(), "jj");
                forwardDialogFragment.show(fm, "fragment_edit_name");
            }
        });
//
//        ImageButton relyying = (ImageButton)findViewById(R.id.image_button_reply);
//        relyying.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//
//                    username.setText(userName);
//                    reply.setText(conversationModelView.getSelectedMessage().getValue().get(0).message);
//                    close.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//
//                            cardview.setVisibility(View.GONE);
//
//                        }
//                    });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                reply.setVisibility(View.VISIBLE);
//                username.setVisibility(View.VISIBLE);
//                close.setVisibility(View.VISIBLE);
//                cardview.setVisibility(View.VISIBLE);
//            }
//        });

    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.conversation_menu, menu);

        return true;
    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem copyItem = menu.findItem(R.id.item_copy);
        MenuItem blockItem = menu.findItem(R.id.item_block);
        MenuItem unblockItem = menu.findItem(R.id.item_unblock);
        MenuItem updateItem = menu.findItem(R.id.item_update);
        System.out.println("preparing message");


        if(conversationModelView.getSelectedMessage().getValue()!=null) {
            ////when selected message > 1 copy and update disable
             if (conversationModelView.getSelectedMessage().getValue().size() > 1) {
                 copyItem.setVisible(false);
                 updateItem.setVisible(false);

             } else if (conversationModelView.getSelectedMessage().getValue().size() == 1) {
                 ////copy and update when message type text only
                 if (conversationModelView.getSelectedMessage().getValue().get(0).getType().equals("text")) {
                     copyItem.setVisible(true);
                     updateItem.setVisible(conversationModelView.getSelectedMessage().getValue().get(0).getUserId().equals(user_id));

                 } else {
                     updateItem.setVisible(false);
                     copyItem.setVisible(false);

                 }


             } else {
                 copyItem.setVisible(false);
                 updateItem.setVisible(false);


             }
         }
        else {
            copyItem.setVisible(false);
            updateItem.setVisible(false);
        }
        //// for block
        if (blockedForMe) {
            blockItem.setVisible(false);
            unblockItem.setVisible(true);
        } else {
            blockItem.setVisible(true);
            unblockItem.setVisible(false);


        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        UserModel userModel = new UserModel(anthor_user_id, userName, "", "", "", specialNumber, imageUrl, "");


        switch (id) {
            case R.id.item_copy:
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("key", conversationModelView.getSelectedMessage().getValue().get(0).getMessage());
                clipboardManager.setPrimaryClip(clipData);
                conversationModelView.setMessageChecked(conversationModelView.getSelectedMessage().getValue().get(0).getId(),false);

                conversationModelView.clearSelectedMessage();
                deleteMessage.clear();

                return true;
            case R.id.item_update:
                showUpdateMessageDialog(conversationModelView.getSelectedMessage().getValue().get(0));

                return true;
            case R.id.item_block:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle(R.string.alert_block_user);
                dialog.setPositiveButton(R.string.block,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {

//                                serverApi.block(user_id, userModel);
                                conversationModelView.sendBlockRequest(user_id,anthor_user_id);
                            }
                        });
                dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = dialog.create();
                alertDialog.show();

                break;


            case R.id.item_unblock:
                AlertDialog.Builder dialogUnBlock = new AlertDialog.Builder(this);
                dialogUnBlock.setTitle(R.string.alert_unblock_user);
                dialogUnBlock.setPositiveButton(R.string.Unblock,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {

//                                serverApi.unbBlockUser(user_id, userModel);
                                conversationModelView.sendUnBlockRequest(user_id,anthor_user_id);
                            }
                        });
                dialogUnBlock.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertUnBlockDialog = dialogUnBlock.create();
                alertUnBlockDialog.show();

                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }



    ///// End initialAction
    private void setUpRecording() {

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        File file = new File(this.getExternalFilesDir(Environment.DIRECTORY_DCIM).getAbsolutePath(), "memo/send/voiceRecord");
//        Environment.DIRECTORY_DCIM+ File.separator+
//                Environment.getExternalStoragePublicDirectory
        if (!file.exists())
            file.mkdirs();


        audioName = System.currentTimeMillis() + ".mp3";
        audioPath = file.getAbsolutePath() + "/" + audioName;


        mediaRecorder.setOutputFile(audioPath);
    }

    ////// ask premission for pick file
    private void askPermissionAndBrowseFile() {
        // With Android Level >= 23, you have to ask the user
        // for permission to access External Storage.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) { // Level 23

            // Check if we have Call permission
            int permisson = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permisson != PackageManager.PERMISSION_GRANTED) {
                // If don't have permission so prompt the user.
                this.requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_REQUEST_CODE_PERMISSION
                );

                return;
            }
        }
        this.doBrowseFile();
    }

    //// for add message to list and display ie]t
    public void
    displayMessage(ChatMessage message) {
        conversationModelView.addMessage(message);
        scroll();
//        scroll();
    }

    /////for browse file from device
    private void doBrowseFile() {
        Intent chooseFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFileIntent.setType("application/pdf");
        // Only return URIs that can be opened with ContentResolver
        chooseFileIntent.addCategory(Intent.CATEGORY_OPENABLE);

        chooseFileIntent = Intent.createChooser(chooseFileIntent, "Choose a file");
        startActivityForResult(chooseFileIntent, MY_RESULT_CODE_FILECHOOSER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_REQUEST_CODE_PERMISSION: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    this.doBrowseFile();
                }
                // Cancelled or denied.
                else {
                    DialogProperties.showPermissionDialog(getResources().getString(R.string.read_premission), AllConstants.READ_STORAGE_PERMISSION_REJECT,ConversationActivity.this);
                }
                break;
            }
            case AllConstants.RECORDING_REQUEST_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (this.permissions.isStorageReadOk(ConversationActivity.this))
                        return;
                    else this.permissions.requestStorage(ConversationActivity.this);

                } else
                    DialogProperties.showPermissionDialog(getResources().getString(R.string.record_voice_premission), AllConstants.RECORD_AUDIO_PERMISSION_REJECT,ConversationActivity.this);

                break;
            }
            case AllConstants.STORAGE_REQUEST_CODE:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    return;
                else
//                    showPermissionDialog(getResources().getString(R.string.read_premission), 1777);
                    DialogProperties.showPermissionDialog(getResources().getString(R.string.read_premission), AllConstants.STORAGE_PERMISSION_REJECT,ConversationActivity.this);

                break;
            case AllConstants.LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // When Permission Granted
                    //Call Method
                    getCurrentLocation();
                } else {
                    DialogProperties.showPermissionDialog(getResources().getString(R.string.location_premission), AllConstants.LOCATION_PERMISSION_REJECT,ConversationActivity.this);

                }
                break;
            case AllConstants.OPEN_MAP_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // When Permission Granted
                    //Call Method
                    openMap();

                } else {
                    DialogProperties.showPermissionDialog(getResources().getString(R.string.location_premission), AllConstants.OPEN_MAP_PERMISSION_REJECT,ConversationActivity.this);

                }
                break;
            case AllConstants.OPEN_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Options options = Options.init()
                            .setRequestCode(PICK_IMAGE_VIDEO)                                           //Request code for activity results
                            .setCount(1)

                            //Number of images to restict selection count
                            .setFrontfacing(false)                                         //Front Facing camera on start
//                            .setPreSelectedUrls(returnValue)
                            .setSpanCount(4
                            )                                               //Span count for gallery min 1 & max 5
                            .setMode(Options.Mode.All)                                     //Option to select only pictures or videos or both
                            .setVideoDurationLimitinSeconds(30)

                            //Duration for video recording
                            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT);     //Orientaion
                    //Custom Path For media Storage

                    Pix.start(ConversationActivity.this, options);
                } else {
                    DialogProperties.showPermissionDialog(getResources().getString(R.string.camera_premission), AllConstants.CAMERA_PERMISSION_REJECT,ConversationActivity.this);
                }
                break;
            case AllConstants.CONTACTS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent in = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                    startActivityForResult(in, RESULT_PICK_CONTACT);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                        DialogProperties.showPermissionDialog(getResources().getString(R.string.contact_permission), AllConstants.READ_CONTACT_PERMISSION_REJECT,ConversationActivity.this);

                    }
                }

                break;
        }
    }

    private void showLayout() {
        float radius = Math.max(view.getWidth(), view.getHeight());
        Animator animator = ViewAnimationUtils.createCircularReveal(view, view.getLeft(), view.getTop(), 0, radius * 2);
        animator.setDuration(800);
        view.setVisibility(View.VISIBLE);
        viewVisability = true;
        animator.start();

    }

    private void hideLayout() {
        System.out.println("hideLayout");
        float radius = Math.max(view.getWidth(), view.getHeight());
        Animator animator = ViewAnimationUtils.createCircularReveal(view, view.getLeft(), view.getTop(), radius * 2, 0);
        animator.setDuration(800);
        viewVisability = false;
        view.setVisibility(View.INVISIBLE);


        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.INVISIBLE);
                System.out.println("View.INVISIBLE)");
                viewVisability = false;

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                System.out.println("onAnimationCancel");


            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                System.out.println("onAnimationCancel");

            }
        });


    }


    private void scroll() {
        messagesContainer.postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("scrolll"+conversationModelView.getChatMessaheHistory().getValue().size());

                // Select the last row so it will scroll into view...
//                if (conversationModelView.getChatMessaheHistory().getValue().size() > 0) {
                if (conversationModelView.getChatMessaheHistory().getValue().size() > 0) {


//                    messagesContainer.scrollToPosition(conversationModelView.getChatMessaheHistory().getValue().size() - 1);
                    messagesContainer.getLayoutManager().scrollToPosition( conversationModelView.getChatMessaheHistory().getValue().size() - 1);


                }

            }
        },100);
//        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    //// for get all message
    @SuppressLint("Range")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String message_id = System.currentTimeMillis() + "_" + user_id;
        System.out.println(requestCode+"requestCode");
        if (requestCode == AllConstants.READ_STORAGE_PERMISSION_REJECT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                DialogProperties.showPermissionDialog(getResources().getString(R.string.read_premission), AllConstants.READ_STORAGE_PERMISSION_REJECT,ConversationActivity.this);

            } else {
                this.doBrowseFile();
            }
        } else if (requestCode == AllConstants.RECORD_AUDIO_PERMISSION_REJECT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
                DialogProperties.showPermissionDialog(getResources().getString(R.string.record_voice_premission), AllConstants.RECORD_AUDIO_PERMISSION_REJECT,ConversationActivity.this);

            } else {
                if (this.permissions.isStorageReadOk(ConversationActivity.this))
                    return;
                else this.permissions.requestStorage(ConversationActivity.this);

            }
        } else if (requestCode == AllConstants.STORAGE_PERMISSION_REJECT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                DialogProperties.showPermissionDialog(getResources().getString(R.string.read_premission), AllConstants.STORAGE_PERMISSION_REJECT,ConversationActivity.this);


            } else {
                return;
            }

        } else if (requestCode == AllConstants.CAMERA_PERMISSION_REJECT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                DialogProperties.showPermissionDialog(getResources().getString(R.string.camera_premission), AllConstants.CAMERA_PERMISSION_REJECT,ConversationActivity.this);


            } else {
                Options options = Options.init()
                        .setRequestCode(PICK_IMAGE_VIDEO)                                           //Request code for activity results
                        .setCount(1)

                        //Number of images to restict selection count
                        .setFrontfacing(false)                                         //Front Facing camera on start
                        .setPreSelectedUrls(returnValue)
                        .setSpanCount(1
                        )                                               //Span count for gallery min 1 & max 5
                        .setMode(Options.Mode.All)                                     //Option to select only pictures or videos or both
                        .setVideoDurationLimitinSeconds(30)

                        //Duration for video recording
                        .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT);     //Orientaion
                //Custom Path For media Storage

                Pix.start(ConversationActivity.this, options);
            }

        } else if (requestCode == AllConstants.LOCATION_PERMISSION_REJECT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                DialogProperties.showPermissionDialog(getResources().getString(R.string.location_premission),  AllConstants.LOCATION_PERMISSION_REJECT,ConversationActivity.this);


            } else {
                getCurrentLocation();
            }
        } else if (requestCode == AllConstants.OPEN_MAP_PERMISSION_REJECT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                DialogProperties.showPermissionDialog(getResources().getString(R.string.location_premission), AllConstants.OPEN_MAP_PERMISSION_REJECT,ConversationActivity.this);
            } else {
                openMap();
            }
        } else if (requestCode == AllConstants.READ_CONTACT_PERMISSION_REJECT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
                DialogProperties.showPermissionDialog(getResources().getString(R.string.contact_permission), AllConstants.READ_CONTACT_PERMISSION_REJECT,ConversationActivity.this);
            } else {
                Intent in = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(in, RESULT_PICK_CONTACT);
            }

        }
        else if (requestCode == OPEN_MAP) {
            openMap();
        }
        else if (requestCode == SEND_LOCATION) {

            getCurrentLocation();
        }

        else if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
                case MY_RESULT_CODE_FILECHOOSER:
                    Uri uri = data.getData();
                    File myFile = new File(uri.getPath());
                    String pathh = myFile.getAbsolutePath();
                    Uri pathUri = Uri.fromFile(new File(pathh));
                    pickiT.getPath(data.getData(), Build.VERSION.SDK_INT);

                    Path path = null;
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                         path = Paths.get(pathUri.toString());
//                    }


                    String uriString = uri.toString();



                    String displayName = null;

                    if (uriString.startsWith("content://")) {
                        Cursor cursor = null;
                        try {
                            cursor = this.getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                Log.d("nameeeee>>>>  ", displayName);

                                ChatMessage chatMessage = FileUtil.uploadPDF(displayName, uri,ConversationActivity.this,user_id,anthor_user_id);
                                displayMessage(chatMessage);

                            }
                        } finally {
                            cursor.close();
                        }
                    } else if (uriString.startsWith("file://")) {
                        displayName = myFile.getName();
                        Log.d("nameeeee>>>>  ", displayName);
                    }
                    break;
                case PICK_IMAGE_VIDEO:
                    ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                    Uri selectedMediaUri = Uri.parse(returnValue.get(0));
                    if (!FileUtil.isVideoFile(selectedMediaUri.toString())) {
                        String displayNamee = null;

                        Uri pathImage = Uri.fromFile(new File(selectedMediaUri.toString()));
                        File myFileImage = new File(pathImage.toString());


                        FileUtil.copyFileOrDirectory(FileUtil.getPath(this, pathImage), this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send/video").getAbsolutePath());


                        if (pathImage.toString().startsWith("content://")) {
                            Cursor cursor = null;
                            try {
                                cursor = this.getContentResolver().query(selectedMediaUri, null, null, null, null);
                                if (cursor != null && cursor.moveToFirst()) {
                                    displayNamee = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                    Log.d("nameeeee>>>>  ", displayNamee);
//                                    System.out.println(displayNamee);

//                                    uploadImage(displayNamee, selectedMediaUri);
                                   ChatMessage chatMessage =  FileUtil.uploadImage(displayNamee,selectedMediaUri,ConversationActivity.this,user_id,anthor_user_id);
                                   displayMessage(chatMessage);

                                }
                            } finally {
                                cursor.close();
                            }
                        } else if (pathImage.toString().startsWith("file://")) {
                            displayNamee = myFileImage.getName();
//                            uploadImage(displayNamee, pathImage);
                            ChatMessage chatMessage = FileUtil.uploadImage(displayNamee,pathImage,ConversationActivity.this,user_id,anthor_user_id);
                            displayMessage(chatMessage);



                            Log.d("nameeeee>>>>  ", displayNamee);
                        }

                    } else {

                        ////////////////////////////
                        Uri pathhh = Uri.fromFile(new File(selectedMediaUri.toString()));
                        File myFilee = new File(pathhh.toString());


                        FileUtil.copyFileOrDirectory(FileUtil.getPath(this, pathhh), this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send/video").getAbsolutePath());

                        String displayNamee = null;

                        if (pathhh.toString().startsWith("content://")) {
                            Cursor cursor = null;
                            try {
                                cursor = this.getContentResolver().query(selectedMediaUri, null, null, null, null);
                                if (cursor != null && cursor.moveToFirst()) {
                                    displayNamee = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                    Log.d("nameeeee>>>>  ", displayNamee);
                                    System.out.println(displayNamee);

//                                    uploadVideo(displayNamee, selectedMediaUri);
                                    ChatMessage chatMessage = FileUtil.uploadVideo(displayNamee,selectedMediaUri,ConversationActivity.this,user_id,anthor_user_id);
                                    displayMessage(chatMessage);
                                }
                            } finally {
                                cursor.close();
                            }
                        } else if (pathhh.toString().startsWith("file://")) {
                            displayNamee = myFilee.getName();
//                            uploadVideo(displayNamee, pathhh);
                            ChatMessage chatMessage = FileUtil.uploadVideo(displayNamee,pathhh,ConversationActivity.this,user_id,anthor_user_id);
                            displayMessage(chatMessage);



                            Log.d("nameeeee>>>>  ", displayNamee);
                        }

                    }
                    break;

                case PICK_IMAGE_FROM_GALLERY:
                    Uri selectedMediaUriGallery = data.getData();
                    String[] columns = {MediaStore.Images.Media.DATA,
                            MediaStore.Images.Media.MIME_TYPE};

                    Cursor cursor1 = getContentResolver().query(selectedMediaUriGallery, columns, null, null, null);
                    cursor1.moveToFirst();

                    int pathColumnIndex = cursor1.getColumnIndex(columns[0]);
                    int mimeTypeColumnIndex = cursor1.getColumnIndex(columns[1]);

                    String contentPath = cursor1.getString(pathColumnIndex);
                    String mimeType = cursor1.getString(mimeTypeColumnIndex);
                    cursor1.close();
                    if (mimeType.startsWith("image")) {
                        System.out.println("this is Imageeeeeeeeeee" + selectedMediaUriGallery.toString());
                        String displayNamee = null;

//                        Uri pathImage = Uri.fromFile(new File(selectedMediaUriGallery.toString()));
                        File myFileImage = new File(selectedMediaUriGallery.toString());


                        FileUtil.copyFileOrDirectory(FileUtil.getPath(this, selectedMediaUriGallery), this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send/video").getAbsolutePath());


                        if (selectedMediaUriGallery.toString().startsWith("content://")) {
                            Cursor cursor = null;
                            try {
                                cursor = this.getContentResolver().query(selectedMediaUriGallery, null, null, null, null);
                                if (cursor != null && cursor.moveToFirst()) {
                                    displayNamee = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                    Log.d("nameeeee>>>>  ", displayNamee);
//                                    System.out.println(displayNamee);

//                                    uploadImage(displayNamee, selectedMediaUriGallery);
                                   ChatMessage chatMessage =  FileUtil.uploadImage(displayNamee,selectedMediaUriGallery,ConversationActivity.this,user_id,anthor_user_id);
                                    displayMessage(chatMessage);

                                }
                            } finally {
                                cursor.close();
                            }
                        } else if (selectedMediaUriGallery.toString().startsWith("file://")) {
                            displayNamee = myFileImage.getName();
                            System.out.println(displayNamee + "lkkkkkkkkkkkkkkkk");
//                            uploadImage(displayNamee, selectedMediaUriGallery);
                             ChatMessage chatMessage = FileUtil.uploadImage(displayNamee,selectedMediaUriGallery,ConversationActivity.this,user_id,anthor_user_id);
                            displayMessage(chatMessage);


                            Log.d("nameeeee>>>>  ", displayNamee);
                        }


                    } else if (mimeType.startsWith("video")) {

                        //////////////////////////
                        File myFilee = new File(selectedMediaUriGallery.toString());


                        FileUtil.copyFileOrDirectory(FileUtil.getPath(this, selectedMediaUriGallery), this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send/video").getAbsolutePath());

                        String displayNamee = null;

                        if (selectedMediaUriGallery.toString().startsWith("content://")) {
                            Cursor cursor = null;
                            try {
                                cursor = this.getContentResolver().query(selectedMediaUriGallery, null, null, null, null);
                                if (cursor != null && cursor.moveToFirst()) {
                                    displayNamee = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                    Log.d("nameeeee>>>>  ", displayNamee);
                                    System.out.println(displayNamee);

//                                    uploadVideo(displayNamee, selectedMediaUriGallery);
                                    ChatMessage chatMessage = FileUtil.uploadVideo(displayNamee,selectedMediaUriGallery,ConversationActivity.this,user_id,anthor_user_id);
                                    displayMessage(chatMessage);
                                }
                            } finally {
                                cursor.close();
                            }
                        } else if (selectedMediaUriGallery.toString().startsWith("file://")) {
                            displayNamee = myFilee.getName();
                            System.out.println(displayNamee + "lkkkkkkkkkkkkkkkk");
//                            uploadVideo(displayNamee, selectedMediaUriGallery);
                            ChatMessage chatMessage = FileUtil.uploadVideo(displayNamee,selectedMediaUriGallery,ConversationActivity.this,user_id,anthor_user_id);
                            displayMessage(chatMessage);


                            Log.d("nameeeee>>>>  ", displayNamee);
                        }

                    }
                    break;


            }
        } else {
//            Toast.makeText(this, "Failed ", Toast.LENGTH_SHORT).show();
        }
//                    }}}
////////////////////////
    }



    @SuppressLint("Range")
    private void contactPicked(Intent data) {
        //        System.out.println(pdfname+"pdfnameeeeeeeeeeee");
        String message_id = System.currentTimeMillis() + "_" + user_id;
        Cursor cursor = null;
        String name = "";
        String phoneIndex = "";
        try {


            Uri uri = data.getData();
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();

            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phoneIndex = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setId(message_id);//dummy
            chatMessage.setMessage(phoneIndex);
            chatMessage.setFileName(name);

            chatMessage.setDate(String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis()));
            chatMessage.setMe(true);
            chatMessage.setType("contact");
            chatMessage.setState("0");
            messageET.setText("");
            chatMessage.setChecked(false);
            displayMessage(chatMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject sendObject = new JSONObject();

        try {
            sendObject.put("sender_id", user_id);
            sendObject.put("reciver_id", anthor_user_id);
            sendObject.put("message", phoneIndex);
            sendObject.put("message_type", "contact");
            sendObject.put("state", "0");
            sendObject.put("message_id", message_id);
            sendObject.put("chat_id", chat_id);

            sendObject.put("orginalName", name);
            sendObject.put("dateTime", String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        newMeesage(sendObject);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("on destroy");
        chatRoomRepo.setInChat(anthor_user_id, false);

        LocalBroadcastManager.getInstance(this).unregisterReceiver(check);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveTyping);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveNwMessage);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveDeleteMessage);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveUpdateMessage);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveBlockUser);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveUnBlockUser);

//        LocalBroadcastManager.getInstance(this).unregisterReceiver(recivecallRequest);
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(recivePeerId);


    }
    @Override
    public void onBackPressed() {
        if(relativeMaps.getVisibility()==View.VISIBLE){
         container.setVisibility(View.VISIBLE);
          relativeMaps.setVisibility(View.GONE);
        }

        else if(conversationModelView.getSelectedMessage().getValue()!=null) {
            if (conversationModelView.getSelectedMessage().getValue().size() > 0){
                conversationModelView.clearSelectedMessage();
            }
              else if(viewVisability){
                hideLayout();
            }
             else {
                  finish();
            }
        }

       else if(viewVisability){
            hideLayout();
        }
        else {
            finish();
        }
    }

    //// on click in message
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onHandleSelection(int position, ChatMessage chatMessage, boolean myMessage) {
        System.out.println(chatMessage.message + "onHandleSelection");
        File pdfFile;
        Log.v(TAG, "view() Method invoked ");

        if (!hasPermissions(ConversationActivity.this, PERMISSIONS)) {

            Log.v(TAG, "download() Method DON'T HAVE PERMISSIONS ");

//            Toast t = Toast.makeText(getApplicationContext(), "You don't have read access !", Toast.LENGTH_LONG);
//            t.show();

        } else {
            if (myMessage) {
                File d = this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo" + File.separator + "send");  // -> filename = maven.pdf
                pdfFile = new File(d, chatMessage.getFileName());
            } else {
                File d = this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo" + File.separator + "recive");  // -> filename = maven.pdf
                pdfFile = new File(d, chatMessage.getMessage());
            }

            Log.v(TAG, "view() Method pdfFile " + pdfFile.getAbsolutePath());

            Uri path = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", pdfFile);

            System.out.println(pdfFile.exists() + "pathhhhhhhhhhhhh");

            if (pdfFile.exists()) {
                Log.v(TAG, "view() Method path " + path);

                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                pdfIntent.setDataAndType(path, "application/pdf");
                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                try {
                    startActivity(pdfIntent);
                } catch (ActivityNotFoundException e) {
//                    Toast.makeText(ConversationActivity.this, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
                }
            } else {
//                Toast.makeText(ConversationActivity.this, getResources().getString(R.string.please_download), Toast.LENGTH_SHORT).show();

            }
        }

        Log.v(TAG, "view() Method completed ");


    }

    ///// on click on download
    @Override
    public void downloadFile(int position, ChatMessage chatMessage, boolean myMessage) {
        File pdfFile;
        System.out.println(chatMessage.message + "onDownload");

        Log.v(TAG, "download() Method invoked ");

        if (!hasPermissions(ConversationActivity.this, PERMISSIONS)) {

            Log.v(TAG, "download() Method DON'T HAVE PERMISSIONS ");

//            Toast t = Toast.makeText(getApplicationContext(), "You don't have write access !", Toast.LENGTH_LONG);
//            t.show();

        } else {

            Log.v(TAG, "download() Method HAVE PERMISSIONS ");
            if (myMessage) {

                File d = this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send");

                pdfFile = new File(d, chatMessage.getFileName());
                if (!pdfFile.exists()) {

//            new DownloadFile().execute("http://maven.apache.org/maven-1.x/maven.pdf", "maven.pdf");
//                    new DownloadFile().execute(AllConstants.download_url + "files/" + chatMessage.getMessage().toString(), chatMessage.getFileName(), "send");
//                    new DownloadFile().execute(AllConstants.download_url + chatMessage.getMessage(), chatMessage.getFileName(), "send");
                    download(chatMessage,d,AllConstants.download_url + chatMessage.getMessage(),chatMessage.getFileName());


                } else {
                    Log.v(TAG, "File already download ");

                }
            } else {

                File d = this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive");

                pdfFile = new File(d, chatMessage.getMessage());
                if (!pdfFile.exists()) {

//            new DownloadFile().execute("http://maven.apache.org/maven-1.x/maven.pdf", "maven.pdf");
//                    new DownloadFile().execute(AllConstants.download_url + "files/" + chatMessage.getMessage().toString(), chatMessage.getMessage().toString(), "recive");
//                    new DownloadFile().execute(AllConstants.download_url + chatMessage.getMessage(), chatMessage.getMessage(), "recive");
                    download(chatMessage,d,AllConstants.download_url + chatMessage.getMessage(),chatMessage.getMessage());

//                 DownloadRequest downloadID = PRDownloader.download(AllConstants.download_url + chatMessage.getMessage(), pdfFile.getPath(), "recive")
//                            .build();
                } else {
                    Log.v(TAG, "File already download ");

                }
            }

        }

        Log.v(TAG, "download() Method completed ");

    }

    @Override
    public void downloadVoice(int position, ChatMessage chatMessage, boolean myMessage) {
        File audioFile;
        System.out.println(chatMessage.message + "onDownload");

        Log.v(TAG, "download() Method invoked ");

        if (!hasPermissions(ConversationActivity.this, PERMISSIONS)) {

            Log.v(TAG, "download() Method DON'T HAVE PERMISSIONS ");

//            Toast t = Toast.makeText(getApplicationContext(), "You don't have write access !", Toast.LENGTH_LONG);
//            t.show();

        } else {

            Log.v(TAG, "download() Method HAVE PERMISSIONS ");
            if (myMessage) {
                File d = this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send/voiceRecord");

                audioFile = new File(d, chatMessage.getFileName());
                if (!audioFile.exists()) {
                    System.out.println(chatMessage.message + "kkkkkkkkkk");


//                    new DownloadFile().execute(AllConstants.download_url + chatMessage.getMessage(), chatMessage.getFileName(), "send/voiceRecord");
                    download(chatMessage,d,AllConstants.download_url + chatMessage.getMessage(),chatMessage.getFileName());



                } else {
                    MediaPlayer mediaPlayer = MediaPlayer.create(this, Uri.parse(audioFile.getAbsolutePath()));
                    mediaPlayer.start();
                    Log.v(TAG, "File already download ");
                    System.out.println("File already download ");

                }
            } else {

                File d = this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive/voiceRecord");

                audioFile = new File(d, chatMessage.getMessage());
                if (!audioFile.exists()) {

//            new DownloadFile().execute("http://maven.apache.org/maven-1.x/maven.pdf", "maven.pdf");
//                    new DownloadFile().execute(AllConstants.download_url + "audio/" + chatMessage.getMessage().toString(), chatMessage.getMessage().toString(), "recive/voiceRecord");
//                    new DownloadFile().execute(AllConstants.download_url + chatMessage.getMessage(), chatMessage.getMessage(), "recive/voiceRecord");
                    download(chatMessage,d,AllConstants.download_url + chatMessage.getMessage(),chatMessage.getMessage());


                } else {
                    Log.v(TAG, "File already download ");

                }
            }

        }

        Log.v(TAG, "download() Method completed ");


    }

    @Override
    public void downloadVideo(int position, ChatMessage chatMessage, boolean myMessage) {
        File videoFile;
        System.out.println(chatMessage.message + "onDownload");

        Log.v(TAG, "download() Method invoked ");

        if (!hasPermissions(ConversationActivity.this, PERMISSIONS)) {

            Log.v(TAG, "download() Method DON'T HAVE PERMISSIONS ");

//            Toast t = Toast.makeText(getApplicationContext(), "You don't have write access !", Toast.LENGTH_LONG);
//            t.show();

        } else {

            Log.v(TAG, "download() Method HAVE PERMISSIONS ");
            if (myMessage) {
                File d = this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send/video");


                videoFile = new File(d, chatMessage.getFileName());
                if (!videoFile.exists()) {

//                    new DownloadFile().execute(AllConstants.download_url + "video/" + chatMessage.getMessage().toString(), chatMessage.getFileName(), "send/video");
//                    new DownloadFile().execute(AllConstants.download_url + chatMessage.getMessage(), chatMessage.getFileName(), "send/video");
                    download(chatMessage,d,AllConstants.download_url + chatMessage.getMessage(),chatMessage.getFileName());

                } else {
                    Log.v(TAG, "File already download ");

                }
            } else {

                File d = this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive/video");

                videoFile = new File(d, chatMessage.getMessage());
                if (!videoFile.exists()) {

//            new DownloadFile().execute("http://maven.apache.org/maven-1.x/maven.pdf", "maven.pdf");
//                    new DownloadFile().execute(AllConstants.download_url + "video/" + chatMessage.getMessage().toString(), chatMessage.getMessage().toString(), "recive/video");
//                    new DownloadFile().execute(AllConstants.download_url + chatMessage.getMessage(), chatMessage.getMessage(), "recive/video");
                    download(chatMessage,d,AllConstants.download_url + chatMessage.getMessage(),chatMessage.getMessage());


                } else {
                    Log.v(TAG, "File already download ");

                }
            }

        }

        Log.v(TAG, "download() Method completed ");


    }

    @Override
    public void downloadImage(int position, ChatMessage chatMessage, boolean myMessage) {
        File imageFile;
        System.out.println(chatMessage.message + "onDownload");

        Log.v(TAG, "download() Method invoked ");

        if (!hasPermissions(ConversationActivity.this, PERMISSIONS)) {

            Log.v(TAG, "download() Method DON'T HAVE PERMISSIONS ");

//            Toast t = Toast.makeText(getApplicationContext(), "You don't have write access !", Toast.LENGTH_LONG);
//            t.show();

        } else {

            Log.v(TAG, "download() Method HAVE PERMISSIONS ");
            if (myMessage) {
                File d = this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send/video");

                imageFile = new File(d, chatMessage.getFileName());
                if (!imageFile.exists()) {
                    download(chatMessage,d,AllConstants.imageUrlInConversation + chatMessage.getImage(),chatMessage.getFileName());

//                    new DownloadFile().execute(AllConstants.imageUrlInConversation + chatMessage.getImage(), chatMessage.getFileName(), "send/video");
//                    DownloadRequest downloadID = PRDownloader.download(AllConstants.imageUrlInConversation + chatMessage.getImage(), d.getPath(),  chatMessage.getFileName())
//                            .build().setOnStartOrResumeListener(new OnStartOrResumeListener() {
//                                @SuppressLint("SetTextI18n")
//                                @Override
//                                public void onStartOrResume() {
//                                    System.out.println("startttttttttt download");
//                                    conversationModelView.setMessageDownload(chatMessage.getId(),true);
//
//                                    Toast.makeText(ConversationActivity.this, "Downloading started", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//
//                      int id =  downloadID.start(new OnDownloadListener() {
//                        @Override
//                        public void onDownloadComplete() {
//                            System.out.println("completed");
//                            conversationModelView.setMessageDownload(chatMessage.getId(),false);
//
//
//                        }
//
//                        @Override
//                        public void onError(Error error) {
//                            conversationModelView.setMessageDownload(chatMessage.getId(),false);
//
//                            System.out.println("errror");
//                        }
//                    });
//                    System.out.println("downloadID"+downloadID.getDownloadId()+""+id);

                } else {
                    Log.v(TAG, "File already download ");

                }
            } else {

                File d = this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive/video");

                imageFile = new File(d, chatMessage.getImage());
                if (!imageFile.exists()) {

//            new DownloadFile().execute("http://maven.apache.org/maven-1.x/maven.pdf", "maven.pdf");
//                    new DownloadFile().execute(AllConstants.imageUrlInConversation + chatMessage.getImage(), chatMessage.getImage(), "recive/video");
                    download(chatMessage,d,AllConstants.imageUrlInConversation + chatMessage.getImage(),chatMessage.getImage());
                } else {
                    Log.v(TAG, "File already download ");

                }
            }

        }

        Log.v(TAG, "download() Method completed ");

    }

    @Override
    public void onClickLocation(int position, ChatMessage chatMessage, boolean myMessage) {
        if (myMessage) {
            sendLocation.setVisibility(View.VISIBLE);
        } else {
            sendLocation.setVisibility(View.GONE);
        }

        String[] latlong = chatMessage.getMessage().split(",");
        System.out.println(chatMessage.getMessage() + " chatMessage.getMessage().toString()");
//        5.967120
        double latitude = Double.parseDouble(latlong[0]);
        double longitude = Double.parseDouble(latlong[1]);
//        client = LocationServices.getFusedLocationProviderClient(this);
        locationLatLng = new LatLng(latitude, longitude);
        container.setVisibility(View.GONE);
        relativeMaps.setVisibility(View.VISIBLE);


        if (ActivityCompat.checkSelfPermission(ConversationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(ConversationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ConversationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, AllConstants.OPEN_MAP_PERMISSION);
        } else {
            openMap();
        }
//        Task<Location> taskd = client.getLastLocation();
//        taskd.addOnSuccessListener(new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                System.out.println("when successs");
//                //When Success
//                if (location != null){
//                    //Sync Map
//                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
//                        @Override
//                        public void onMapReady(GoogleMap googleMap) {
//
////                                   latLng = new LatLng("lat from response API" + lat ,"longg from response API" + longg);
//
//
//                            // Create Marker Option                                                             //  We need a small icon to represent our company
//                            MarkerOptions options = new MarkerOptions().position(locationLatLng).title("He is there"); // .icon(BitmapDescriptorFactory.fromResource(R.drawable.logolocation))
//
//                            //Zoom Map
//                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng,15));
//
//                            //Add Marker On Map
//                            googleMap.addMarker(options);
//
//                        }
//                    });
//
//                }
//
//            }
//        });

    }

    void openMap() {
        client = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Task<Location> taskd = client.getLastLocation();
            taskd.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    //When Success
                    if (location != null) {
                        //Sync Map
                        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {

                                // Create Marker Option                                                             //  We need a small icon to represent our company
                                MarkerOptions options = new MarkerOptions().position(locationLatLng).title("He is there"); // .icon(BitmapDescriptorFactory.fromResource(R.drawable.logolocation))

                                //Zoom Map
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 15));

                                //Add Marker On Map
                                googleMap.addMarker(options);

                            }
                        });

                    }

                }
            });
        }
        else {
            showGPSDisabledAlertToUser(OPEN_MAP);
        }
    }


    @SuppressLint("ResourceType")
    @Override
    public void onLongClick(int position, ChatMessage chatMessage, boolean isChecked) {
        System.out.println(isChecked);
        personInformationLiner.setVisibility(View.GONE);
        toolbar.setBackgroundColor(getResources().getColor(R.color.memo_background_color));

        conversationModelView.setMessageChecked(chatMessage.getId(),isChecked);

        toolsLiner.setVisibility(View.VISIBLE);

        if (isChecked) {
            conversationModelView.addSelectedMessage(chatMessage);
            deleteMessage.add("\"" + chatMessage.getId() + "\"");

        } else {
            conversationModelView.removeSelectedMessage(chatMessage);
            deleteMessage.remove("\"" + chatMessage.getId() + "\"");


//            if (conversationModelView.selectedMessage.getValue().size() < 1) {
//                System.out.println("selectedMessage.size() < 1");
//                personInformationLiner.setVisibility(View.VISIBLE);
//                toolbar.setBackgroundColor(getResources().getColor(R.color.memo_background_color));
//                toolsLiner.setVisibility(View.GONE);
//            }


        }

    }

    @Override
    public void playVideo(Uri path) {
        Bundle bundle = new Bundle();
        bundle.putString("path", path.toString());
        Intent intent = new Intent(ConversationActivity.this, VideoActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }




    @Override
    public void PickiTonUriReturned() {

    }

    @Override
    public void PickiTonStartListener() {

    }

    @Override
    public void PickiTonProgressUpdate(int progress) {

    }

    @Override
    public void PickiTonCompleteListener(String path, boolean wasDriveFile, boolean wasUnknownProvider, boolean wasSuccessful, String Reason) {
        FileUtil.copyFileOrDirectory(path, this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send").getAbsolutePath());

    }

    @Override
    public void PickiTonMultipleCompleteListener(ArrayList<String> paths, boolean wasSuccessful, String Reason) {
    }


    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            Log.v(TAG, "doInBackground() Method invoked ");

            String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
            String fileName = strings[1];
            String folderName = strings[2];
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            /// File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File folder = ConversationActivity.this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/" + folderName);

            File pdfFile = new File(folder, fileName);
            Log.v(TAG, "doInBackground() pdfFile invoked " + pdfFile.getAbsolutePath());
            Log.v(TAG, "doInBackground() pdfFile invoked " + pdfFile.getAbsoluteFile());

            try {
                pdfFile.createNewFile();
                Log.v(TAG, "doInBackground() file created" + pdfFile);

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "doInBackground() error" + e.getMessage());
                Log.e(TAG, "doInBackground() error" + e.getStackTrace());


            }
            FileDownloader.downloadFile(fileUrl, pdfFile);
            Log.v(TAG, "doInBackground() file download completed");

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();

        }
    }




    private void showUpdateMessageDialog(ChatMessage chatMessage) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_message_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = dialogView.findViewById(R.id.edit1);
        edt.setText(chatMessage.getMessage());

        dialogBuilder.setTitle(R.string.update_message_note);
        dialogBuilder.setPositiveButton(R.string.update_message, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (!chatMessage.getMessage().equals(edt.getText().toString())) {
                    JSONObject data = new JSONObject();
                    try {
                        data.put("message_id", chatMessage.getId());
                        data.put("message", edt.getText().toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    updateMessage(data);
                    conversationModelView.setMessageChecked(conversationModelView.getSelectedMessage().getValue().get(0).getId(),false);

                    conversationModelView.clearSelectedMessage();

                    deleteMessage.clear();
                }
            }
        });
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void makeMapsAction() {

        if (ActivityCompat.checkSelfPermission(ConversationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();


        } else {
            // When Permission Denied
            // Request Permission
            ActivityCompat.requestPermissions(ConversationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, AllConstants.LOCATION_PERMISSION);
        }

    }

    private void getCurrentLocation() {
        //Initialize Task Location

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Task<Location> task = client.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    //When Success
                    System.out.println(location+"Location");
                    if (location != null) {
                        //Sync Map
                        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                //Initialize Lat And Long

                                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                Log.d("latLng", String.valueOf(latLng));
//                            System.out.println(String.valueOf(latLng) + "latln");
                                stringLatLng = location.getLatitude() + "," + location.getLongitude();

                                // Create Marker Option                                                            //  We need a small icon to represent our company
                                MarkerOptions options = new MarkerOptions().position(latLng).title("I am There"); // .icon(BitmapDescriptorFactory.fromResource(R.drawable.logolocation))

                                //Zoom Map
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                                //Add Marker On Map
                                googleMap.addMarker(options);

                            }
                        });

                    }

                }
            });
        }else{
            showGPSDisabledAlertToUser(SEND_LOCATION);
        }




    }

    private void alertDeleteDialog() {

        for (ChatMessage message : conversationModelView.getSelectedMessage().getValue()) {
            if (!message.isMe()) {
                isAllMessgeMe = false;
                break;
            } else {
                isAllMessgeMe = true;

            }

        }
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.alert_delete_message);
        dialog.setPositiveButton(R.string.delete_for_me,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        conversationModelView.deleteMessageForMe(deleteMessage,user_id);

                        deleteMessage.clear();

                    }
                });
        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        if (isAllMessgeMe) {
            dialog.setNeutralButton(R.string.delete_for_all, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    JSONObject jsonObject = new JSONObject();
//                for (ChatMessage message : selectedMessage) {
//                    adapter.chatMessages.remove(message);
//                }

                    System.out.println(deleteMessage.size() + "sizeeeeeeeeeeeeee");
                    System.out.println(deleteMessage.toString() + "stringgggggggg");


                    try {
                        jsonObject.put("message_to_delete", deleteMessage.toString());
                        jsonObject.put("anthor_user_id", anthor_user_id);
                        jsonObject.put("my_id", user_id);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    deletForAll(jsonObject);

                    conversationModelView.clearSelectedMessage();
                    deleteMessage.clear();



                }
            });
        }
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

//    private class DownloadFileFromSocket extends AsyncTask<String, Void, Void> {
//        ChatMessage chatMessage;
//
//        public DownloadFileFromSocket(ChatMessage chatMessage) {
//            this.chatMessage = chatMessage;
//        }
//
//        @Override



    private void checkContactpermission() {

        if (permissions.isContactOk(this)) {
            Intent in = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(in, RESULT_PICK_CONTACT);
        } else {
            permissions.requestContact(this);
        }
    }

    private void askPermissions() {
        ActivityCompat.requestPermissions(this, callPermissions, requestcode);
    }

    private Boolean isPermissionGranted() {

        for (String permission : callPermissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }

        return true;
    }
   @RequiresApi(api = Build.VERSION_CODES.M)
   public void closeCurrentNotification(){

       NotificationManager mNotificationManager = (NotificationManager)
                       getSystemService(Context. NOTIFICATION_SERVICE ) ;
       mNotificationManager.cancel(Integer.parseInt(anthor_user_id));


   }

    private void showGPSDisabledAlertToUser(int requestcode){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.gps_message))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.enable_gps),
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
//                                container.setVisibility(View.VISIBLE);
//                                relativeMaps.setVisibility(View.GONE);
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                Intent in = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                                startActivityForResult(callGPSSettingIntent,requestcode);
                            }
                        });
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
    void download(ChatMessage chatMessage, File d, String downloadUrl, String fileName){
        conversationModelView.setMessageDownload(chatMessage.getId(),true);

        DownloadRequest downloadID = PRDownloader.download(downloadUrl, d.getPath(), fileName)
                .build().setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onStartOrResume() {
                        System.out.println("startttttttttt download");
//                        conversationModelView.setMessageDownload(chatMessage.getId(),true);

//                        Toast.makeText(ConversationActivity.this, "Downloading started", Toast.LENGTH_SHORT).show();
                    }
                });

        int id =  downloadID.start(new OnDownloadListener() {
            @Override
            public void onDownloadComplete() {
                System.out.println("completed");
                conversationModelView.setMessageDownload(chatMessage.getId(),false);


            }

            @Override
            public void onError(Error error) {


                conversationModelView.setMessageDownload(chatMessage.getId(),false);

                System.out.println("errror");
            }
        });
        System.out.println("downloadID"+downloadID.getDownloadId()+""+id);
    }
    public Void processSocketFile(ChatMessage chatMessage) {
//         chatMessage.setDownload(true);
         displayMessage(chatMessage);

        if (!hasPermissions(ConversationActivity.this, PERMISSIONS)) {

            Log.v(TAG, "download() Method DON'T HAVE PERMISSIONS ");

//            Toast t = Toast.makeText(getApplicationContext(), "You don't have write access !", Toast.LENGTH_LONG);
//            t.show();

        } else {
            switch (chatMessage.getType()) {
                case "imageWeb":
                    File imageFile;
                    File d = ConversationActivity.this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive/video");

                    imageFile = new File(d, chatMessage.getImage());
                    if (!imageFile.exists()) {

//                            FileDownloader.downloadFile(AllConstants.imageUrlInConversation + chatMessage.getImage(), imageFile);
                        downloadSocket(chatMessage,d,AllConstants.imageUrlInConversation + chatMessage.getImage(), chatMessage.getImage());
                        Log.v(TAG, "doInBackground() file download completed");


                    } else {
                        Log.v(TAG, "File already download ");

                    }
                    //            try {


                    break;
                case "voice":
                    File voiceFile;
                    System.out.println("image weeeeeeeeeeeeeeeeeeeeeeeb");
                    File dV = ConversationActivity.this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive/voiceRecord");

                    voiceFile = new File(dV, chatMessage.getMessage());
                    if (!voiceFile.exists()) {

//                            FileDownloader.downloadFile(AllConstants.download_url+"audio/"+chatMessage.getMessage().toString(), voiceFile);
//                            FileDownloader.downloadFile(AllConstants.download_url + chatMessage.getMessage(), voiceFile);
                        downloadSocket(chatMessage,dV,AllConstants.download_url + chatMessage.getMessage(), chatMessage.getMessage());


                        Log.v(TAG, "doInBackground() file download completed");


                    } else {
                        Log.v(TAG, "File already download ");

                    }

                    break;
                case "video":
                    File videoFile;
                    System.out.println("image weeeeeeeeeeeeeeeeeeeeeeeb");
                    File dVideo = ConversationActivity.this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive/video");

                    videoFile = new File(dVideo, chatMessage.getMessage());
                    if (!videoFile.exists()) {

//                            FileDownloader.downloadFile(AllConstants.download_url+"video/"+chatMessage.getMessage().toString(), videoFile);
//                            FileDownloader.downloadFile(AllConstants.download_url + chatMessage.getMessage(), videoFile);
                        downloadSocket(chatMessage,dVideo,AllConstants.download_url + chatMessage.getMessage(), chatMessage.getMessage());


                        Log.v(TAG, "doInBackground() file download completed");


                    } else {
                        Log.v(TAG, "File already download ");

                    }

                    break;
                case "file":
                    File pdfFile;


                    if (!hasPermissions(ConversationActivity.this, PERMISSIONS)) {


//                        Toast t = Toast.makeText(getApplicationContext(), "You don't have write access !", Toast.LENGTH_LONG);
//                        t.show();

                    } else {

                        Log.v(TAG, "download() Method HAVE PERMISSIONS ");


                        File dFile = ConversationActivity.this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive");

                        pdfFile = new File(dFile, chatMessage.getMessage());
                        if (!pdfFile.exists()) {

//                                    FileDownloader.downloadFile(AllConstants.download_url+"files/"+chatMessage.getMessage().toString(), pdfFile);
//                                FileDownloader.downloadFile(AllConstants.download_url + chatMessage.getMessage(), pdfFile);
                            downloadSocket(chatMessage,dFile,AllConstants.download_url + chatMessage.getMessage(), chatMessage.getMessage());


                            Log.v(TAG, "doInBackground() file download completed");

//            new DownloadFile().execute("http://maven.apache.org/maven-1.x/maven.pdf", "maven.pdf");
//                                    new DownloadFile().execute(AllConstants.download_url + "files/" + chatMessage.getMessage().toString(), chatMessage.getMessage().toString(), "recive");
                        } else {
                            Log.v(TAG, "File already download ");

                        }
                    }


                    break;

                default:
//                    Toast t = Toast.makeText(getApplicationContext(), "don't support", Toast.LENGTH_LONG);
//                    t.show();
            }


        }


        Log.v(TAG, "download() Method completed ");


        return null;
    }


    void downloadSocket(ChatMessage chatMessage, File d, String downloadUrl, String fileName){
        conversationModelView.setMessageDownload(chatMessage.getId(),true);
        DownloadRequest downloadID = PRDownloader.download(downloadUrl, d.getPath(), fileName)
                .build().setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onStartOrResume() {
                        System.out.println("startttttttttt download");



//                        Toast.makeText(ConversationActivity.this, "Downloading started", Toast.LENGTH_SHORT).show();
                    }
                });

        int id =  downloadID.start(new OnDownloadListener() {
            @Override
            public void onDownloadComplete() {
                System.out.println("completed");
                conversationModelView.setMessageDownload(chatMessage.getId(),false);
//                displayMessage(chatMessage);


            }

            @Override
            public void onError(Error error) {
                conversationModelView.setMessageDownload(chatMessage.getId(),false);


            }
        });
    }

}











