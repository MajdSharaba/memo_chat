package com.yawar.memo.notification;

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
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yawar.memo.BaseApp;
import com.yawar.memo.R;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.utils.ImageProperties;
import com.yawar.memo.ui.dashBoard.DashBord;

import java.util.ArrayList;
public class NotificationMessingCallWorker extends Worker {
    public static final String WORK_NAME ="NotificationMessingCallWorker" ;
    private static final String TAG ="NotificationMessingCallWorker" ;
    String call_ongoing_call_user_id;
    NotificationCompat.MessagingStyle inboxStyle ;
    ClassSharedPreferences classSharedPreferences;
    Notification.BubbleMetadata bubbleData;
    public NotificationMessingCallWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @NonNull
    @Override
    public Worker.Result doWork() {
        classSharedPreferences = BaseApp.Companion.getInstance().getClassSharedPreferences();
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

            Intent intent = new Intent(applicationContext, DashBord.class);
            intent.setAction("calls");
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK  |Intent.FLAG_ACTIVITY_CLEAR_TOP );

//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(applicationContext);
            stackBuilder.addNextIntentWithParentStack(intent);
            PendingIntent pendingIntent
                    = PendingIntent.getActivity(applicationContext, 0,
                    intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT );

            String channel_id = "notification_messing_call";
            NotificationCompat.Builder builder
                    = new NotificationCompat
                    .Builder(getApplicationContext(),
                    channel_id)
                    .setFullScreenIntent(null, true)

                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setContentText(message)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setSmallIcon(R.drawable.ic_memo_logo)
                    .setAutoCancel(false)
                    .setNumber(5)

                    .setGroup(GROUP_KEY_WORK_EMAIL)
                    .setVibrate(new long[]{1000, 1000, 1000,
                            1000, 1000})
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
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                notificationChannel.setShowBadge(true);
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                        .build();
//                    notificationChannel.setSound(alarmSound, null);
                notificationChannel.setSound(Uri.parse("android.resource://" + applicationContext.getPackageName() + "/" + R.raw.notification_sound), audioAttributes);
                notificationChannel.setSound(RingtoneManager. getDefaultUri (RingtoneManager.TYPE_NOTIFICATION), audioAttributes);
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
            Log.e(TAG, "Error applying blur", throwable);
            return Result.failure();
        }


    }

}

