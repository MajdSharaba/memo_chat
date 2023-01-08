package com.yawar.memo.notification;

import static android.content.Context.NOTIFICATION_SERVICE;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yawar.memo.R;
import com.yawar.memo.ui.responeCallPage.ResponeCallActivity;
import com.yawar.memo.ui.responeCallPage.CallNotificationActivity;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.utils.BaseApp;
import com.yawar.memo.utils.ImageProperties;

import java.util.HashMap;
import java.util.Map;


public class NotificationCallWorker extends Worker {

    public static final String WORK_NAME ="NotificationWorker" ;
    private static final String TAG ="NotificationCallWorker" ;
    String call_ongoing_call_user_id;

    /**
     * Creates an instance of the {@link Worker}.
     *
     * @param appContext   the application {@link Context}
     * @param workerParams the set of {@link WorkerParameters}
     */
    public NotificationCallWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }



    @NonNull
    @Override
    public Worker.Result doWork() {
        boolean inCall=false;
        Context applicationContext = getApplicationContext();
        final String callId = getInputData().getString("call_id");
        final String imageUrl = getInputData().getString("image");
        final String name = getInputData().getString("name" );
        final String message = getInputData().getString("body" );
        final String anthor_user_id = getInputData().getString("anthorUserCallId" );
        final String channel = getInputData().getString("channel" );
        final Boolean isViseoCall = getInputData().getBoolean("isVideoCall",true);
         String title = "";



        if(isViseoCall){
            title = applicationContext.getResources().getString(R.string.call_video_from);

        }
        else{
            title = applicationContext.getResources().getString(R.string.call_audio_from);

        }


        {

            NotificationManager notificationManager = (NotificationManager) applicationContext.getSystemService(applicationContext.NOTIFICATION_SERVICE);

            for (StatusBarNotification statusBarNotification : notificationManager.getActiveNotifications()) {
                System.out.println(statusBarNotification.getId()+"statusBarNotification.getId()");
                if(statusBarNotification.getId()==AllConstants.onGoingCallChannelId){
                    System.out.println(statusBarNotification.getTag()+"statusBarNotification.getTag()");
                    inCall=true;
//                     call_ongoing_call_user_id = statusBarNotification.getGroupKey();
                    break;
                }
            }
        }

        try {

            Intent intent
                    = new Intent(applicationContext, CallNotificationActivity.class);
            intent.putExtra("callRequest", message);
            intent.putExtra("id", anthor_user_id);
            intent.putExtra("title", title);
            intent.putExtra("name", name);
            intent.putExtra("imageUrl", imageUrl);
            intent.putExtra("call_id", callId);








            String channelCall = "call_Notifications";

//                PendingIntent pendingIntent = PendingIntent.getActivity(FirebaseMessageReceiver.this, 0,
//                        intent, PendingIntent.FLAG_CANCEL_CURRENT);
            PendingIntent pendingIntent = PendingIntent.getActivity(applicationContext, 0,
                    intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);
            /////////intent for answare
            Intent intentAnsware
                    = new Intent(applicationContext, ResponeCallActivity.class);
            intentAnsware.putExtra("callRequest", message);
            intentAnsware.putExtra("id", anthor_user_id);
            intentAnsware.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intentAnsware.putExtra("title", title);
//            intentAnsware.putExtra("name", name);
//            intent.putExtra("imageUrl", imageUrl);
//            intent.putExtra("call_id", callId);





            intentAnsware.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                PendingIntent answarePendingIntent = PendingIntent.getActivity(FirebaseMessageReceiver.this, 0,
//                        intentAnsware, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent answarePendingIntent = PendingIntent.getActivity(applicationContext, 0,
                    intentAnsware, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);

            /////////intent for reject
            Intent intentCancel
                    = new Intent(applicationContext, CancelCallFromCallNotification.class);

            intentCancel.putExtra("callRequest", message);
            intentCancel.putExtra("id", anthor_user_id);

            intentCancel.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                PendingIntent pendingIntentCancell = PendingIntent.getBroadcast(FirebaseMessageReceiver.this, 0,
//                        intentCancel,   PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent pendingIntentCancell = PendingIntent.getBroadcast(applicationContext, 0,
                    intentCancel, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            /////////////////
            ///for cancel ongoing call and start new call
//            CancelCallAndStartNewCall cancelCallAndStartNewCall=new CancelCallAndStartNewCall(applicationContext);
            Intent intentCancelAndStart
                    = new Intent(applicationContext, ResponeCallActivity.class);

            intentCancelAndStart.putExtra("callRequest", message);
            intentAnsware.putExtra("title", title);
            intentCancelAndStart.putExtra("id", anthor_user_id);

            intentCancelAndStart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                PendingIntent pendingIntentCancell = PendingIntent.getBroadcast(FirebaseMessageReceiver.this, 0,
//                        intentCancel,   PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent pendingIntentCancelAndStart = PendingIntent.getActivity(applicationContext, 1,
                    intentCancelAndStart, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);
////////////////////////
            NotificationCompat.Builder builder
                    = new NotificationCompat
                    .Builder(getApplicationContext(),
                    channelCall)
                    .setPriority(NotificationCompat.PRIORITY_MAX)

                    .setFullScreenIntent(pendingIntent, true)

                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
//                    .setSound(null)
                    .setOngoing(true)
                    .setOnlyAlertOnce(true)


                    .setVibrate(new long[]{10000, 10000})
                    .setTicker("Call_STATUS")

                    .setColorized(true)
                    .setSmallIcon(R.drawable.ic_memo_logo);
            if(!inCall) {
                System.out.println("!!!!!inCall");

                builder.addAction(R.drawable.btx_custom, HtmlCompat.fromHtml("<font color=\"" + ContextCompat.getColor(applicationContext, R.color.green) + "\">" + applicationContext.getResources().getString(R.string.recive_call) + " </font>", HtmlCompat.FROM_HTML_MODE_LEGACY), answarePendingIntent);
            }
            else {

                System.out.println("inCall");

                builder.addAction(R.drawable.btx_custom, HtmlCompat.fromHtml("<font color=\"" + ContextCompat.getColor(applicationContext, R.color.green) + "\">" + applicationContext.getResources().getString(R.string.recive_call_and_close) + " </font>", HtmlCompat.FROM_HTML_MODE_LEGACY), pendingIntentCancelAndStart);

            }


            builder.addAction(R.drawable.btx_custom, HtmlCompat.fromHtml("<font color=\"" + ContextCompat.getColor(applicationContext, R.color.red) + "\">" +applicationContext.getResources().getString(R.string.end_call) +" </font>", HtmlCompat.FROM_HTML_MODE_LEGACY), pendingIntentCancell);


            builder.setContentTitle(title);
            builder.setContentText(name);
            //}


            NotificationManager notificationManager
                    = (NotificationManager) applicationContext.getSystemService(
                    NOTIFICATION_SERVICE);
            // Check if the Android Version is greater than Oreo
            NotificationChannel notificationChannel
                    = new NotificationChannel(
                    channelCall, "call  Notifications",

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
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);
//                    notificationChannel.setSound(alarmSound, null);
            notificationChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE),null);
//                notificationChannel.setSound(Uri.parse("android.resource://" + applicationContext.getPackageName() + "/" + R.raw.ring), audioAttributes);
//                notificationChannel.setSound(RingtoneManager. getDefaultUri (RingtoneManager.TYPE_RINGTONE), audioAttributes);


            notificationManager.createNotificationChannel(
                    notificationChannel);
//                    notificationManager.createNotificationChannelGroup(new NotificationChannelGroup(channel_id, "Memo"));

            //            Notification note = builder.build();
//            note.flags |= Notification.FLAG_INSISTENT;
//                note.flags |= Notification.FLAG_NO_CLEAR;
            Glide.with(applicationContext)
                    .asBitmap()
                    .load(AllConstants.imageUrl + imageUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            builder.setLargeIcon(ImageProperties.getCircleBitmap(resource));
                            Notification note = builder.build();
                            note.flags |= Notification.FLAG_INSISTENT;
                            note.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                            notificationManager.notify(-1, note);
                            isRining(anthor_user_id,applicationContext,callId);

                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            builder.setLargeIcon(ImageProperties.getCircleBitmap(BitmapFactory.decodeResource(applicationContext.getResources(),
                             R.drawable.th)));
                            Notification note = builder.build();
                            note.flags |= Notification.FLAG_INSISTENT;
                            note.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

//                            notificationManager.notify(Integer.parseInt(channel), note);
                            notificationManager.notify(-1, note);

                            isRining(anthor_user_id,applicationContext,callId);
                        }
                    });





            // If there were no errors, return SUCCESS
            return Result.success();
        } catch (Throwable throwable) {

            // Technically WorkManager will return Result.failure()
            // but it's best to be explicit about it.
            // Thus if there were errors, we're return FAILURE
            Log.e(TAG, "Error applying blur", throwable);
            return Result.failure();
        }


    }
    public void isRining(String yout_id,Context context, String callId) {
        BaseApp myBase = BaseApp.getInstance();
        ClassSharedPreferences classSharedPreferences = BaseApp.getInstance().getClassSharedPreferences();

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(context);
        // on below line we are calling a string
        // request method to post the data to our API
        // in this we are calling a post method.
        StringRequest request = new StringRequest(Request.Method.POST, AllConstants.base_url_final+"ringing", new com.android.volley.Response.Listener<String>() {
//        StringRequest request = new StringRequest(Request.Method.POST, AllConstants.base_node_url+"ringing", new com.android.volley.Response.Listener<String>() {

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
                params.put("call_id", callId);


                // at last we are
                // returning our params.
                return params;
            }
        };
        // below line is to make
        // a json object request.
        myBase.addToRequestQueue(request);
    }
}
