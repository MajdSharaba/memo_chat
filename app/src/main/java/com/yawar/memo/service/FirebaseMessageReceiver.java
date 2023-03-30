package com.yawar.memo.service;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.yawar.memo.BaseApp;
import com.yawar.memo.R;
import com.yawar.memo.repositry.ChatMessageRepoo;
import com.yawar.memo.repositry.ChatRoomRepoo;
//import com.yawar.memo.repositry.chatRoomRepo.ChatRoomRepoImp;
import com.yawar.memo.ui.responeCallPage.CallNotificationActivity;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.domain.model.ChatMessage;
import com.yawar.memo.domain.model.ChatRoomModel;
import com.yawar.memo.notification.NotificationCallWorker;
import com.yawar.memo.notification.NotificationMessingCallWorker;
import com.yawar.memo.notification.NotificationWorker;
//import com.yawar.memo.repositry.ChatRoomRepoo;
import com.yawar.memo.sessionManager.ClassSharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FirebaseMessageReceiver
        extends FirebaseMessagingService  {
    
    int id =1;
    String TAG = "FirebaseMessageReceiver";
    public static final String workCallTag = "notificationCallWork";
    public static final String workTag = "notificationWork";
    BaseApp myBase;
    @Inject
    ChatRoomRepoo chatRoomRepoo;
//    ServerApi serverApi;
    String chat_id;
    private WorkManager mWorkManager;
    ArrayList<String> muteList = new ArrayList<String>();
    ClassSharedPreferences classSharedPreferences;
    @Inject
    ChatMessageRepoo chatMessageRepoo;

    private final int NOTIFICATION_ID = 237;
//    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
    String GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL";
    // Override onMessageReceived() method to extract the
    // title and
    // body from the message passed in FCM
    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("WrongThread")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        mWorkManager = WorkManager.getInstance();
        wackLock();
        Log.i(TAG, "onMessageReceived: " + remoteMessage.getData() + "getPriority" + remoteMessage.getPriority() + remoteMessage.getData());
        myBase = (BaseApp) getApplication();

//        chatRoomRepoo = myBase.getChatRoomRepoo();
//        myBase.getObserver().addObserver(this);
        String message = "";
        classSharedPreferences = BaseApp.Companion.getInstance().getClassSharedPreferences();
        Map<String, String> data = remoteMessage.getData();
        String myCustomKey = data.get("body");

        if (remoteMessage.getData().size() > 0) {
            boolean isCall = false;
            System.out.println(remoteMessage.getData() + "getPriority" + remoteMessage.getPriority());


            switch (remoteMessage.getData().get("type")) {
                case "call":
                    isCall = true;
                    String callId = "";

                    JSONObject messagebody = null;
                    JSONObject userObject;
                    JSONObject typeObject;
                    String username = "";
                    String anthorUserCallId = "";
                    String image = "";
                    boolean isVideoCall = true;


                    try {
                        messagebody = new JSONObject(remoteMessage.getData().get("body"));
                        userObject = new JSONObject(messagebody.getString("user"));
                        typeObject = new JSONObject(messagebody.getString("type"));
                        isVideoCall = typeObject.getBoolean("video");
                        anthorUserCallId = messagebody.getString("snd_id");
                        callId = messagebody.getString("call_id");
                        username = userObject.getString("name");
                        image = userObject.getString("image_profile");

//
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int channel_id = Integer.parseInt(anthorUserCallId) + 10000;


                    Data inputData = new Data.Builder().putString("name", username).putString("image", image).putString("body", remoteMessage.getData().get("body")).putString("anthorUserCallId", anthorUserCallId).putString("channel", String.valueOf(channel_id)).putBoolean("isVideoCall", isVideoCall).putString("call_id", callId).build();


                    OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(NotificationCallWorker.class)
                            .setInputData(inputData)
                            .addTag(workCallTag)
                            .build();
                    WorkManager.getInstance().enqueue(notificationWork);


                    break;
                case "missingCall":


                    isCall = true;
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                    JSONObject messageMissingCall = null;
                    JSONObject userMessCallObject;
//                        JSONObject typeObject;
                    String userMissCallName = "";
                    String channelId = "";
                    String userMissCallimage = "";

                    try {
                        messageMissingCall = new JSONObject(Objects.requireNonNull(remoteMessage.getData().get("body")));
                        userMessCallObject = new JSONObject(messageMissingCall.getString("user"));
                        channelId = messageMissingCall.getString("snd_id");
                        userMissCallName = userMessCallObject.getString("name");
                        userMissCallimage = userMessCallObject.getString("image_profile");
                        notificationManager.cancel(-1);

                        Intent closeIntent = new Intent(CallNotificationActivity.ON_CLOSE_CALL_FROM_NOTIFICATION);
                        channel_id = Integer.parseInt(channelId);

                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(closeIntent);


                        Data inputDataNotification = new Data.Builder().putString("name", userMissCallName).putString("image", userMissCallimage).putString("body", getResources().getString(R.string.missing_call)).putString("channel", String.valueOf(channelId + AllConstants.CHANNEL_ID)).build();
                        if (myBase.getClassSharedPreferences() != null) {

                            myBase.getClassSharedPreferences().setNumberMissingCall(myBase.getClassSharedPreferences().getNumberMissingCall() + 1);

                        }
                        OneTimeWorkRequest notificationWork1 = new OneTimeWorkRequest.Builder(NotificationMessingCallWorker.class)
                                .setInputData(inputDataNotification)
                                .addTag("NotificationMessingCallWorker")
                                .build();
                        WorkManager.getInstance().enqueue(notificationWork1);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case "imageWeb":
                    message = getResources().getString(R.string.n_photo);
                    break;
                case "voice":
                    message = getResources().getString(R.string.n_voice);
                    break;
                case "video":
                    message = getResources().getString(R.string.n_video);
                    break;
                case "file":
                    message = getResources().getString(R.string.n_file);
                    break;
                case "contact":
                    message = getResources().getString(R.string.n_contact);
                    break;
                case "location":
                    message = getResources().getString(R.string.n_location);
                    break;

                default:
                    message = remoteMessage.getData().get("body");
            }


            if (!isCall) {
                muteList = classSharedPreferences.getMuteUsers();
                boolean isMute = false;
                if (muteList != null) {
                    for (String s :
                            muteList) {
                        if (s.equals(remoteMessage.getData().get("sender_id"))) {
                            isMute = true;
                            break;
                        }

                    }
                }
//                if (!chatRoomRepoo.checkInChat(remoteMessage.getData().get("sender_id")) && !isMute) {
                if (!isMute) {
                    System.out.println("not muteeeeeeeeeeee");


//                    new showNotification(this).execute(remoteMessage.getData().get("title"), remoteMessage.getData().get("image"), message, remoteMessage.getData().get("sender_id"));
                    Data inputDataNotification = new Data.Builder().putString("name", remoteMessage.getData().get("title")).putString("image", remoteMessage.getData().get("image"))
                            .putString("body", message).putString("channel", remoteMessage.getData().get("sender_id"))
                            .putString("blockedFor", remoteMessage.getData().get("blockedFor")).putString("special", remoteMessage.getData().get("special"))
                            .putString("chat_id", remoteMessage.getData().get("chat_id"))
                            .putString("fcm_token", remoteMessage.getData().get("my_token")).build();


                    sendMessageStateTwo(remoteMessage.getData().get("sender_id"), message, remoteMessage.getData().get("type"),
                            remoteMessage.getData().get("message_id"), remoteMessage.getData().get("dateTime"), remoteMessage.getData().get("chat_id"));


                    OneTimeWorkRequest notificationWork1 = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                            .setInputData(inputDataNotification)
                            .addTag(workTag)
                            .build();
                    WorkManager.getInstance().enqueue(notificationWork1);
                    chatRoomRepoo.setLastMessage(remoteMessage.getData().get("body"), remoteMessage.getData().get("chat_id"), remoteMessage.getData().get("sender_id"),
                            remoteMessage.getData().get("reciver_id"), remoteMessage.getData().get("type"),
                            remoteMessage.getData().get("state"), remoteMessage.getData().get("dateTime"),
                            remoteMessage.getData().get("sender_id"));
                    Log.d(TAG, "baseApp" + remoteMessage.getData().get("chat_id"));
                }

                    if (chatRoomRepoo.checkISNewChat( remoteMessage.getData().get("chat_id"))) {
                        chatRoomRepoo.addChatRoom(
                                new ChatRoomModel(
                                        remoteMessage.getData().get("title"),
                                        remoteMessage.getData().get("sender_id"),
                                        message,
                                        remoteMessage.getData().get("image"),
                                        false,
                                        "0",
                                        remoteMessage.getData().get("chat_id"),
                                        "null",
                                        "1",
                                        false,
                                        remoteMessage.getData().get("my_token"),
                                        "",
                                        remoteMessage.getData().get("type"),
                                        remoteMessage.getData().get("state"),
                                        remoteMessage.getData().get("dateTime"),
                                        false,
                                        "null",
                                        remoteMessage.getData().get("sender_id"),
                                        ""
                                )
                        );
                    }
//                    else if (chatRoomRepoo.checkInChat(remoteMessage.getData().get("sender_id"))) {
                    else  {


                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setMessageId(remoteMessage.getData().get("message_id"));

                        chatMessage.setMessage(remoteMessage.getData().get("body"));
                        if (remoteMessage.getData().get("type").equals("imageWeb")) {
                            chatMessage.setImage(remoteMessage.getData().get("body"));
                        }
                        chatMessage.setDateTime(remoteMessage.getData().get("dateTime"));
                        chatMessage.setMe(false);
                        chatMessage.setSenderId(remoteMessage.getData().get("sender_id"));
                        chatMessage.setType(remoteMessage.getData().get("type"));
                        chatMessage.setState(remoteMessage.getData().get("state"));
                        chatMessage.setChecked(false);
                        if (!remoteMessage.getData().get("type").equals("text") && !remoteMessage.getData().get("type").equals("location")) {
                            System.out.println("(remoteMessage.getData().get(\"type\")" + remoteMessage.getData().get("type"));
                            chatMessage.setFileName(remoteMessage.getData().get("orginalName"));
                        }
                        chatMessage.setUpdate("0");
//                        myBase.getChatMessageRepoo().addMessage(chatMessage);
                        chatMessageRepoo.addMessage(chatMessage);

                    }

//                        chatRoomRepoo.setLastMessage(remoteMessage.getData().get("body"), remoteMessage.getData().get("chat_id"), remoteMessage.getData().get("sender_id"),
//                                remoteMessage.getData().get("reciver_id"), remoteMessage.getData().get("type"),
//                                remoteMessage.getData().get("state"), remoteMessage.getData().get("dateTime"),
//                                remoteMessage.getData().get("sender_id"));
//                        Log.d(TAG, "baseApp" + myBase.isActivityVisible());

                    }

                }
            }





void wackLock(){
            PowerManager powerManager = (PowerManager) this.getSystemService(this.POWER_SERVICE);
        PowerManager.WakeLock  wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, "appname::WakeLock");
            wakeLock.acquire(1*60*1000L);


}



    public void sendMessageStateTwo ( String anthorUserId, String message, String typeMessage,
    String messageId , String dateTime, String chatId ) {
        BaseApp myBase = BaseApp.Companion.getInstance();
        ClassSharedPreferences classSharedPreferences = BaseApp.Companion.getInstance().getClassSharedPreferences();

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(FirebaseMessageReceiver.this);
        // on below line we are calling a string
        // request method to post the data to our API
        // in this we are calling a post method.
        StringRequest request = new StringRequest(Request.Method.POST, AllConstants.base_url_final+"change_state", new com.android.volley.Response.Listener<String>() {
//        StringRequest request = new StringRequest(Request.Method.POST, AllConstants.base_node_url+"change_state", new com.android.volley.Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                System.out.println("responseeee"+response);

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("erroreeee"+error);

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // below line we are creating a map for
                // storing our values in key and value pair.
                Map<String, String> params = new HashMap<String, String>();

                // on below line we are passing our key
                // and value pair to our parameters.
                params.put("my_id", classSharedPreferences.getUser().getUserId());
                params.put("your_id", anthorUserId);
                params.put("message", message);
                params.put("message_type", typeMessage);
                params.put("state", "2");
                params.put("message_id", messageId);
                params.put("sender_id", anthorUserId);
                params.put("reciver_id",classSharedPreferences.getUser().getUserId());
                params.put("chat_id",chatId);
                params.put("dateTime",dateTime);

                return params;
            }
        };
        // below line is to make
        // a json object request.
        myBase.addToRequestQueue(request);
    }
}

