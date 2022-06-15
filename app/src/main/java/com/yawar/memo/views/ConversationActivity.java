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
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
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
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.bumptech.glide.Glide;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordPermissionHandler;
import com.devlomi.record_view.RecordView;

import com.example.videocallapp.JavascriptInterface;
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
import com.yawar.memo.Api.ClassSharedPreferences;
import com.yawar.memo.Api.ServerApi;
import com.yawar.memo.BuildConfig;
import com.yawar.memo.R;
import com.yawar.memo.adapter.ChatAdapter;
import com.yawar.memo.call.CallMainActivity;
import com.yawar.memo.call.RequestCallActivity;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.fragment.ForwardDialogFragment;
import com.yawar.memo.model.ChatMessage;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.modelView.ArchivedActViewModel;
import com.yawar.memo.modelView.ConversationModelView;
import com.yawar.memo.repositry.BlockUserRepo;
import com.yawar.memo.repositry.ChatMessageRepo;
import com.yawar.memo.repositry.ChatRoomRepo;
import com.yawar.memo.service.SocketIOService;
import com.yawar.memo.utils.BaseApp;
import com.yawar.memo.utils.FileDownloader;
import com.yawar.memo.utils.FileUtil;
import com.yawar.memo.utils.TimeProperties;
import com.yawar.memo.utils.VolleyMultipartRequest;
import com.yawar.memo.permissions.Permissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.Socket;


public class ConversationActivity extends AppCompatActivity implements ChatAdapter.CallbackInterface, Observer, PickiTCallbacks {

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

    WebView webView;
    private int requestcode = 1;
    String peerId = null;


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
    //    private String senderId;
//    private String reciverId;
    private String userName;
    private String imageUrl;
    Bitmap bitmap;
    String imageString;
    MediaController mediaControls;
    Toolbar toolbar;


    String audioPath, audioName;
    String lastSeen = "";
    private static final int REQUEST_PERMISSIONS = 100;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_IMAGE_VIDEO = 1111;
    private String filePath;

    private static final int PICK_IMAGE = 100;
    private static final int MY_REQUEST_CODE_PERMISSION = 1000;
    private static final int MY_RESULT_CODE_FILECHOOSER = 2200;

    ArrayList<String> returnValue = new ArrayList<>();
    private boolean isCoonect;
    private ArrayList<ChatMessage> chatHistory;
    private ArrayList<ChatMessage> selectedMessage = new ArrayList<>();
    private ArrayList<JSONObject> unSendMessage = new ArrayList<>();
    private ArrayList<String> deleteMessage = new ArrayList<>();


    SearchView searchView;
    private Boolean hasConnection = false;
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
//    public static final String ON_CALL_REQUEST = "ConversationActivity.ON_CALL_REQUEST";
//    public static final String FETCH_PEER_ID = "ConversationActivity.FETCH_PEER_ID";


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
    private BroadcastReceiver check = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String check = intent.getExtras().getString("check");
            JSONObject checkObject = null;
            String checkConnect = "false";

            try {
                checkObject = new JSONObject(check);
                checkConnect = checkObject.getString("is_connect");
                lastSeen = checkObject.getString("last_seen");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            System.out.println(check + "usernnnnnnnnnnnnnnnnnnnnnnn");
            if (checkConnect.equals("true")) {
                isCoonect = true;
                tv_state.setText(R.string.connect_now);
                tv_state.setVisibility(View.VISIBLE);
            } else if (checkConnect.equals("false")) {
                isCoonect = false;
//                try {
                if (!lastSeen.equals("null")) {
                    tv_state.setText(getResources().getString(R.string.last_seen) + " " + timeProperties.getDateForLastSeen(context, Long.parseLong(lastSeen)));
                    tv_state.setVisibility(View.VISIBLE);
                }

//                }catch (Exception e){
//                    System.out.println("nulll");
//                }
//                tv_state.setText(getResources().getString(R.string.last_seen)+" "+timeProperties.getFormattedDate(context,Long.parseLong(lastSeen)));
//                tv_state.setVisibility(View.VISIBLE);

//                tv_state.setVisibility(View.GONE);
            }
        }
    };

    private BroadcastReceiver reciveTyping = new BroadcastReceiver() {
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

                        if (isTyping.equals("true")) {
                            tv_state.setText(R.string.writing_now);
                            tv_state.setVisibility(View.VISIBLE);
                        } else if (isCoonect) {
                            tv_state.setText(R.string.connect_now);
                        } else {
                            tv_state.setText(getResources().getString(R.string.last_seen) + " " + timeProperties.getDateForLastSeen(context, Long.parseLong(lastSeen)));


//                        tv_state.setVisibility(View.GONE);
                        }

                    }
                }
            });
        }
    };


    private BroadcastReceiver reciveDeleteMessage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String deleteString = intent.getExtras().getString("delete message");
                    JSONObject message = null;

                    try {
                        JSONObject jsonObject = new JSONObject(deleteString);
                        String deleteMessage = jsonObject.getString("message_to_delete");
                        JSONArray jsonArray = new JSONArray(deleteMessage);
                        String deleteChat_id = jsonObject.getString("chat_id");
                        String first_user_id = jsonObject.getString("first_id");
                        String second_user_id = jsonObject.getString("second_id");


                        if (anthor_user_id.equals(first_user_id) || anthor_user_id.equals(second_user_id)) {
                            for (int i = 0; i < jsonArray.length(); i++) {

                                String message_id = jsonArray.getString(i);
                                for (ChatMessage chatMessage : adapter.chatMessages) {
                                    if (chatMessage.getId().equals(message_id)) {
                                        System.out.println(chatMessage.getId() + " " + message_id);
                                        adapter.chatMessages.remove(chatMessage);
                                        break;
                                    }
                                }


                            }
                            adapter.notifyDataSetChanged();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });
        }
    };
    private BroadcastReceiver reciveUpdateMessage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String deleteString = intent.getExtras().getString("updateMessage");
                    JSONObject message = null;
                    System.out.println(deleteString.toString() + "deleteRespone");

                    try {
                        JSONObject jsonObject = new JSONObject(deleteString);
                        String message_id = jsonObject.getString("message_id");
                        String updateMessage = jsonObject.getString("message");


                        String first_user_id = jsonObject.getString("reciver_id");
                        String second_user_id = jsonObject.getString("sender_id");


                        if (anthor_user_id.equals(first_user_id) || anthor_user_id.equals(second_user_id)) {

                            for (ChatMessage chatMessage : adapter.chatMessages) {
                                System.out.println(chatMessage.getId() + "majdfadi" + message_id);
                                if (chatMessage.getId().equals(message_id)) {
                                    System.out.println(chatMessage.getId() + "mmk " + message_id);
                                    chatMessage.setMessage(updateMessage);
                                    chatMessage.setIsUpdate("1");
                                    break;
                                }
                            }


                            adapter.notifyDataSetChanged();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });
        }
    };
    private BroadcastReceiver reciveNwMessage = new BroadcastReceiver() {
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
//                    if(chat_id.equals("-1")){
//
//                    }

                    if (senderId.equals(user_id) && reciverId.equals(anthor_user_id)) {
                        if (!state.equals("3") && chat_id.equals(user_id + anthor_user_id)) {
//                            myBase.getObserver().setLastMessage(text, recive_chat_id, user_id, anthor_user_id, type, state, MessageDate);
                            if (!recive_chat_id.isEmpty()) {
                                chatRoomRepo.setLastMessage(text, recive_chat_id, user_id, anthor_user_id, type, state, MessageDate);

                                chat_id = recive_chat_id;
                            }
                        }

//                        if(chat_id.equals(user_id+anthor_user_id)&&!state.equals("3")){
//                            System.out.println("afterrrrrrrrrrrrrrrrObserrrrrrrrrrrrrrrrrrv");
//                            myBase.getObserver().setLastMessage(text,recive_chat_id,myId,anthor_id,type,state,dateTime);
//
//
//
//                        }//                        Toast.makeText(ConversationActivity.this,args[0].toString(),Toast.LENGTH_LONG).show();


                        for (int i = adapter.chatMessages.size() - 1; i >= 0; i--) {
                            if (state.equals("3")) {
                                System.out.println("state.equals(\"3\")==========================");

                                if (adapter.chatMessages.get(i).getState().equals("3")) {
                                    System.out.println(i + "===============");

                                    break;
                                }
                                adapter.chatMessages.get(i).setState(state);

//                                }

//                            } else if (state.equals("2")) {
//                                if (adapter.chatMessages.get(i).getState().equals("1") || adapter.chatMessages.get(i).getState().equals("0")) {
//                                    adapter.chatMessages.get(i).setState(state);
//                                    adapter.chatMessages.get(i).setId(id);
//                                    System.out.println(adapter.chatMessages.get(i).message);
//                                }
//                                else{
//                                    break;
//                                }
                            } else if (state.equals("2")) {
                                System.out.println("state.equals(\"2\")==========================");

                                if (adapter.chatMessages.get(i).getId().equals(id)) {
                                    adapter.chatMessages.get(i).setState(state);
                                    break;
//                                    adapter.chatMessages.get(i).setId(id);
//                                    System.out.println(adapter.chatMessages.get(i).message);
                                }
                            } else if (state.equals("1")) {
                                /// System.out.println(adapter.chatMessages.get(i).getId()+"xxxx"+id);


                                if (adapter.chatMessages.get(i).getId().equals(id)) {
                                    System.out.println("majdddddddddddd" + unSendMessage.size());
                                    adapter.chatMessages.get(i).setState(state);
//
//                                        adapter.chatMessages.get(adapter.chatMessages.size()-1).setState(state);
//                                        adapter.chatMessages.get(adapter.chatMessages.size()-1).setId(id);
//                                        for (JSONObject  object:
//                                                unSendMessage) {
                                    for (i = 0; i < unSendMessage.size(); i++) {
                                        try {
                                            if (unSendMessage.get(i).getString("message_id").equals(id))

                                                unSendMessage.remove(unSendMessage.get(i));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                    classSharedPreferences.setList("list", unSendMessage);
                                    break;
                                }


                            } else break;
//                                if (state.equals("1")) {
//                                unSendMessage.remove(0);
//                                classSharedPreferences.setList("list",unSendMessage);
//
//                                if (adapter.chatMessages.get(i).getState().equals("0")) {
//                                    System.out.println("majdddddddddddd");
//                                    adapter.chatMessages.get(i).setState(state);
//                                    adapter.chatMessages.get(i).setId(id);
////                                    unSendMessage.remove(1);
////                                    classSharedPreferences.setList("list",unSendMessage);
//                                }
//                                else {
//                                    break;
//                                }
//                            }


                        }
//                        if (state.equals("1")) {
//
//
//                                if (adapter.chatMessages.get(adapter.chatMessages.size()-1).getState().equals("0")) {
//                                    System.out.println("majdddddddddddd");
//                                    adapter.chatMessages.get(adapter.chatMessages.size()-1).setState(state);
//                                    adapter.chatMessages.get(adapter.chatMessages.size()-1).setId(id);
////                                    unSendMessage.remove(0);
//                                    classSharedPreferences.setList("list",unSendMessage);
//                                }
//
//                            }

                        adapter.notifyDataSetChanged();
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
                        else new DownloadFileFromSocket(chatMessage).execute();


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
    private void newMeesage(JSONObject chatMessage) {


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
            chatRoomRepo.getChatRoomModelList().add(new ChatRoomModel(userName, anthor_user_id, message, imageUrl, false, "0", user_id + anthor_user_id, "null", "0", true, fcmToken, specialNumber, type, "1", time, false, "null"));
        }

        serverApi.sendNotification(message, type,fcmToken,chat_id);
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

    private void updateMessage(JSONObject chatMessage) {
        Intent service = new Intent(this, SocketIOService.class);

        service.putExtra(SocketIOService.EXTRA_ON_UPDTE_MESSAGE_PARAMTERS, chatMessage.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_ON_UPDATE_MESSAGE);
        startService(service);

    }
//    private void startCall() {
//        Intent service = new Intent(this, SocketIOService.class);
//        JSONObject data = new JSONObject();
//        JSONObject type = new JSONObject();
//        try {
//            type.put("video",true);
//            type.put("audio",true);
//            data.put("rcv_id", anthor_user_id);
//            data.put("type", type.toString());
//            data.put("message", "");
//            data.put("snd_id", user_id);
//
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        System.out.println("call");
//        service.putExtra(SocketIOService.EXTRA_CALL_PARAMTERS, data.toString());
//        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_CALLING);
//        startService(service);
//
//    }
//    private void sendPeerId(String object) {
//        System.out.println(object+"this is object ");
//        JSONObject message = null;
//        /////////
//
//
//
//                    try {
//                        message = new JSONObject(object);
//                        message.put("peerId",peerId);
//
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    System.out.println(message.toString()+"peeeeeeeeeeeId object");
//
//
//        Intent service = new Intent(this, SocketIOService.class);
//
//        service.putExtra(SocketIOService.EXTRA_SEND_PEER_ID_PARAMTERS, message.toString());
//        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_SEND_PEER_ID);
//        startService(service);
//
//    }
    ///////////////end

    float textSize = 14.0F;
    int progressNew = 0;

    SharedPreferences sharedPreferences;
    PickiT pickiT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences("txtFontSize", Context.MODE_PRIVATE);
        pickiT = new PickiT(this, this, this);
        if (!isPermissionGranted()) {
            askPermissions();
        }


        initViews();
        initAction();
        EnterRoom();
        checkConnect();
        LocalBroadcastManager.getInstance(this).registerReceiver(check, new IntentFilter(CHEK));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveTyping, new IntentFilter(TYPING));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveNwMessage, new IntentFilter(ON_MESSAGE_RECEIVED));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveDeleteMessage, new IntentFilter(ON_MESSAGE_DELETED));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveUpdateMessage, new IntentFilter(ON_MESSAGE_UPDATE));
