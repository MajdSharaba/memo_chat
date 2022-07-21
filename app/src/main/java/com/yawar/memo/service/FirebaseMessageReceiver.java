package com.yawar.memo.service;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Person;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.yawar.memo.Api.ServerApi;
import com.yawar.memo.R;
import com.yawar.memo.call.CallMainActivity;
import com.yawar.memo.call.CallNotificationActivity;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.repositry.ChatRoomRepo;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.utils.BaseApp;
import com.yawar.memo.utils.ImageProperties;
//import com.yawar.memo.videocalltest.ConnService;
import com.yawar.memo.views.SplashScreen;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class FirebaseMessageReceiver
        extends FirebaseMessagingService implements Observer {


    int id =1;
    String TAG = "FirebaseMessageReceiver";
    BaseApp myBase;
    ChatRoomRepo chatRoomRepo;
    ServerApi serverApi;
    String chat_id;
    ClassSharedPreferences classSharedPreferences;

    private final int NOTIFICATION_ID = 237;
//    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
    String GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL";




    // Override onMessageReceived() method to extract the
    // title and
    // body from the message passed in FCM
    @SuppressLint("WrongThread")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        wackLock();
        Log.i(TAG, "onMessageReceived: "+ remoteMessage.getOriginalPriority()+"getPriority"+remoteMessage.getPriority());
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

                        try {
                            messagebody = new JSONObject(remoteMessage.getData().get("body"));
                            userObject = new JSONObject(messagebody.getString("user"));
                            typeObject = new JSONObject(messagebody.getString("type"));
                            anthorUserCallId = messagebody.getString("snd_id");
//            isVideo = typeObject.getBoolean("video");
                            username = userObject.getString("name");
                            image = userObject.getString("image_profile");
//            anotherUserId = message.getString("snd_id");
//
//
//
//
//
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        callRecived(classSharedPreferences.getUser().getUserId(),anthorUserCallId);
//                                                try {
//                            Bundle callInfo = new Bundle();
//                            PhoneAccountHandle phoneAccountHandle;
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                ComponentName componentName = new ComponentName(this, ConnService.class);
//
//                                phoneAccountHandle = new PhoneAccountHandle(componentName, "com.darkhorse.videocalltest");
//
//
//                                callInfo.putString("from", "test");
//                                callInfo.putString("callRequest",remoteMessage.getData().get("body")) ;
//
//                                TelecomManager telecomManager = (TelecomManager) this.getSystemService(Context.TELECOM_SERVICE);
//
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                    telecomManager.addNewIncomingCall(phoneAccountHandle, callInfo);
//                                }
//                            }} catch(Exception e){
//                                Log.e("main activity incoming", e.toString());
//                            }


//                showNotification( username, image, remoteMessage.getData().get("body"));
                        new showCallNotification(this).execute(username,image,remoteMessage.getData().get("body"),anthorUserCallId);

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
                            messageMissingCall = new JSONObject(remoteMessage.getData().get("body"));
                            userMessCallObject = new JSONObject(messageMissingCall.getString("user"));
                            channelId = messageMissingCall.getString("snd_id");
                            userMissCallName = userMessCallObject.getString("name");
                            userMissCallimage = userMessCallObject.getString("image_profile");


                            notificationManager.cancel(Integer.parseInt(channelId));
                            Intent closeIntent = new Intent(CallNotificationActivity.ON_CLOSE_CALL_FROM_NOTIFICATION);

                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(closeIntent);
                            new showNotification(this).execute(userMissCallName, userMissCallimage, getResources().getString(R.string.missing_call), channelId);



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

                    new showNotification(this).execute(remoteMessage.getData().get("title"), remoteMessage.getData().get("image"), message, remoteMessage.getData().get("sender_id"));
                }

        }}


    // Method to get the custom Design for the display of
    // notification.
    private RemoteViews getCustomDesign(String anthor_user_id,String title,
                                        String message,Bitmap image) {
        @SuppressLint("RemoteViewLayout") RemoteViews remoteViews = new RemoteViews(
                getApplicationContext().getPackageName(),
                R.layout.notification);
        remoteViews.setTextViewText(R.id.callType, title);
        remoteViews.setTextViewText(R.id.message, message);


        Intent intent
                = new Intent(this, CallMainActivity.class);
        System.out.println("message in getCustomDesign is"+message);
        intent.putExtra("callRequest",message );
        intent.putExtra("id",anthor_user_id);

        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
//                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_IMMUTABLE);



        remoteViews.setImageViewBitmap(R.id.icon, image);

        remoteViews.setOnClickPendingIntent(R.id.btnAnswer,pendingIntent);

        ///////////////////
        Intent intentCancel
                = new Intent(this, NotificationReceiver.class);

        intentCancel.putExtra("callRequest",message );
        intentCancel.putExtra("id",anthor_user_id);

        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntentCancell = PendingIntent.getBroadcast(this, 0,
//                intentCancel,   PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntentCancell = PendingIntent.getBroadcast(this, 0,
                intentCancel,   PendingIntent.FLAG_IMMUTABLE);

        remoteViews.setOnClickPendingIntent(R.id.btnDecline,pendingIntentCancell);
//        remoteViews.setO(R.id.btnDecline,sendPeerId(message,"null"));


        return remoteViews;
    }

    // Method to display the notifications
    public void showNotification(String title,String image,
                                 String message) {
        Person person;

        // Pass the intent to switch to the MainActivity
        Intent intent
                = new Intent(this, CallNotificationActivity.class);
        intent.putExtra("callRequest",message );


        String channel_id = "notification_channel";

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder
                = new NotificationCompat
                .Builder(getApplicationContext(),
                channel_id)
//                .setNumber(id++)
                .setPriority(NotificationCompat.PRIORITY_MAX).
                        setContentTitle(title)
                .setContentText(message)

                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setFullScreenIntent(pendingIntent, true)


                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setTimeoutAfter(10000)
                .setSmallIcon(R.drawable.ic_memo_logo);
//                .setCustomContentView(getCustomDesign(title, message,image));






//                .setContentIntent(pendingIntent);
//
//        builder = builder.setContent(
//                getCustomDesign(title, message,image));


        NotificationManager notificationManager
                = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        // Check if the Android Version is greater than Oreo
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = new NotificationChannel(
                    channel_id, "Memo",

                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            notificationManager.createNotificationChannel(
                    notificationChannel);
            notificationManager.createNotificationChannelGroup(new NotificationChannelGroup(channel_id, "Memo"));

        }
        notificationManager.notify(0,builder.build());



    }

    @Override
    public void update(Observable observable, Object o) {

    }

    private class showNotification extends AsyncTask<String, Void, Bitmap> {

        Context ctx;
        String message;
        String title;
        String sender_id;

        public showNotification(Context context) {
            super();
            this.ctx = context;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            InputStream in;
           message = params[2] ;
           title = params[0];
           sender_id = params[3];

            try {
                if(!params[1].equals("")){
                URL url = new URL(AllConstants.imageUrl+params[1]);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;}
                else {
                    return null;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            Bitmap bitmap;

            super.onPostExecute(result);
            try {
               if(result==null){
                   bitmap = BitmapFactory.decodeResource(FirebaseMessageReceiver.this.getResources(),
                           R.drawable.th);
                   System.out.println("this null");
               }
               else {
                   bitmap= result;
               }
                Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.incomingcall);
                Intent intent
                        = new Intent(FirebaseMessageReceiver.this, SplashScreen.class);
                // Assign channel ID
                String channel_id = "notification_channel";
                // Here FLAG_ACTIVITY_CLEAR_TOP flag is set to clear
                // the activities present in the activity stack,
                // on the top of the Activity that is to be launched
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // Pass the intent to PendingIntent to start the
                // next Activity
                PendingIntent pendingIntent
                        = PendingIntent.getActivity(FirebaseMessageReceiver.this
                        , 0, intent,
                        PendingIntent.FLAG_ONE_SHOT);

                // Create a Builder object using NotificationCompat
                // class. This will allow control over all the flags
                NotificationCompat.Builder builder
                        = new NotificationCompat
                        .Builder(getApplicationContext(),
                        channel_id)
                        .setNumber(id++)
                        .setPriority(NotificationCompat.PRIORITY_MAX).
                         setContentTitle(title)
                        .setContentText(message)
                        .setLargeIcon(ImageProperties.getCircleBitmap(bitmap))




                        .setSmallIcon(R.drawable.ic_memo_logo)
                        .setAutoCancel(false)
                        .setSound((RingtoneManager. getDefaultUri (RingtoneManager. TYPE_NOTIFICATION)))
                        .setGroup(GROUP_KEY_WORK_EMAIL)




                        //specify which group this notification belongs to
                        //set this notification as the summary for the group
                        .setVibrate(new long[]{1000, 1000, 1000,
                                1000, 1000})
//                        .setOnlyAlertOnce(true)
                        .setContentIntent(pendingIntent)
                        .setGroupSummary(true);




                // A customized design for the notification can be
                // set only for Android versions 4.1 and above. Thus
                // condition for the same is checked here.
//        if (Build.VERSION.SDK_INT
//                >= Build.VERSION_CODES.JELLY_BEAN) {
//            System.out.println("");}
//            Bitmap bitmap = null;
//                bitmap = CircleImage.getCircleBitmap(result);
//
//            builder = builder.setContent(
//                    getCustomDesign(message, message, bitmap)
//            );
//        } // If Android Version is lower than Jelly Beans,
//        // customized layout cannot be used and thus the
//        // layout is set as follows
//        else {
//            builder = builder.setContentTitle(message)
//                    .setContentText(message)
//
//                    .setSmallIcon(R.drawable.ic_launcher_foreground);
//
//        }
                // Create an object of NotificationManager class to
                // notify the
                // user of events that happen in the background.
//                            builder = builder.setContent(
//                    getCustomDesign(message, message));
                NotificationManager notificationManager
                        = (NotificationManager) getSystemService(
                        Context.NOTIFICATION_SERVICE);
                // Check if the Android Version is greater than Oreo
                if (Build.VERSION.SDK_INT
                        >= Build.VERSION_CODES.O) {
                    NotificationChannel notificationChannel
                            = new NotificationChannel(
                            channel_id, "Memo",

                            NotificationManager.IMPORTANCE_HIGH);
                    notificationManager.createNotificationChannel(
                            notificationChannel);
                    notificationManager.createNotificationChannelGroup(new NotificationChannelGroup(channel_id, "Memo"));

                }


                notificationManager.notify(Integer.parseInt(sender_id), builder.build());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private class showCallNotification extends AsyncTask<String, Void, Bitmap> {

        Context ctx;
        String message;
        String title;
        String anthor_user_id;

        public showCallNotification(Context context) {
            super();
            this.ctx = context;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            InputStream in;
            message = params[2] ;
            System.out.println("message message"+message);
            title = params[0];
           anthor_user_id = params[3];

            try {
                if(!params[1].equals("")){
                    URL url = new URL(AllConstants.imageUrl+params[1]);
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    in = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(in);
                    return myBitmap;}
                else {
                    return null;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            Bitmap bitmap;

            super.onPostExecute(result);
            try {
                if(result==null){
                    bitmap = BitmapFactory.decodeResource(FirebaseMessageReceiver.this.getResources(),
                            R.drawable.th);
                    System.out.println("this null");
                }
                else {
                    bitmap= result;
                }
                /////////intent for lock screen
                Intent intent
                        = new Intent(FirebaseMessageReceiver.this, CallNotificationActivity.class);
                intent.putExtra("callRequest",message);
                intent.putExtra("id",anthor_user_id);



                String channelCall = "call_channel";

//                PendingIntent pendingIntent = PendingIntent.getActivity(FirebaseMessageReceiver.this, 0,
//                        intent, PendingIntent.FLAG_CANCEL_CURRENT);
                PendingIntent pendingIntent = PendingIntent.getActivity(FirebaseMessageReceiver.this, 0,
                        intent, PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_CANCEL_CURRENT);
                /////////intent for answare
                Intent intentAnsware
                        = new Intent(FirebaseMessageReceiver.this, CallMainActivity.class);
                intentAnsware.putExtra("callRequest",message);
                intentAnsware.putExtra("id",anthor_user_id);

                intentAnsware.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                PendingIntent answarePendingIntent = PendingIntent.getActivity(FirebaseMessageReceiver.this, 0,
//                        intentAnsware, PendingIntent.FLAG_UPDATE_CURRENT);
                PendingIntent answarePendingIntent = PendingIntent.getActivity(FirebaseMessageReceiver.this, 0,
                        intentAnsware, PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);

                /////////intent for reject
                Intent intentCancel
                        = new Intent(FirebaseMessageReceiver.this, NotificationReceiver.class);

                intentCancel.putExtra("callRequest",message );
                intentCancel.putExtra("id",anthor_user_id);

                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                PendingIntent pendingIntentCancell = PendingIntent.getBroadcast(FirebaseMessageReceiver.this, 0,
//                        intentCancel,   PendingIntent.FLAG_UPDATE_CURRENT);
                PendingIntent pendingIntentCancell = PendingIntent.getBroadcast(FirebaseMessageReceiver.this, 0,
                        intentCancel,   PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);
                /////////////////

//                Uri alarmSound =
//                        RingtoneManager. getDefaultUri (RingtoneManager.TYPE_RINGTONE );

//                MediaPlayer mp = MediaPlayer.create (getApplicationContext(), alarmSound);
//                mp.start();
                NotificationCompat.Builder builder
                        = new NotificationCompat
                        .Builder(getApplicationContext(),
                        channelCall)
                        .setPriority(NotificationCompat.PRIORITY_MAX)


//                        .setLargeIcon(ImageProperties.getCircleBitmap(bitmap))


//                        .setContentText(message)
                        .setFullScreenIntent(pendingIntent, true)

                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setCategory(NotificationCompat.CATEGORY_CALL)
//                        .setTimeoutAfter(10000)
                        .setSound(null)
                        .setOngoing(true)
                        .setColor(ContextCompat.getColor(FirebaseMessageReceiver.this, R.color.green))



                        .setVibrate(new long[] { 10000, 10000})
                        .setTicker("Call_STATUS")

                        .setColorized(true)
                        .setSmallIcon(R.drawable.ic_memo_logo);
//                if (Build.VERSION.SDK_INT
//                        > Build.VERSION_CODES.Q) {
//                    builder.setCustomContentView(getCustomDesign(anthor_user_id,title, message,ImageProperties.getCircleBitmap(bitmap)));
//                    builder.setCustomBigContentView(getCustomDesign(anthor_user_id,title, message,ImageProperties.getCircleBitmap(bitmap)));
//                    builder.setCustomHeadsUpContentView(getCustomDesign(anthor_user_id,title, message,ImageProperties.getCircleBitmap(bitmap)));
//
//                }
               /// else {
                    builder.addAction(R.drawable.btx_custom, "Receive Call", answarePendingIntent);
                    builder.setLargeIcon(ImageProperties.getCircleBitmap(bitmap));
                    builder.addAction(R.drawable.btx_custom, "Cancel call", pendingIntentCancell);


                    builder.setContentTitle(getResources().getString(R.string.call_from));
                    builder.setContentText(title);
                //}


                NotificationManager notificationManager
                        = (NotificationManager) getSystemService(
                        Context.NOTIFICATION_SERVICE);
                // Check if the Android Version is greater than Oreo
                if (Build.VERSION.SDK_INT
                        >= Build.VERSION_CODES.O) {
                    NotificationChannel notificationChannel
                            = new NotificationChannel(
                            channelCall, "call Channel",

                            NotificationManager.IMPORTANCE_HIGH);
                    notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

//                    notificationChannel.setLightColor(Color.GREEN);
//                    notificationChannel.enableLights(true);
//                      notificationChannel.enableVibration(true);
                    notificationChannel.setDescription("Call Notifications");
                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)

                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setLegacyStreamType(AudioManager.STREAM_RING)
                            .build();
//                    notificationChannel.setSound(alarmSound, null);
                    notificationChannel.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.incomingcall),audioAttributes);


                    notificationManager.createNotificationChannel(
                            notificationChannel);
//                    notificationManager.createNotificationChannelGroup(new NotificationChannelGroup(channel_id, "Memo"));

                }
                Notification note = builder.build();
                note.flags |= Notification.FLAG_INSISTENT;
//                note.flags |= Notification.FLAG_NO_CLEAR;

                notificationManager.notify(Integer.parseInt(anthor_user_id),note);
                isRining(anthor_user_id);

////////////////////////////////
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
//    private void callRecived(String my_id,String anthor_user_id) {
//
//        Intent service = new Intent(this, SocketIOService.class);
//        JSONObject userEnter = new JSONObject();
//        System.out.println("call Recived");
//
//        try {
//            userEnter.put("my_id", my_id);
//            userEnter.put("your_id", anthor_user_id);
//            userEnter.put("state", "true");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        service.putExtra(SocketIOService.EXTRA_RECIVED_CALL_PARAMTERS, userEnter.toString());
//        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_RECIVED_CALL);
//        startService(service);
//    }
public void isRining(String yout_id) {

    // creating a new variable for our request queue
    RequestQueue queue = Volley.newRequestQueue(FirebaseMessageReceiver.this);
    // on below line we are calling a string
    // request method to post the data to our API
    // in this we are calling a post method.
    StringRequest request = new StringRequest(Request.Method.POST, AllConstants.base_node_url+"ringing", new com.android.volley.Response.Listener<String>() {
        @Override
        public void onResponse(String response) {

        }
    }, new com.android.volley.Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
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
            params.put("your_id", yout_id);
            params.put("state", "true");

            // at last we are
            // returning our params.
            return params;
        }
    };
    // below line is to make
    // a json object request.
    myBase.addToRequestQueue(request);
}
void wackLock(){
            PowerManager powerManager = (PowerManager) this.getSystemService(this.POWER_SERVICE);
        PowerManager.WakeLock  wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, "appname::WakeLock");
            wakeLock.acquire(1*60*1000L);

}
}

