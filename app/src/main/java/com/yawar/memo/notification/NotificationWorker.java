package com.yawar.memo.notification;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.text.format.Time;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
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
import com.yawar.memo.call.CallNotificationActivity;
import com.yawar.memo.call.ResponeCallActivity;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.service.FirebaseMessageReceiver;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.utils.BaseApp;
import com.yawar.memo.utils.ImageProperties;
import com.yawar.memo.views.ConversationActivity;
import com.yawar.memo.views.DashBord;
import com.yawar.memo.views.SplashScreen;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class NotificationWorker extends Worker {

    public static final String WORK_NAME ="NotificationWorker" ;
    private static final String TAG ="NotificationWorker" ;
    String call_ongoing_call_user_id;
    NotificationCompat.MessagingStyle inboxStyle ;
    ClassSharedPreferences classSharedPreferences;


    /**
     * Creates an instance of the {@link Worker}.
     *
     * @param appContext   the application {@link Context}
     * @param workerParams the set of {@link WorkerParameters}
     */
    public NotificationWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }



    @NonNull
    @Override
    public Worker.Result doWork() {
        classSharedPreferences = new ClassSharedPreferences(getApplicationContext());
        boolean inCall=false;
        Context applicationContext = getApplicationContext();
        ArrayList<String> arrayList = new ArrayList<String>();
//        inboxStyle = new NotificationCompat.InboxStyle();
       inboxStyle =  new NotificationCompat.MessagingStyle("Me");
        final String imageUrl = getInputData().getString("image");
        final String name = getInputData().getString("name" );
        final String message = getInputData().getString("body" );
        final String channel = getInputData().getString("channel" );
        final String fcmToken = getInputData().getString("fcm_token" );
        final String specialNumber = getInputData().getString("special" );
        final String blockedFor = getInputData().getString("blockedFor" );




        int id =1;
        String GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL";
        String title = "";







        try {

            Intent intent
                    = new Intent(applicationContext, ConversationActivity.class);

           ///// add paramters
            intent.putExtra("reciver_id",channel);
            intent.putExtra("sender_id", classSharedPreferences.getUser().getUserId());
            intent.putExtra("fcm_token",fcmToken);
            intent.putExtra("name",name);
            intent.putExtra("image",imageUrl);
            intent.putExtra("chat_id","");
            intent.putExtra("special", specialNumber);
            intent.putExtra("blockedFor",blockedFor);



//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(applicationContext);
            stackBuilder.addNextIntentWithParentStack(intent);

            // Pass the intent to PendingIntent to start the
            // next Activity
            PendingIntent pendingIntent
                    = stackBuilder.getPendingIntent(
                     0,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);






            String channel_id = "notification_channel";


            NotificationCompat.Builder builder
                    = new NotificationCompat
                    .Builder(getApplicationContext(),
                    channel_id)
                    .setNumber(id++)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
//                            .setContentTitle(name)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)

                    .setContentText(message)




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

            NotificationManager notificationManager
                    = (NotificationManager) applicationContext.getSystemService(
                    Context.NOTIFICATION_SERVICE);
            // Check if the Android Version is greater than Oreo
            if (Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel
                        = new NotificationChannel(
                        channel_id, "Memo",

                        NotificationManager.IMPORTANCE_HIGH);

                /// for check current notification
                for (StatusBarNotification statusBarNotification : notificationManager.getActiveNotifications()) {
                    System.out.println(statusBarNotification.getId() + "statusBarNotification.getId()");
                    if (statusBarNotification.getId() == Integer.parseInt(channel)) {
                        Notification notification = statusBarNotification.getNotification();
                        Bundle bundle = notification.extras;
                        arrayList = bundle.getStringArrayList("majd");
                        System.out.println("getStringArrayList"+arrayList.toString());



                        break;
                    }
                }
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

                notificationManager.createNotificationChannel(
                        notificationChannel);
                notificationManager.createNotificationChannelGroup(new NotificationChannelGroup(channel_id, "Memo"));





            }
            //// add new text

            arrayList.add(message);
            //// show all last message
            for(String s : arrayList){

                inboxStyle.addMessage(s,System.currentTimeMillis() ,name );
            }

//            inboxStyle.setBigContentTitle(name);
            builder.setStyle(inboxStyle);
            //// for save current messages
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("majd",arrayList);
            builder.setExtras(bundle);


            Glide.with(applicationContext)
                    .asBitmap()
                    .load(AllConstants.imageUrl + imageUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            builder.setLargeIcon(ImageProperties.getCircleBitmap(resource));
                            notificationManager.notify(Integer.parseInt(channel), builder.build());

                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
//                            super.onLoadCleared(placeholder);

                            builder.setLargeIcon(ImageProperties.getCircleBitmap(BitmapFactory.decodeResource(applicationContext.getResources(),
                                    R.drawable.th)));
                            notificationManager.notify(Integer.parseInt(channel), builder.build());

                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            builder.setLargeIcon(ImageProperties.getCircleBitmap(BitmapFactory.decodeResource(applicationContext.getResources(),
                                    R.drawable.th)));
                            notificationManager.notify(Integer.parseInt(channel), builder.build());


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

}