//        LocalBroadcastManager.getInstance(this).registerReceiver(recivecallRequest, new IntentFilter(ON_CALL_REQUEST));
//        LocalBroadcastManager.getInstance(this).registerReceiver(recivePeerId, new IntentFilter(FETCH_PEER_ID));


    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResuuuuuuuuuuuumr");
//        checkConnect();
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("OnStop");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("hasConnection", hasConnection);
    }


    private void initViews() {

//        cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        timeProperties = new TimeProperties();
        conversationModelView = new ViewModelProvider(this).get(ConversationModelView.class);

//        moreOption = findViewById(R.id.image_button_Options);
        messageLiner = findViewById(R.id.liner);
        videoCallBtn = findViewById(R.id.video_call);
        audioCallBtn = findViewById(R.id.audio_call);
        webView = findViewById(R.id.webView);

        myBase = BaseApp.getInstance();

        textForBlock = findViewById(R.id.text_for_block);
        serverApi = new ServerApi(this);

        chatRoomRepo = myBase.getChatRoomRepo();
        blockUserRepo = myBase.getBlockUserRepo();
        Bundle bundle = getIntent().getExtras();
        user_id = bundle.getString("sender_id", "1");
        anthor_user_id = bundle.getString("reciver_id", "2");
        userName = bundle.getString("name", "user");
        imageUrl = bundle.getString("image");
        specialNumber = bundle.getString("special", "");


        chat_id = bundle.getString("chat_id", "");
        if (chat_id.isEmpty()) {
            System.out.println("anthor_user_id" + anthor_user_id);
            chat_id = chatRoomRepo.getChatId(anthor_user_id);
            System.out.println(chat_id + "chat_iddddddddd");
        }
        fcmToken = bundle.getString("fcm_token", "");
        personImage = findViewById(R.id.user_image);
        if (!imageUrl.isEmpty()) {
            Glide.with(personImage).load(AllConstants.imageUrl + imageUrl).error(R.drawable.th).into(personImage);
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

        ///// block when anthor user do
        ;
        blockUserRepo.userBlockListMutableLiveData.observe(this, new androidx.lifecycle.Observer<ArrayList<UserModel>>() {
            @Override
            public void onChanged(ArrayList<UserModel> userModelArrayList) {
                boolean isAnyOneBlock = false;

                if (userModelArrayList != null) {
                    for (UserModel userModel : userModelArrayList) {
                        if (userModel.getUserId().equals(anthor_user_id)) {
                            if (userModel.getStatus().equals(user_id)) {
                                textForBlock.setText(getResources().getString(R.string.block_message));
                                textForBlock.setVisibility(View.VISIBLE);
                                messageLiner.setVisibility(View.GONE);
                                blockedForMe = true;
                                isAnyOneBlock = true;
                            } else if (userModel.getStatus().equals(anthor_user_id)) {
                                textForBlock.setVisibility(View.VISIBLE);
                                messageLiner.setVisibility(View.GONE);

                                textForBlock.setText(getResources().getString(R.string.block_message2));
                                blockedForMe = false;
                                isAnyOneBlock = true;


                            } else if (userModel.getStatus().equals("0")) {
                                textForBlock.setVisibility(View.VISIBLE);
                                messageLiner.setVisibility(View.GONE);
                                textForBlock.setText(getResources().getString(R.string.block_message2));
                                blockedForMe = true;
                                isAnyOneBlock = true;


                            } else {
                                textForBlock.setVisibility(View.GONE);
                                messageLiner.setVisibility(View.VISIBLE);
                                blockedForMe = false;
                                isAnyOneBlock = false;


                            }

                            break;


                        }
                    }
                    if (!isAnyOneBlock) {
                        textForBlock.setVisibility(View.GONE);
                        messageLiner.setVisibility(View.VISIBLE);
                        blockedForMe = false;
                    }

                }
            }


        });
        chatHistory = new ArrayList<ChatMessage>();

        conversationModelView.getChatMessaheHistory().observe(this, new androidx.lifecycle.Observer<ArrayList<ChatMessage>>() {
            @Override
            public void onChanged(ArrayList<ChatMessage> chatMessages) {
                if (chatMessages != null) {
                    chatHistory = chatMessages;
                    adapter.add(chatHistory);
                    scroll();

                }
                //adapter.notifyDataSetChanged();

            }
        });
        adapter = new ChatAdapter(ConversationActivity.this, new ArrayList<ChatMessage>());
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
                            if (adapter.getItemCount() > 1)
                                messagesContainer.smoothScrollToPosition(
                                        adapter.getItemCount() - 1);
                        }
                    }, 100);
                }
            }
        });

        messageET = (EditText) findViewById(R.id.messageEdit);
        sendMessageBtn = findViewById(R.id.btn_send_message_text);
        sendImageBtn = findViewById(R.id.btn_send_message_image);
        searchView = findViewById(R.id.search_con);
        fowordImageBtn = findViewById(R.id.image_button_foword);

        tv_name = findViewById(R.id.name);
