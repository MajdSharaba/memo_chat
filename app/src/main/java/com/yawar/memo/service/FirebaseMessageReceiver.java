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

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.yawar.memo.Api.ServerApi;
import com.yawar.memo.R;
import com.yawar.memo.call.CallNotificationActivity;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.notification.NotificationCallWorker;
import com.yawar.memo.notification.NotificationWorker;
import com.yawar.memo.repositry.ChatRoomRepoo;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.utils.BaseApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class FirebaseMessageReceiver
        extends FirebaseMessagingService  {

    int id =1;
    String TAG = "FirebaseMessageReceiver";
    public static final String workCallTag = "notificationCallWork";
    public static final String workTag = "notificationWork";

    BaseApp myBase;
    ChatRoomRepoo chatRoomRepoo;
    ServerApi serverApi;
    String chat_id;
    private WorkManager mWorkManager;
    ArrayList<String> muteList = new ArrayList<String>();


    ClassSharedPreferences classSharedPreferences;

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
        Log.i(TAG, "onMessageReceived: " + remoteMessage.getOriginalPriority() + "getPriority" + remoteMessage.getPriority() + remoteMessage.getData());
        myBase = (BaseApp) getApplication();

        chatRoomRepoo = myBase.getChatRoomRepoo();
//        myBase.getObserver().addObserver(this);
        String message = "";
        classSharedPreferences = BaseApp.getInstance().getClassSharedPreferences();
        Map<String, String> data = remoteMessage.getData();
        String myCustomKey = data.get("body");

        if (remoteMessage.getData().size() > 0) {
            boolean isCall = false;
            System.out.println(remoteMessage.getPriority() + "getPriority" + remoteMessage.getPriority());


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


//                            notificationManager.cancel(Integer.parseInt(channelId)+10000);
                        notificationManager.cancel(-1);

                        Intent closeIntent = new Intent(CallNotificationActivity.ON_CLOSE_CALL_FROM_NOTIFICATION);
                        channel_id = Integer.parseInt(channelId);

                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(closeIntent);
//                            new showNotification(this).execute(userMissCallName, userMissCallimage, getResources().getString(R.string.missing_call), String.valueOf(channelId));

                        Data inputDataNotification = new Data.Builder().putString("name", userMissCallName).putString("image", userMissCallimage).putString("body", getResources().getString(R.string.missing_call)).putString("channel", String.valueOf(channelId + AllConstants.CHANNEL_ID)).build();


                        OneTimeWorkRequest notificationWork1 = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                                .setInputData(inputDataNotification)
                                .addTag(workTag)
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
                if (!chatRoomRepoo.checkInChat(remoteMessage.getData().get("sender_id")) && !isMute) {

//                    new showNotification(this).execute(remoteMessage.getData().get("title"), remoteMessage.getData().get("image"), message, remoteMessage.getData().get("sender_id"));
                    Data inputDataNotification = new Data.Builder().putString("name", remoteMessage.getData().get("title")).putString("image", remoteMessage.getData().get("image"))
                            .putString("body", message).putString("channel", remoteMessage.getData().get("sender_id"))
                            .putString("blockedFor", remoteMessage.getData().get("blockedFor")).putString("special", remoteMessage.getData().get("special"))
                            .putString("fcm_token", remoteMessage.getData().get("fcm_token")).build();


                    OneTimeWorkRequest notificationWork1 = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                            .setInputData(inputDataNotification)
                            .addTag(workTag)
                            .build();
                    WorkManager.getInstance().enqueue(notificationWork1);
                }

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
}

