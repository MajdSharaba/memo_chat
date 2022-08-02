package com.yawar.memo.service;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
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
import com.yawar.memo.repositry.ChatRoomRepo;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.utils.BaseApp;
import com.yawar.memo.utils.ImageProperties;
import com.yawar.memo.views.SplashScreen;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

public class FirebaseMessageReceiver
        extends FirebaseMessagingService  {

    int id =1;
    String TAG = "FirebaseMessageReceiver";
    public static final String workCallTag = "notificationCallWork";
    public static final String workTag = "notificationWork";

    BaseApp myBase;
    ChatRoomRepo chatRoomRepo;
    ServerApi serverApi;
    String chat_id;
    private WorkManager mWorkManager;

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
        Log.i(TAG, "onMessageReceived: "+ remoteMessage.getOriginalPriority()+"getPriority"+remoteMessage.getPriority()+remoteMessage.getData());
        myBase = (BaseApp) getApplication();

        chatRoomRepo=myBase.getChatRoomRepo();
//        myBase.getObserver().addObserver(this);
        String message = "";
        classSharedPreferences = new ClassSharedPreferences(this);
        Map<String, String> data = remoteMessage.getData();
        String myCustomKey = data.get("body");
        // First case when notifications are received via
        // data event
        // Here, 'title' and 'message' are the assumed names
        // of JSON
        // attributes. Since here we do not have any data
        // payload, This section is commented out. It is
        // here only for reference purposes.


        // Second case when notification payload is
        // received.
        if(remoteMessage.getData().size()>0){
            boolean isCall = false;
            System.out.println(remoteMessage.getPriority()+"getPriority"+remoteMessage.getPriority());


            // Since the notification is received directly from
            // FCM, the title and the body can be fetched
            // directly as below.

                switch (remoteMessage.getData().get("type")) {
                    case "call":
                        isCall = true;
                        JSONObject messagebody = null;
                        JSONObject userObject;
                        JSONObject typeObject;
                        String username="";
                        String anthorUserCallId="";
                        String image ="";
                        boolean isVideoCall = true;

                        try {
                            messagebody = new JSONObject(remoteMessage.getData().get("body"));
                            userObject = new JSONObject(messagebody.getString("user"));
                            typeObject = new JSONObject(messagebody.getString("type"));
                            isVideoCall = typeObject.getBoolean("video");
                            anthorUserCallId = messagebody.getString("snd_id");
                            username = userObject.getString("name");
                            image = userObject.getString("image_profile");

//
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        int channel_id=Integer.parseInt(anthorUserCallId)+10000;


                        Data inputData = new Data.Builder().putString("name",username).putString("image",image).putString("body",remoteMessage.getData().get("body")).putString("anthorUserCallId", anthorUserCallId).putString("channel", String.valueOf(channel_id)).putBoolean("isVideoCall",isVideoCall).build();


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
                        String userMissCallName="";
                        String channelId="";
                        String userMissCallimage ="";

                        try {
                            messageMissingCall = new JSONObject(Objects.requireNonNull(remoteMessage.getData().get("body")));
                            userMessCallObject = new JSONObject(messageMissingCall.getString("user"));
                            channelId = messageMissingCall.getString("snd_id");
                            userMissCallName = userMessCallObject.getString("name");
                            userMissCallimage = userMessCallObject.getString("image_profile");


//                            notificationManager.cancel(Integer.parseInt(channelId)+10000);
                            notificationManager.cancel(-1);

                            Intent closeIntent = new Intent(CallNotificationActivity.ON_CLOSE_CALL_FROM_NOTIFICATION);
//                            int channel_id=Integer.parseInt(channelId)+10000;

                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(closeIntent);
//                            new showNotification(this).execute(userMissCallName, userMissCallimage, getResources().getString(R.string.missing_call), String.valueOf(channelId));

                            Data inputDataNotification = new Data.Builder().putString("name",userMissCallName).putString("image",userMissCallimage).putString("body",getResources().getString(R.string.missing_call)).putString("channel", String.valueOf(channelId)).build();


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
                        message = getResources().getString(R.string.photo);
                        break;
                    case "voice":
                        message = getResources().getString(R.string.voice);
                        break;
                    case "video":
                        message = getResources().getString(R.string.video);
                        break;
                    case "file":
                        message = getResources().getString(R.string.file);
                        break;
                    case "contact":
                        message = getResources().getString(R.string.contact_number);
                        break;
                    case "location":
                        message = getResources().getString(R.string.location);
                        break;

                    default:
                        message = remoteMessage.getData().get("body");
                }


                 if(!isCall)

                if (!chatRoomRepo.checkInChat(remoteMessage.getData().get("sender_id"))) {

//                    new showNotification(this).execute(remoteMessage.getData().get("title"), remoteMessage.getData().get("image"), message, remoteMessage.getData().get("sender_id"));
                    Data inputDataNotification = new Data.Builder().putString("name",remoteMessage.getData().get("title")).putString("image",remoteMessage.getData().get("image")).putString("body",message).putString("channel", remoteMessage.getData().get("sender_id")).build();


                    OneTimeWorkRequest notificationWork1 = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                            .setInputData(inputDataNotification)
                            .addTag(workTag)
                            .build();
                    WorkManager.getInstance().enqueue(notificationWork1);
                }

        }}





//    private class showNotification extends AsyncTask<String, Void, Bitmap> {
//
//        Context ctx;
//        String message;
//        String title;
//        String sender_id;
//
//        public showNotification(Context context) {
//            super();
//            this.ctx = context;
//        }
//
//        @Override
//        protected Bitmap doInBackground(String... params) {
//
//            InputStream in;
//           message = params[2] ;
//           title = params[0];
//           sender_id = params[3];
//
//            try {
//                if(!params[1].equals("")){
//                URL url = new URL(AllConstants.imageUrl+params[1]);
//                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
//                connection.setDoInput(true);
//                connection.connect();
//                in = connection.getInputStream();
//                Bitmap myBitmap = BitmapFactory.decodeStream(in);
//                return myBitmap;}
//                else {
//                    return null;
//                }
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap result) {
//            Bitmap bitmap;
//
//            super.onPostExecute(result);
//            try {
//               if(result==null){
//                   bitmap = BitmapFactory.decodeResource(FirebaseMessageReceiver.this.getResources(),
//                           R.drawable.th);
//                   System.out.println("this null");
//               }
//               else {
//                   bitmap= result;
//               }
//                Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.incomingcall);
//                Intent intent
//                        = new Intent(FirebaseMessageReceiver.this, SplashScreen.class);
//                // Assign channel ID
//                String channel_id = "notification_channel";
//                // Here FLAG_ACTIVITY_CLEAR_TOP flag is set to clear
//                // the activities present in the activity stack,
//                // on the top of the Activity that is to be launched
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                // Pass the intent to PendingIntent to start the
//                // next Activity
//                PendingIntent pendingIntent
//                        = PendingIntent.getActivity(FirebaseMessageReceiver.this
//                        , 0, intent,
//                        PendingIntent.FLAG_ONE_SHOT|PendingIntent.FLAG_IMMUTABLE);
//
//                // Create a Builder object using NotificationCompat
//                // class. This will allow control over all the flags
//                NotificationCompat.Builder builder
//                        = new NotificationCompat
//                        .Builder(getApplicationContext(),
//                        channel_id)
//                        .setNumber(id++)
//                        .setPriority(NotificationCompat.PRIORITY_MAX).
//                         setContentTitle(title)
//                        .setContentText(message)
//                        .setLargeIcon(ImageProperties.getCircleBitmap(bitmap))
//
//
//
//
//                        .setSmallIcon(R.drawable.ic_memo_logo)
//                        .setAutoCancel(false)
//                        .setSound((RingtoneManager. getDefaultUri (RingtoneManager. TYPE_NOTIFICATION)))
//                        .setGroup(GROUP_KEY_WORK_EMAIL)
//
//
//
//
//                        //specify which group this notification belongs to
//                        //set this notification as the summary for the group
//                        .setVibrate(new long[]{1000, 1000, 1000,
//                                1000, 1000})
////                        .setOnlyAlertOnce(true)
//                        .setContentIntent(pendingIntent)
//                        .setGroupSummary(true);
//
//                NotificationManager notificationManager
//                        = (NotificationManager) getSystemService(
//                        Context.NOTIFICATION_SERVICE);
//                // Check if the Android Version is greater than Oreo
//                if (Build.VERSION.SDK_INT
//                        >= Build.VERSION_CODES.O) {
//                    NotificationChannel notificationChannel
//                            = new NotificationChannel(
//                            channel_id, "Memo",
//
//                            NotificationManager.IMPORTANCE_HIGH);
//                    notificationManager.createNotificationChannel(
//                            notificationChannel);
//                    notificationManager.createNotificationChannelGroup(new NotificationChannelGroup(channel_id, "Memo"));
//
//                }
//
//
//                notificationManager.notify(Integer.parseInt(sender_id), builder.build());
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }



void wackLock(){
            PowerManager powerManager = (PowerManager) this.getSystemService(this.POWER_SERVICE);
        PowerManager.WakeLock  wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, "appname::WakeLock");
            wakeLock.acquire(1*60*1000L);

}
}