//        tv_name.setTextSize(textSize);
//        tv_name.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));


        tv_state = findViewById(R.id.state);
//        tv_state.setTextSize(textSize);
//        tv_state.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));


        gallery = findViewById(R.id.gallery);
//        gallery.setTextSize(textSize);
//        gallery.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        pdf = findViewById(R.id.pdf);
//        pdf.setTextSize(textSize);
//        pdf.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        contact = findViewById(R.id.contact);
//        contact.setTextSize(textSize);
//        contact.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        location = findViewById(R.id.location);
//        location.setTextSize(textSize);
//        location.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));


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


        recordView = (RecordView) findViewById(R.id.recordView);
        recordButton = (RecordButton) findViewById(R.id.recordButton);
        deletImageBtn = findViewById(R.id.image_button_delete);
        recordButton.setRecordView(recordView);

        recordButton.setListenForRecord(false);
        deletImageBtn = findViewById(R.id.image_button_delete);
        classSharedPreferences = new ClassSharedPreferences(this);

        if (classSharedPreferences.getList() != null) {
            unSendMessage = classSharedPreferences.getList();

        }


        chatRoomRepo.setInChat(chat_id, true);
        container = (RelativeLayout) findViewById(R.id.container);
        openMaps = (LinearLayout) findViewById(R.id.pick_location);
        sendLocation = (Button) findViewById(R.id.sendLocation);
        relativeMaps = (RelativeLayout) findViewById(R.id.relativeMaps);
        cardOpenItLocation = (CardView) findViewById(R.id.cardOpenItLocation);

        openMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeMapsAction();
                container.setVisibility(View.GONE);
                relativeMaps.setVisibility(View.VISIBLE);

//
            }
        });
        sendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideLayout();
                String message_id = System.currentTimeMillis() + "_" + user_id;
                System.out.println(stringLatLng + "ssssssstring" + message_id);

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
        setupWebView();

///// form get message history
//        loadDummyHistory();
    }

    private void initAction() {
        tv_name.setText(userName);


        personImage.setOnClickListener(new View.OnClickListener() {
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
                bundle.putString("blockedFor", "");


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
                intent.putExtra("anthor_user_id", anthor_user_id);
                intent.putExtra("user_name", userName);
                intent.putExtra("isVideo", true);
                intent.putExtra("fcm_token", fcmToken);




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


                startActivity(intent);
            }
        });


        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message_id = System.currentTimeMillis() + "_" + user_id;
                System.out.println(System.currentTimeMillis() + "_--" + user_id);

                if (classSharedPreferences.getList() != null) {
                    unSendMessage = classSharedPreferences.getList();

                }
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
                unSendMessage.clear();

                unSendMessage.add(jsonObject);
//              unSendMessage.clear();


                classSharedPreferences.setList("list", unSendMessage);
                for (JSONObject message :
                        unSendMessage) {

                    newMeesage(message);

                }

//                newMeesage(jsonObject);

            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
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
//                Intent in = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
//                startActivityForResult(in, RESULT_PICK_CONTACT);

            }
        });


        //// for voice record
        recordButton.setListenForRecord(true);

        recordButton.setOnClickListener(view -> {

//            recordButton.setListenForRecord(true);

//            recordButton.setListenForRecord(true);
//            recordButton.setListenForRecord(true);

//            if (permissions.isRecordingOk(ConversationActivity.this))
//                if (permissions.isStorageReadOk(ConversationActivity.this))
//                    recordButton.setListenForRecord(true);
//                else permissions.requestStorage(ConversationActivity.this);
//            else permissions.requestRecording(ConversationActivity.this);
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
//
//
//                ActivityCompat.
//                        requestPermissions(MainActivity.this,
//                                new String[]{Manifest.permission.RECORD_AUDIO},
//                                0);

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

                uploadVoice(audioName, Uri.fromFile(f));

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
        deletImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                deleteMessage(deleteMessage);
                alertDeleteDialog();

//                System.out.println(selectedMessage.get(0).getMessage()+"kkkkk");
            }
        });
        fowordImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                ForwardDialogFragment forwardDialogFragment = ForwardDialogFragment.newInstance(selectedMessage, "jj");
                forwardDialogFragment.show(fm, "fragment_edit_name");
            }
        });


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


        if (selectedMessage.size() > 1) {
            copyItem.setVisible(false);
            updateItem.setVisible(false);
//            menu.removeItem(item.getItemId());

        } else if (selectedMessage.size() == 1) {
            if (selectedMessage.get(0).getType().equals("text")) {
                copyItem.setVisible(true);
                System.out.println(selectedMessage.get(0).getMessage() + "majddddd");
                if (selectedMessage.get(0).getUserId().equals(user_id)) {
                    updateItem.setVisible(true);
                } else updateItem.setVisible(false);

            } else {
                updateItem.setVisible(false);
                copyItem.setVisible(false);

            }


        } else {
            copyItem.setVisible(false);
            updateItem.setVisible(false);


        }
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
                ClipData clipData = ClipData.newPlainText("key", selectedMessage.get(0).getMessage());
                clipboardManager.setPrimaryClip(clipData);
//                chatHistory.get(selectedMessage.get(0).).setChecked(false);
                for (ChatMessage chatMessage : chatHistory) {
                    if (chatMessage.getId().equals(selectedMessage.get(0).getId())) {
                        chatMessage.setChecked(false);
                        break;
                    }
                }
                selectedMessage.clear();
                deleteMessage.clear();
//                System.out.println(chatMessage.getMessage()+chatMessage.getId());

                adapter.notifyDataSetChanged();
                toolsLiner.setVisibility(View.GONE);
                personInformationLiner.setVisibility(View.VISIBLE);
                toolbar.setBackgroundColor(getResources().getColor(R.color.green_500));
                return true;
            case R.id.item_update:
                showUpdateMessageDialog(selectedMessage.get(0));

                for (ChatMessage chatMessage : chatHistory) {
                    if (chatMessage.getId().equals(selectedMessage.get(0).getId())) {
                        chatMessage.setChecked(false);
                        break;
                    }
                }
                selectedMessage.clear();
                deleteMessage.clear();
//                System.out.println(chatMessage.getMessage()+chatMessage.getId());

                adapter.notifyDataSetChanged();
                toolsLiner.setVisibility(View.GONE);
                personInformationLiner.setVisibility(View.VISIBLE);
                toolbar.setBackgroundColor(getResources().getColor(R.color.green_500));
                return true;
            case R.id.item_block:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle(R.string.alert_block_user);
                dialog.setPositiveButton(R.string.block,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                serverApi.block(user_id, userModel);
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

                                serverApi.unbBlockUser(user_id, userModel);
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


    private void deleteMessage() {
        final ProgressDialog progressDialo = new ProgressDialog(this);
        JSONArray jsonArray = new JSONArray(deleteMessage);
        ;
        progressDialo.setMessage(getResources().getString(R.string.prograss_message));
        progressDialo.show();
        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, AllConstants.delete_message, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialo.dismiss();
                System.out.println("Data added to API+" + response);
                for (ChatMessage message : selectedMessage) {
                    adapter.chatMessages.remove(message);
                }
                selectedMessage.clear();
                deleteMessage.clear();
//                System.out.println(chatMessage.getMessage()+chatMessage.getId());
                adapter.notifyDataSetChanged();
                toolsLiner.setVisibility(View.GONE);
                personInformationLiner.setVisibility(View.VISIBLE);
                toolbar.setBackgroundColor(getResources().getColor(R.color.green_500));


            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
//                Toast.makeText(ConversationActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // below line we are creating a map for
                // storing our values in key and value pair.
                Map<String, String> params = new HashMap<String, String>();

                // on below line we are passing our key
                // and value pair to our parameters.
                params.put("message_id", deleteMessage.toString());
                params.put("user_id", user_id);


                // at last we are
                // returning our params.
                return params;
            }
        };
        // below line is to make
        // a json object request.
        myBase.addToRequestQueue(request);
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
    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        scroll();
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
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        System.out.println(requestCode + "this is Requesstttttttttttttt code ");
        switch (requestCode) {
            case MY_REQUEST_CODE_PERMISSION: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    this.doBrowseFile();
                }
                // Cancelled or denied.
                else {
                    Log.i(",,", "Permission denied!");
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                    showPermissionDialog(getResources().getString(R.string.read_premission), 1999);
                }
                break;
            }
            case AllConstants.RECORDING_REQUEST_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (this.permissions.isStorageReadOk(ConversationActivity.this))
                        return;
                    else this.permissions.requestStorage(ConversationActivity.this);

                } else
                    showPermissionDialog(getResources().getString(R.string.record_voice_premission), 1888);
//                    this.permissions.requestRecording(ConversationActivity.this);
//                    Toast.makeText(this, "Recording permission denied", Toast.LENGTH_SHORT).show();}
                break;
            }
            case AllConstants.STORAGE_REQUEST_CODE:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    return;
                else
                    showPermissionDialog(getResources().getString(R.string.read_premission), 1777);
                break;
            case 44:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // When Permission Granted
                    //Call Method
                    getCurrentLocation();
                } else {
                    showPermissionDialog(getResources().getString(R.string.location_premission), 1555);

                }
                break;
            case 448:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // When Permission Granted
                    //Call Method
                    openMap();

                } else {
                    showPermissionDialog(getResources().getString(R.string.location_premission), 1448);

                }
                break;
            case 9921:
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
                    showPermissionDialog(getResources().getString(R.string.camera_premission), 1666);
                }
                break;
            case AllConstants.CONTACTS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent in = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                    startActivityForResult(in, RESULT_PICK_CONTACT);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                        showPermissionDialog(getResources().getString(R.string.contact_permission), 2020);

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

    //    @Override
//    public void onBackPressed() {
//
//        if (binding.dataLayout.getVisibility() == View.VISIBLE)
//            hideLayout();
//        else
//            super.onBackPressed();
//    }
///// for scroll to end of list
    private void scroll() {
        messagesContainer.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                if (adapter.getItemCount() > 0) {
                    messagesContainer.scrollToPosition(adapter.getItemCount() - 1);
                }

            }
        });
//        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    //// for get all message
    @SuppressLint("Range")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String message_id = System.currentTimeMillis() + "_" + user_id;
        if (requestCode == 1999) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                showPermissionDialog(getResources().getString(R.string.read_premission), 1999);

            } else {
                this.doBrowseFile();
            }
        } else if (requestCode == 1888) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
                showPermissionDialog(getResources().getString(R.string.record_voice_premission), 1888);

            } else {
                if (this.permissions.isStorageReadOk(ConversationActivity.this))
                    return;
                else this.permissions.requestStorage(ConversationActivity.this);

            }
        } else if (requestCode == 1777) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                showPermissionDialog(getResources().getString(R.string.read_premission), 1777);


            } else {
                return;
            }

        } else if (requestCode == 1666) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                showPermissionDialog(getResources().getString(R.string.camera_premission), 1666);


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

        } else if (requestCode == 1555) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                showPermissionDialog(getResources().getString(R.string.location_premission), 1555);


            } else {
                getCurrentLocation();
            }
        } else if (requestCode == 1448) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                showPermissionDialog(getResources().getString(R.string.location_premission), 1555);
            } else {
                openMap();
            }
        } else if (requestCode == 2020) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
                showPermissionDialog(getResources().getString(R.string.contact_permission), 2020);
            } else {
                Intent in = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(in, RESULT_PICK_CONTACT);
            }

        } else if (resultCode == RESULT_OK) {
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
                    System.out.println(pathUri + "patttttttttttttttttttt");
//                    }


                    String uriString = uri.toString();
//                    copyFileOrDirectory( FilePath.getPath(this,pathUri), this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send/video").getAbsolutePath());


//                    Uri pathUri = Uri.fromFile(new File(pathh));


//                    System.out.println(uri+"ppppp"+FilePath.getPath(this,Uri.parse(pathUri.toString()))+"firsssssssssst");

//                    copyFileOrDirectory( FilePath.getPath(this,Uri.parse(uriString),, this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send/video").getAbsolutePath()));


                    String displayName = null;

                    if (uriString.startsWith("content://")) {
                        Cursor cursor = null;
                        try {
                            cursor = this.getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                Log.d("nameeeee>>>>  ", displayName);

                                uploadPDF(displayName, uri);

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
                    if (!isVideoFile(selectedMediaUri.toString())) {
                        String displayNamee = null;

                        Uri pathImage = Uri.fromFile(new File(selectedMediaUri.toString()));
                        File myFileImage = new File(pathImage.toString());

//                        System.out.println("FileUtil.getPath(this,selectedMediaUri)" + FileUtil.getPath(this, selectedMediaUri) + path);
//                            copyFileOrDirectory(FileUtil.getPath(this,selectedMediaUri),Environment.getExternalStoragePublicDirectory("memo/send/video").getAbsolutePath());
                        copyFileOrDirectory(FileUtil.getPath(this, pathImage), this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send/video").getAbsolutePath());


                        if (pathImage.toString().startsWith("content://")) {
                            Cursor cursor = null;
                            try {
                                cursor = this.getContentResolver().query(selectedMediaUri, null, null, null, null);
                                if (cursor != null && cursor.moveToFirst()) {
                                    displayNamee = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                    Log.d("nameeeee>>>>  ", displayNamee);
//                                    System.out.println(displayNamee);

                                    uploadImage(displayNamee, selectedMediaUri);
                                }
                            } finally {
                                cursor.close();
                            }
                        } else if (pathImage.toString().startsWith("file://")) {
                            displayNamee = myFileImage.getName();
                            System.out.println(displayNamee + "lkkkkkkkkkkkkkkkk");
                            uploadImage(displayNamee, pathImage);


                            Log.d("nameeeee>>>>  ", displayNamee);
                        }

                    } else {
//                        System.out.println(selectedMediaUri.toString()+"selectedMediaUri.toString()");
//                                                Uri pathhh = Uri.fromFile(new File(selectedMediaUri.toString()));
//
//                        TrimVideo.activity(pathhh.toString())
//                                .setTrimType(TrimType.MIN_MAX_DURATION)
//                                .setLocal("ar")
//                                .setMinToMax(1, 15) //seconds
//                                .start(ConversationActivity.this, startForResult);
//            }

//                            String uriString = selectedMediaUri.toString();
//                            System.out.println("this is Video"+uriString);
//                            File myFile = new File(uriString);

//                            String path = myFile.getAbsolutePath();
                        ////////////////////////////
                        Uri pathhh = Uri.fromFile(new File(selectedMediaUri.toString()));
                        File myFilee = new File(pathhh.toString());

//                        System.out.println("FileUtil.getPath(this,selectedMediaUri)" + FileUtil.getPath(this, selectedMediaUri) + path);
//                            copyFileOrDirectory(FileUtil.getPath(this,selectedMediaUri),Environment.getExternalStoragePublicDirectory("memo/send/video").getAbsolutePath());
                        copyFileOrDirectory(FileUtil.getPath(this, pathhh), this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send/video").getAbsolutePath());

                        String displayNamee = null;

                        if (pathhh.toString().startsWith("content://")) {
                            Cursor cursor = null;
                            try {
                                cursor = this.getContentResolver().query(selectedMediaUri, null, null, null, null);
                                if (cursor != null && cursor.moveToFirst()) {
                                    displayNamee = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                    Log.d("nameeeee>>>>  ", displayNamee);
                                    System.out.println(displayNamee);

                                    uploadVideo(displayNamee, selectedMediaUri);
                                }
                            } finally {
                                cursor.close();
                            }
                        } else if (pathhh.toString().startsWith("file://")) {
                            displayNamee = myFilee.getName();
                            System.out.println(displayNamee + "lkkkkkkkkkkkkkkkk");
                            uploadVideo(displayNamee, pathhh);


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

//                        System.out.println("FileUtil.getPath(this,selectedMediaUri)" + FileUtil.getPath(this, selectedMediaUri) + path);
//                            copyFileOrDirectory(FileUtil.getPath(this,selectedMediaUri),Environment.getExternalStoragePublicDirectory("memo/send/video").getAbsolutePath());
                        copyFileOrDirectory(FileUtil.getPath(this, selectedMediaUriGallery), this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send/video").getAbsolutePath());


                        if (selectedMediaUriGallery.toString().startsWith("content://")) {
                            Cursor cursor = null;
                            try {
                                cursor = this.getContentResolver().query(selectedMediaUriGallery, null, null, null, null);
                                if (cursor != null && cursor.moveToFirst()) {
                                    displayNamee = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                    Log.d("nameeeee>>>>  ", displayNamee);
//                                    System.out.println(displayNamee);

                                    uploadImage(displayNamee, selectedMediaUriGallery);
                                }
                            } finally {
                                cursor.close();
                            }
                        } else if (selectedMediaUriGallery.toString().startsWith("file://")) {
                            displayNamee = myFileImage.getName();
                            System.out.println(displayNamee + "lkkkkkkkkkkkkkkkk");
                            uploadImage(displayNamee, selectedMediaUriGallery);


                            Log.d("nameeeee>>>>  ", displayNamee);
                        }


                    } else if (mimeType.startsWith("video")) {
//                        TrimVideo.activity(String.valueOf(selectedMediaUriGallery))
////        .setCompressOption(new CompressOption()) //empty constructor for default compress option
//                                .setHideSeekBar(true)
//                                .setTrimType(TrimType.MIN_MAX_DURATION)
//                                .setMinToMax(1, 60)  //seconds
//                                .start(this);
//                        System.out.println(selectedMediaUri.toString()+"selectedMediaUri.toString()");
//                                                Uri pathhh = Uri.fromFile(new File(selectedMediaUri.toString()));
//
//                        TrimVideo.activity(pathhh.toString())
//                                .setTrimType(TrimType.MIN_MAX_DURATION)
//                                .setLocal("ar")
//                                .setMinToMax(1, 15) //seconds
//                                .start(ConversationActivity.this, startForResult);
//            }

//                            String uriString = selectedMediaUri.toString();
//                            System.out.println("this is Video"+uriString);
//                            File myFile = new File(uriString);
//
//                            String path = myFile.getAbsolutePath();
                        //////////////////////////
                        File myFilee = new File(selectedMediaUriGallery.toString());

//                        System.out.println("FileUtil.getPath(this,selectedMediaUri)" + FileUtil.getPath(this, selectedMediaUri) + path);
//                            copyFileOrDirectory(FileUtil.getPath(this,selectedMediaUri),Environment.getExternalStoragePublicDirectory("memo/send/video").getAbsolutePath());
                        copyFileOrDirectory(FileUtil.getPath(this, selectedMediaUriGallery), this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send/video").getAbsolutePath());

                        String displayNamee = null;

                        if (selectedMediaUriGallery.toString().startsWith("content://")) {
                            Cursor cursor = null;
                            try {
                                cursor = this.getContentResolver().query(selectedMediaUriGallery, null, null, null, null);
                                if (cursor != null && cursor.moveToFirst()) {
                                    displayNamee = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                    Log.d("nameeeee>>>>  ", displayNamee);
                                    System.out.println(displayNamee);

                                    uploadVideo(displayNamee, selectedMediaUriGallery);
                                }
                            } finally {
                                cursor.close();
                            }
                        } else if (selectedMediaUriGallery.toString().startsWith("file://")) {
                            displayNamee = myFilee.getName();
                            System.out.println(displayNamee + "lkkkkkkkkkkkkkkkk");
                            uploadVideo(displayNamee, selectedMediaUriGallery);


                            Log.d("nameeeee>>>>  ", displayNamee);
                        }

                    }
                    break;


            }
        } else {
            Toast.makeText(this, "Failed ", Toast.LENGTH_SHORT).show();
        }
//                    }}}
////////////////////////
    }
//        @SuppressLint("Range")
//        ActivityResultLauncher<Intent> startForResult = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                System.out.println("uri is uriiiiiiiiiiiiiiiiiiii");
//                if (result.getResultCode() == Activity.RESULT_OK &&
//                        result.getData() != null) {
//                    Uri uri = Uri.parse(TrimVideo.getTrimmedVideoPath(result.getData()));
//                     Uri pathhh = Uri.fromFile(new File(uri.toString()));
//
//                    String uriSt = uri.toString();
//                   System.out.println("uri is uri"+uriSt);
////                    String selectedFilePath = FilePath.getPath(getContext(), uri);
//
//                    File myFile = new File(uriSt);
//                    String uriString =Uri.fromFile(myFile).toString();
//                    copyFileOrDirectory(FileUtil.getPath(this, pathhh), this.getExternalFilesDir(Environment.DIRECTORY_DCIM+ File.separator+"memo/send/video").getAbsolutePath());
//
//
//
//                    System.out.println("   Uri uri = Uri.parse(TrimVideo.getTrimmedVideoPath(result.getData()));"+uriString);
//
//                    String displayName = null;
//
//                    if (uriString.startsWith("content://")) {
//                        System.out.println("uriString.startsWith(\"content://\")");
//                        Cursor cursor = null;
//                        try {
//                            cursor = this.getContentResolver().query(uri, null, null, null, null);
//                            if (cursor != null && cursor.moveToFirst()) {
//                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//                                Log.d("nameeeee>>>>  ", displayName);
//                                uploadVideo(displayName, Uri.parse(uriString));
//
////                                upload(displayName, Uri.parse(uriString),"video");
//                            }
//                        } finally {
//                            cursor.close();
//                        }
//                    } else if (uriString.startsWith("file://")) {
//
//                        displayName = myFile.getName();
//                        uploadVideo(displayName, Uri.parse(uriString));
//
//                        Log.d("nameeeee>>>>  ", displayName);
//                    }
//                    else {
////                        upload("video", uri,"video");
//
//                    }
//                    Log.d(TAG, "Trimmed path:: " + uri);
//
//                } else
//                    Toast.makeText(this, "Failed ", Toast.LENGTH_SHORT).show();
//            });


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

    //// for upload file to server
    private void uploadPDF(final String pdfname, Uri pdffile) {
        String message_id = System.currentTimeMillis() + "_" + user_id;
        System.out.println(pdfname + "pdfnameeeeeeeeeeee");

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(message_id);//dummy
        chatMessage.setMessage(pdffile.toString());
        chatMessage.setFileName(pdfname);
        chatMessage.setDate(String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis()));


//        chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatMessage.setMe(true);
        chatMessage.setType("file");
        chatMessage.setState("0");
        messageET.setText("");
        chatMessage.setChecked(false);
        displayMessage(chatMessage);


        InputStream iStream = null;
        try {
            iStream = getContentResolver().openInputStream(pdffile);
            //"file:///storage/emulated/0/memo/1640514470604.3gp"
            final byte[] inputData = getBytes(iStream);

            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, AllConstants.upload_file_URL,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            Log.d("ressssssoo", new String(response.data));
                            System.out.println("ressssssssponeee" + new String(response.data));
                            rQueue.getCache().clear();
                            try {
                                JSONObject jsonObject = new JSONObject(new String(response.data));


//                                String text = "";
//                                String type = "";
//                                String state = "";
//                                String senderId = "";
//                                String reciverId = "";
//                                String id = "";
//
//                                    /// JSONObject jsonObject= (JSONObject) messageJson.get("data");
//                                    text = jsonObject.getString("message");
//                                    type = jsonObject.getString("message_type");
//                                    state = jsonObject.getString("state");
//                                    senderId = jsonObject.getString("sender_id");
//                                    id = jsonObject.getString("id");
//
//                                    reciverId = jsonObject.getString("reciver_id");
//                                    System.out.println(text+type+state+senderId+reciverId+id);
////                                    jsonObject.put("file name",pdfname );
//
//                                newMeesage(jsonObject);
                                System.out.println(jsonObject.getString("orginalName") + pdfname);

                                JSONObject sendObject = new JSONObject();

                                sendObject.put("sender_id", jsonObject.getString("sender_id"));
                                sendObject.put("reciver_id", jsonObject.getString("reciver_id"));
                                sendObject.put("message", jsonObject.getString("message"));
                                sendObject.put("message_type", jsonObject.getString("message_type"));
                                sendObject.put("state", jsonObject.getString("state"));
                                sendObject.put("message_id", message_id);
                                sendObject.put("chat_id", jsonObject.getInt("chat_id"));
                                sendObject.put("id", jsonObject.getInt("id"));
                                sendObject.put("deleted_for", jsonObject.getString("deleted_for"));

                                if (jsonObject.getBoolean("newchat") == true) {
                                    sendObject.put("newchat", jsonObject.getBoolean("newchat"));
                                }


                                sendObject.put("orginalName", jsonObject.getString("orginalName"));
                                sendObject.put("dateTime", String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis()));


//                                newMeesage(sendObject);

//                                ChatMessage chatMessage = new ChatMessage();
//                                chatMessage.setId(message_id);//dummy
//                                chatMessage.setMessage(jsonObject.getString("message"));
//                                chatMessage.setFileName(pdfname);
//
//                                chatMessage.setDate(String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis()));
//                                chatMessage.setMe(true);
//                                chatMessage.setType("file");
//                                chatMessage.setState("0");
//                                messageET.setText("");
//                                chatMessage.setChecked(false);
//                                displayMessage(chatMessage);
                                newMeesage(sendObject);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {

                /*
                 * If you want to add more parameters with the image
                 * you can do it here
                 * here we have only one parameter with the image
                 * which is tags
                 * */
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("sender_id", user_id);
                    params.put("reciver_id", anthor_user_id);
                    params.put("message_type", "file");
                    params.put("state", "0");
                    params.put("orginalName", pdfname);
                    params.put("dateTime", String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis()));
                    return params;
                }

                /*
                 *pass files using below method
                 * */
                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();

//                    params.put("files", new DataPart(pdfname, inputData, "plan/text"));
                    params.put("files", new DataPart(pdfname, inputData, "plan/text"));


                    return params;
                }
            };


            volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            rQueue = Volley.newRequestQueue(ConversationActivity.this);
//            rQueue.add(volleyMultipartRequest);
            myBase.addToRequestQueue(volleyMultipartRequest);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    ///// for upload voice
    private void uploadVoice(final String voiceName, Uri voicedPath) {
        String message_id = System.currentTimeMillis() + "_" + user_id;
        System.out.println("the message id is" + message_id);


        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(message_id);//dummy
        chatMessage.setMessage(voicedPath.toString());
        chatMessage.setFileName(voiceName);

        chatMessage.setDate(String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis()));
        chatMessage.setMe(true);
        chatMessage.setType("voice");
        chatMessage.setState("0");
        chatMessage.setChecked(false);

        messageET.setText("");
        displayMessage(chatMessage);


        InputStream iStream = null;
        try {

            iStream = getContentResolver().openInputStream(voicedPath);
            final byte[] inputData = getBytes(iStream);

            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, AllConstants.upload_Voice_URL,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            Log.d("ressssssoo", new String(response.data));
                            System.out.println("responeeeeeeeeeeee" + new String(response.data));
                            rQueue.getCache().clear();
                            try {
                                JSONObject jsonObject = new JSONObject(new String(response.data));
                                JSONObject sendObject = new JSONObject();

                                sendObject.put("sender_id", jsonObject.getString("sender_id"));
                                sendObject.put("reciver_id", jsonObject.getString("reciver_id"));
                                sendObject.put("message", jsonObject.getString("message"));
                                sendObject.put("message_type", jsonObject.getString("message_type"));
                                sendObject.put("state", jsonObject.getString("state"));
                                sendObject.put("message_id", message_id);
                                sendObject.put("orginalName", jsonObject.getString("orginalName"));
                                sendObject.put("chat_id", jsonObject.getInt("chat_id"));
                                sendObject.put("id", jsonObject.getInt("id"));
                                sendObject.put("deleted_for", jsonObject.getString("deleted_for"));

                                if (jsonObject.getBoolean("newchat") == true) {
                                    sendObject.put("newchat", jsonObject.getBoolean("newchat"));
                                }

                                sendObject.put("dateTime", String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis()));


                                newMeesage(sendObject);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {

                /*
                 * If you want to add more parameters with the image
                 * you can do it here
                 * here we have only one parameter with the image
                 * which is tags
                 * */
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("sender_id", user_id);
                    params.put("reciver_id", anthor_user_id);
                    params.put("message_type", "voice");
                    params.put("state", "0");
                    params.put("orginalName", voiceName);
                    params.put("dateTime", String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis()));
                    return params;
                }

                /*
                 *pass files using below method
                 * */
                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();

//                    params.put("audios", new DataPart(voiceName, inputData));
                    params.put("audios", new DataPart(voiceName, inputData, "audio/aac"));


                    return params;
                }
            };


            volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            rQueue = Volley.newRequestQueue(ConversationActivity.this);
//            rQueue.add(volleyMultipartRequest);
            myBase.addToRequestQueue(volleyMultipartRequest);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //// for upload video
    private void uploadVideo(final String pdfname, Uri pdffile) {
        String message_id = System.currentTimeMillis() + "_" + user_id;

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(message_id);//dummy
        chatMessage.setMessage(pdffile.toString());
        chatMessage.setFileName(pdfname);

        chatMessage.setDate(String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis()));
        chatMessage.setMe(true);
        chatMessage.setType("video");
        chatMessage.setState("0");
        messageET.setText("");
        chatMessage.setChecked(false);
        displayMessage(chatMessage);


        InputStream iStream = null;
        try {

            iStream = getContentResolver().openInputStream(pdffile);
            System.out.println(pdffile);
            //"file:///storage/emulated/0/memo/1640514470604.3gp"
            final byte[] inputData = getBytes(iStream);

            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, AllConstants.upload_video_URL,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            System.out.println("responeeeeeeeeeeee" + new String(response.data));

                            rQueue.getCache().clear();
                            try {
                                JSONObject jsonObject = new JSONObject(new String(response.data));

                                JSONObject sendObject = new JSONObject();

                                sendObject.put("sender_id", jsonObject.getString("sender_id"));
                                sendObject.put("reciver_id", jsonObject.getString("reciver_id"));
                                sendObject.put("message", jsonObject.getString("message"));
                                sendObject.put("message_type", jsonObject.getString("message_type"));
                                sendObject.put("state", jsonObject.getString("state"));
                                sendObject.put("message_id", message_id);
                                sendObject.put("chat_id", jsonObject.getInt("chat_id"));
                                sendObject.put("id", jsonObject.getInt("id"));
                                sendObject.put("deleted_for", jsonObject.getString("deleted_for"));

                                if (jsonObject.getBoolean("newchat") == true) {
                                    System.out.println("newwwwwwwwwwwww chatttttttttttt");
                                    sendObject.put("newchat", jsonObject.getBoolean("newchat"));
                                }

                                sendObject.put("orginalName", jsonObject.getString("orginalName"));
                                sendObject.put("dateTime", String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis()));


                                newMeesage(sendObject);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {

                /*
                 * If you want to add more parameters with the image
                 * you can do it here
                 * here we have only one parameter with the image
                 * which is tags
                 * */
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("sender_id", user_id);
                    params.put("reciver_id", anthor_user_id);
                    params.put("message_type", "video");
                    params.put("state", "0");
                    params.put("orginalName", pdfname);
                    params.put("dateTime", String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis()));
                    return params;
                }

                /*
                 *pass files using below method
                 * */
                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();

                    params.put("vedios", new DataPart(pdfname, inputData, "plan/text"));

                    return params;
                }
            };


            volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            rQueue = Volley.newRequestQueue(ConversationActivity.this);
//            rQueue.add(volleyMultipartRequest);
            myBase.addToRequestQueue(volleyMultipartRequest);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void uploadImage(final String imageName, Uri pdfFile) {
        String message_id = System.currentTimeMillis() + "_" + user_id;
        //                        ChatMessage chatMessage = new ChatMessage();
//                        chatMessage.setImage(String.valueOf(selectedMediaUri));
//                        chatMessage.setType("image");
//                        chatMessage.setState("0");
//                        chatMessage.setId(message_id);
//                        chatMessage.setFileName(displayNamee);
//
//                        chatMessage.setDate(String.valueOf(cal.getTimeInMillis()));
//                        chatMessage.setMe(true);
//                        displayMessage(chatMessage);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(message_id);//dummy
        chatMessage.setImage(pdfFile.toString());
        chatMessage.setFileName(imageName);

        chatMessage.setDate(String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis()));
        chatMessage.setMe(true);
        chatMessage.setType("imageWeb");
        chatMessage.setState("0");
        messageET.setText("");
//        chatMessage.setDate(String.valueOf(cal.getTimeInMillis()));
        chatMessage.setChecked(false);
        displayMessage(chatMessage);


        InputStream iStream = null;
        try {

            iStream = getContentResolver().openInputStream(pdfFile);
            System.out.println(pdfFile);
            //"file:///storage/emulated/0/memo/1640514470604.3gp"
            final byte[] inputData = getBytes(iStream);

            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, AllConstants.upload_image_URL,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            System.out.println("responeeeeeeeeeeee" + new String(response.data));

                            rQueue.getCache().clear();
                            try {
                                JSONObject jsonObject = new JSONObject(new String(response.data));

                                JSONObject sendObject = new JSONObject();


                                sendObject.put("sender_id", jsonObject.getString("sender_id"));
                                sendObject.put("reciver_id", jsonObject.getString("reciver_id"));
                                sendObject.put("message", jsonObject.getString("message"));
                                sendObject.put("message_type", jsonObject.getString("message_type"));
                                sendObject.put("state", jsonObject.getString("state"));
                                sendObject.put("message_id", message_id);
                                sendObject.put("chat_id", jsonObject.getInt("chat_id"));
                                sendObject.put("id", jsonObject.getInt("id"));
                                sendObject.put("deleted_for", jsonObject.getString("deleted_for"));

                                if (jsonObject.getBoolean("newchat") == true) {
                                    sendObject.put("newchat", jsonObject.getBoolean("newchat"));
                                }

                                sendObject.put("orginalName", jsonObject.getString("orginalName"));
                                sendObject.put("dateTime", String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis()));


                                newMeesage(sendObject);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {

                /*
                 * If you want to add more parameters with the image
                 * you can do it here
                 * here we have only one parameter with the image
                 * which is tags
                 * */
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("sender_id", user_id);
                    params.put("reciver_id", anthor_user_id);
                    params.put("message_type", "imageWeb");
                    params.put("state", "0");
                    params.put("orginalName", imageName);
                    params.put("dateTime", String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis()));
                    return params;
                }

                /*
                 *pass files using below method
                 * */
                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();

                    params.put("img_chat", new DataPart(imageName, inputData, "plan/text"));

                    return params;
                }
            };


            volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            rQueue = Volley.newRequestQueue(ConversationActivity.this);
//            rQueue.add(volleyMultipartRequest);
            myBase.addToRequestQueue(volleyMultipartRequest);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


    ///////for copy file
    public static void copyFileOrDirectory(String srcDir, String dstDir) {

        try {
            System.out.println(srcDir + dstDir + "copy Recorrrrrrd");

            File src = new File(srcDir);
            File dst = new File(dstDir, src.getName());


            if (src.isDirectory()) {

                String files[] = src.list();
                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(src, files[i]).getPath());
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(src1, dst1);

                }
            } else {
                copyFile(src, dst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        System.out.println(sourceFile.getPath() + destFile.getPath() + "nameeeeeee");
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

///// end copy file


    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("on destroy");
        chatRoomRepo.setInChat(chat_id, false);

        LocalBroadcastManager.getInstance(this).unregisterReceiver(check);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveTyping);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveNwMessage);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveDeleteMessage);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveUpdateMessage);
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(recivecallRequest);
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(recivePeerId);


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

            Toast t = Toast.makeText(getApplicationContext(), "You don't have read access !", Toast.LENGTH_LONG);
            t.show();

        } else {
            if (myMessage) {
                File d = this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo" + File.separator + "send");  // -> filename = maven.pdf
                pdfFile = new File(d, chatMessage.getFileName());
            } else {
                File d = this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo" + File.separator + "recive");  // -> filename = maven.pdf
                pdfFile = new File(d, chatMessage.getMessage().toString());
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
                    Toast.makeText(ConversationActivity.this, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ConversationActivity.this, getResources().getString(R.string.please_download), Toast.LENGTH_SHORT).show();

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

            Toast t = Toast.makeText(getApplicationContext(), "You don't have write access !", Toast.LENGTH_LONG);
            t.show();

        } else {

            Log.v(TAG, "download() Method HAVE PERMISSIONS ");
            if (myMessage) {
                File d = this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send");

                pdfFile = new File(d, chatMessage.getFileName());
                if (!pdfFile.exists()) {

//            new DownloadFile().execute("http://maven.apache.org/maven-1.x/maven.pdf", "maven.pdf");
//                    new DownloadFile().execute(AllConstants.download_url + "files/" + chatMessage.getMessage().toString(), chatMessage.getFileName(), "send");
                    new DownloadFile().execute(AllConstants.download_url + chatMessage.getMessage().toString(), chatMessage.getFileName(), "send");

                } else {
                    Log.v(TAG, "File already download ");

                }
            } else {

                File d = this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive");

                pdfFile = new File(d, chatMessage.getMessage().toString());
                if (!pdfFile.exists()) {

//            new DownloadFile().execute("http://maven.apache.org/maven-1.x/maven.pdf", "maven.pdf");
//                    new DownloadFile().execute(AllConstants.download_url + "files/" + chatMessage.getMessage().toString(), chatMessage.getMessage().toString(), "recive");
                    new DownloadFile().execute(AllConstants.download_url + chatMessage.getMessage().toString(), chatMessage.getMessage().toString(), "recive");

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

            Toast t = Toast.makeText(getApplicationContext(), "You don't have write access !", Toast.LENGTH_LONG);
            t.show();

        } else {

            Log.v(TAG, "download() Method HAVE PERMISSIONS ");
            if (myMessage) {
                File d = this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send/voiceRecord");

                audioFile = new File(d, chatMessage.getFileName());
                if (!audioFile.exists()) {
                    System.out.println(chatMessage.message.toString() + "kkkkkkkkkk");

//            new DownloadFile().execute("http://maven.apache.org/maven-1.x/maven.pdf", "maven.pdf");
                    //new DownloadFile().execute("http://192.168.1.9:8080/src/yawar_chat/uploads/files/" + chatMessage.getMessage().toString(), chatMessage.getFileName(),"send");
//                    new DownloadFile().execute(AllConstants.download_url + "audio/" + chatMessage.getMessage().toString(), chatMessage.getFileName(), "send/voiceRecord");
                    new DownloadFile().execute(AllConstants.download_url + chatMessage.getMessage().toString(), chatMessage.getFileName(), "send/voiceRecord");


                } else {
                    MediaPlayer mediaPlayer = MediaPlayer.create(this, Uri.parse(audioFile.getAbsolutePath()));
                    mediaPlayer.start();
                    Log.v(TAG, "File already download ");
                    System.out.println("File already download ");

                }
            } else {

                File d = this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive/voiceRecord");

                audioFile = new File(d, chatMessage.getMessage().toString());
                if (!audioFile.exists()) {

//            new DownloadFile().execute("http://maven.apache.org/maven-1.x/maven.pdf", "maven.pdf");
//                    new DownloadFile().execute(AllConstants.download_url + "audio/" + chatMessage.getMessage().toString(), chatMessage.getMessage().toString(), "recive/voiceRecord");
                    new DownloadFile().execute(AllConstants.download_url + chatMessage.getMessage().toString(), chatMessage.getMessage().toString(), "recive/voiceRecord");

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

            Toast t = Toast.makeText(getApplicationContext(), "You don't have write access !", Toast.LENGTH_LONG);
            t.show();

        } else {

            Log.v(TAG, "download() Method HAVE PERMISSIONS ");
            if (myMessage) {
                File d = this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send/video");

                videoFile = new File(d, chatMessage.getFileName());
                if (!videoFile.exists()) {

//                    new DownloadFile().execute(AllConstants.download_url + "video/" + chatMessage.getMessage().toString(), chatMessage.getFileName(), "send/video");
                    new DownloadFile().execute(AllConstants.download_url + chatMessage.getMessage().toString(), chatMessage.getFileName(), "send/video");

                } else {
                    Log.v(TAG, "File already download ");

                }
            } else {

                File d = this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive/video");

                videoFile = new File(d, chatMessage.getMessage().toString());
                if (!videoFile.exists()) {

//            new DownloadFile().execute("http://maven.apache.org/maven-1.x/maven.pdf", "maven.pdf");
//                    new DownloadFile().execute(AllConstants.download_url + "video/" + chatMessage.getMessage().toString(), chatMessage.getMessage().toString(), "recive/video");
                    new DownloadFile().execute(AllConstants.download_url + chatMessage.getMessage().toString(), chatMessage.getMessage().toString(), "recive/video");

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

            Toast t = Toast.makeText(getApplicationContext(), "You don't have write access !", Toast.LENGTH_LONG);
            t.show();

        } else {

            Log.v(TAG, "download() Method HAVE PERMISSIONS ");
            if (myMessage) {
                File d = this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send/video");

                imageFile = new File(d, chatMessage.getFileName());
                if (!imageFile.exists()) {

                    new DownloadFile().execute(AllConstants.imageUrlInConversation + chatMessage.getImage().toString(), chatMessage.getFileName(), "send/video");
                } else {
                    Log.v(TAG, "File already download ");

                }
            } else {

                File d = this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive/video");

                imageFile = new File(d, chatMessage.getImage().toString());
                if (!imageFile.exists()) {

//            new DownloadFile().execute("http://maven.apache.org/maven-1.x/maven.pdf", "maven.pdf");
                    new DownloadFile().execute(AllConstants.imageUrlInConversation + chatMessage.getImage().toString(), chatMessage.getImage().toString(), "recive/video");
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

        String[] latlong = chatMessage.getMessage().toString().split(",");
        System.out.println(chatMessage.getMessage().toString() + " chatMessage.getMessage().toString()");
//        5.967120
        double latitude = Double.parseDouble(latlong[0]);
        double longitude = Double.parseDouble(latlong[1]);
//        client = LocationServices.getFusedLocationProviderClient(this);
        locationLatLng = new LatLng(latitude, longitude);
        container.setVisibility(View.GONE);
        relativeMaps.setVisibility(View.VISIBLE);


        if (ActivityCompat.checkSelfPermission(ConversationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(ConversationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ConversationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 448);
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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
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

//                                   latLng = new LatLng("lat from response API" + lat ,"longg from response API" + longg);


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


    @SuppressLint("ResourceType")
    @Override
    public void onLongClick(int position, ChatMessage chatMessage, boolean isChecked) {
        System.out.println(isChecked);
        personInformationLiner.setVisibility(View.GONE);
        toolbar.setBackgroundColor(getResources().getColor(R.color.memo_background_color));


        toolsLiner.setVisibility(View.VISIBLE);

        if (isChecked) {
            System.out.println(chatMessage.getMessage() + selectedMessage.size() + "getMessage");
            selectedMessage.add(chatMessage);
            System.out.println(chatMessage.getUserId() + "mmmmhhhh" + selectedMessage.get(0).getUserId());
            deleteMessage.add("\"" + chatMessage.getId() + "\"");
        } else {
            selectedMessage.remove(chatMessage);
            System.out.println("selected message " + selectedMessage.size() + "lllll");
            deleteMessage.remove("\"" + chatMessage.getId() + "\"");

            if (selectedMessage.size() < 1) {
                System.out.println("selectedMessage.size() < 1");
                personInformationLiner.setVisibility(View.VISIBLE);
                toolbar.setBackgroundColor(getResources().getColor(R.color.memo_background_color));
                toolsLiner.setVisibility(View.GONE);
            }

        }

        System.out.println(deleteMessage.size() + "is sizeeee");
        adapter.notifyDataSetChanged();
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
    public void update(Observable observable, Object o) {
        System.out.println("prevvvvvvvvvvvvvObserrrrrrrrrrrrrrrrrrv");
//        if(chat_id.equals(user_id+anthor_user_id)){
//            System.out.println("afterrrrrrrrrrrrrrrrObserrrrrrrrrrrrrrrrrrv");
//
//            chat_id = myBase.getObserver().getChatId(anthor_user_id);
//
//        }

    }

//    public void sendNotification(String message, String type) {
//
//        try {
//            RequestQueue queue = Volley.newRequestQueue(ConversationActivity.this);
//
//
//            System.out.println("fcmTokennn" + fcmToken + "message" + message);
//
//            JSONObject data = new JSONObject();
//            data.put("title", classSharedPreferences.getUser().getUserName());
//            data.put("body", message);
//            data.put("image", classSharedPreferences.getUser().getImage());
//            data.put("chat_id", chat_id);
//            data.put("type", type);
//
//            JSONObject notification_data = new JSONObject();
//            notification_data.put("data", data);
//            notification_data.put("to", fcmToken);
//            notification_data.put("content_available", true);
//            notification_data.put("priority", "high");
//
//
//            JsonObjectRequest request = new JsonObjectRequest(AllConstants.fcm_send_notification_url, notification_data, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    System.out.println("responeeeeeeeeeeeeeeeeeeeeeeeeee" + message + fcmToken);
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//
//                }
//            }) {
//                @Override
//                public Map<String, String> getHeaders() {
//                    Map<String, String> headers = new HashMap<>();
//                    headers.put("Content-Type", "application/json");
//                    headers.put("Authorization", AllConstants.api_key_fcm_token_header_value);
//                    return headers;
//                }
//            };
//
////      queue.add(request);
//            myBase.addToRequestQueue(request);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void PickiTonUriReturned() {
        System.out.println("PickiTonUriReturned");

    }

    @Override
    public void PickiTonStartListener() {
        System.out.println("PickiTonStartListener");

    }

    @Override
    public void PickiTonProgressUpdate(int progress) {
        System.out.println("PickiTonProgressUpdate");

    }

    @Override
    public void PickiTonCompleteListener(String path, boolean wasDriveFile, boolean wasUnknownProvider, boolean wasSuccessful, String Reason) {
//        System.out.println("PickiTonCompleteListener");
        copyFileOrDirectory(path, this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send").getAbsolutePath());
//                    ll;l

    }

    @Override
    public void PickiTonMultipleCompleteListener(ArrayList<String> paths, boolean wasSuccessful, String Reason) {
        System.out.println("PickiTonMultipleCompleteListener" + paths + wasSuccessful);
        System.out.println(paths.size() + "sizee");


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
            System.out.println("data notifyyyyyyyyyyyyyyyyyy");
            adapter.notifyDataSetChanged();

        }
    }

    public boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        System.out.println(mimeType + "memeType");
        return mimeType != null && mimeType.indexOf("video") == 0;

    }


    private void showUpdateMessageDialog(ChatMessage chatMessage) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_message_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);
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
        //Assign Variable

        //Check Permission
        if (ActivityCompat.checkSelfPermission(ConversationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //When Permission Grated
            //Call Method


            getCurrentLocation();


        } else {
            // When Permission Denied
            // Request Permission
            ActivityCompat.requestPermissions(ConversationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

    }

    private void getCurrentLocation() {
        //Initialize Task Location

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("majdddddddddd");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                //When Success
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

    }

    private void alertDeleteDialog() {

        for (ChatMessage message : selectedMessage) {
            if (!message.isMe()) {
                isAllMessgeMe = false;
                break;
            } else {
                isAllMessgeMe = true;

            }

        }
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//        dialog.setMessage(getString(R.string.alert_delete_message));
        dialog.setTitle(R.string.alert_delete_message);
        dialog.setPositiveButton(R.string.delete_for_me,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        deleteMessage();
                        Toast.makeText(getApplicationContext(), "Yes is clicked", Toast.LENGTH_LONG).show();
                    }
                });
        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "cancel is clicked", Toast.LENGTH_LONG).show();
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
//                                deleteMessage(deleteMessage);
                    selectedMessage.clear();
                    deleteMessage.clear();
//                System.out.println(chatMessage.getMessage()+chatMessage.getId());
//                adapter.notifyDataSetChanged();
                    toolsLiner.setVisibility(View.GONE);
                    personInformationLiner.setVisibility(View.VISIBLE);
                    toolbar.setBackgroundColor(getResources().getColor(R.color.green_500));


                }
            });
        }
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    private class DownloadFileFromSocket extends AsyncTask<String, Void, Void> {
        ChatMessage chatMessage;

        public DownloadFileFromSocket(ChatMessage chatMessage) {
            this.chatMessage = chatMessage;
        }

        @Override
        protected Void doInBackground(String... strings) {


            if (!hasPermissions(ConversationActivity.this, PERMISSIONS)) {

                Log.v(TAG, "download() Method DON'T HAVE PERMISSIONS ");

                Toast t = Toast.makeText(getApplicationContext(), "You don't have write access !", Toast.LENGTH_LONG);
                t.show();

            } else {
                switch (chatMessage.getType()) {
                    case "imageWeb":
                        File imageFile;
                        File d = ConversationActivity.this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive/video");

                        imageFile = new File(d, chatMessage.getImage().toString());
                        if (!imageFile.exists()) {
                            try {
                                imageFile.createNewFile();
                                Log.v(TAG, "doInBackground() file created" + imageFile);

                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.e(TAG, "doInBackground() error" + e.getMessage());
                                Log.e(TAG, "doInBackground() error" + e.getStackTrace());


                            }
                            FileDownloader.downloadFile(AllConstants.imageUrlInConversation + chatMessage.getImage().toString(), imageFile);
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

                        voiceFile = new File(dV, chatMessage.getMessage().toString());
                        if (!voiceFile.exists()) {
                            try {
                                voiceFile.createNewFile();
                                Log.v(TAG, "doInBackground() file created" + voiceFile);

                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.e(TAG, "doInBackground() error" + e.getMessage());
                                Log.e(TAG, "doInBackground() error" + e.getStackTrace());


                            }
//                            FileDownloader.downloadFile(AllConstants.download_url+"audio/"+chatMessage.getMessage().toString(), voiceFile);
                            FileDownloader.downloadFile(AllConstants.download_url + chatMessage.getMessage().toString(), voiceFile);

                            Log.v(TAG, "doInBackground() file download completed");


                        } else {
                            Log.v(TAG, "File already download ");

                        }

                        break;
                    case "video":
                        File videoFile;
                        System.out.println("image weeeeeeeeeeeeeeeeeeeeeeeb");
                        File dVideo = ConversationActivity.this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive/video");

                        videoFile = new File(dVideo, chatMessage.getMessage().toString());
                        if (!videoFile.exists()) {
                            try {
                                videoFile.createNewFile();
                                Log.v(TAG, "doInBackground() file created" + videoFile);

                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.e(TAG, "doInBackground() error" + e.getMessage());
                                Log.e(TAG, "doInBackground() error" + e.getStackTrace());


                            }
//                            FileDownloader.downloadFile(AllConstants.download_url+"video/"+chatMessage.getMessage().toString(), videoFile);
                            FileDownloader.downloadFile(AllConstants.download_url + chatMessage.getMessage().toString(), videoFile);

                            Log.v(TAG, "doInBackground() file download completed");


                        } else {
                            Log.v(TAG, "File already download ");

                        }

                        break;
                    case "file":
                        File pdfFile;


                        if (!hasPermissions(ConversationActivity.this, PERMISSIONS)) {


                            Toast t = Toast.makeText(getApplicationContext(), "You don't have write access !", Toast.LENGTH_LONG);
                            t.show();

                        } else {

                            Log.v(TAG, "download() Method HAVE PERMISSIONS ");


                            File dFile = ConversationActivity.this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive");

                            pdfFile = new File(dFile, chatMessage.getMessage().toString());
                            if (!pdfFile.exists()) {
                                try {
                                    pdfFile.createNewFile();
                                    Log.v(TAG, "doInBackground() file created" + pdfFile);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Log.e(TAG, "doInBackground() error" + e.getMessage());
                                    Log.e(TAG, "doInBackground() error" + e.getStackTrace());


                                }
//                                    FileDownloader.downloadFile(AllConstants.download_url+"files/"+chatMessage.getMessage().toString(), pdfFile);
                                FileDownloader.downloadFile(AllConstants.download_url + chatMessage.getMessage().toString(), pdfFile);

                                Log.v(TAG, "doInBackground() file download completed");

//            new DownloadFile().execute("http://maven.apache.org/maven-1.x/maven.pdf", "maven.pdf");
//                                    new DownloadFile().execute(AllConstants.download_url + "files/" + chatMessage.getMessage().toString(), chatMessage.getMessage().toString(), "recive");
                            } else {
                                Log.v(TAG, "File already download ");

                            }
                        }


                        break;

                    default:
                        Toast t = Toast.makeText(getApplicationContext(), "dont support", Toast.LENGTH_LONG);
                        t.show();
                }


            }


            Log.v(TAG, "download() Method completed ");


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            System.out.println("data notifyyyyyyyyyyyyyyyyyy");
            displayMessage(chatMessage);
            adapter.notifyDataSetChanged();

        }
    }

    public void showPermissionDialog(String message, int RequestCode) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle(getResources().getString(R.string.permission_necessary));
        alertBuilder.setMessage(getResources().getString(R.string.contact_permission));
        alertBuilder.setMessage(message);

        alertBuilder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, RequestCode);
            }
        });

        AlertDialog alert = alertBuilder.create();
        alert.show();


    }

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

    private void setupWebView() {

//        webView.webChromeClient = object: WebChromeClient() {
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                request.grant(request.getResources());

            }

//            override fun onPermissionRequest(request: PermissionRequest?) {
//                request?.grant(request.resources)
//            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
//        webView.addJavascriptInterface(new JavascriptInterface(this), "Android");

        loadVideoCall();
    }

    private void loadVideoCall() {
        String filePath = "file:android_asset/call.html";
        System.out.println("load videooooooooooooooo" + filePath);
        webView.loadUrl(filePath);

//        webView.webViewClient = object: WebViewClient() {
//            override fun onPageFinished(view: WebView?, url: String?) {
//                initializePeer()
//            }
//        }
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                initializePeer();

            }
        });
    }

    private void initializePeer() {

        String uniqueId = getUniqueID();
//        System.out.println(uniqueId+"uniqueId");

//        callJavascriptFunction("javascript:init(\""+uniqueId+"\")");
//        firebaseRef.child(username).child("incoming").addValueEventListener(object: ValueEventListener {
//            override fun onCancelled(error: DatabaseError) {}
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//                println("value is "+snapshot.value)
//                onCallRequest(snapshot.value as? String)
//            }

//        })

    }

    private void switchToControls() {
//        inputLayout.setVisibility(View.GONE);
//        callControlLayout.setVisibility(View.VISIBLE);
    }

    private String getUniqueID() {
        return UUID.randomUUID().toString();
    }

    private void callJavascriptFunction(String functionString) {
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.evaluateJavascript(functionString, null);
            }
        });
    }

    public void onPeerConnected(String string) {
        System.out.println("the key is" + string);
        peerId = string;
//        isPeerConnected = true;
    }


}









