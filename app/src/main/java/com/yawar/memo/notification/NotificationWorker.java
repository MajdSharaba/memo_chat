package com.yawar.memo.notification;
import static com.yawar.memo.utils.ShowNotificationKt.checkThereIsOngoingCall;

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
import com.yawar.memo.ui.chatPage.ConversationActivity;
import java.util.ArrayList;

public class NotificationWorker extends Worker {

    public static final String WORK_NAME ="NotificationWorker" ;
    private static final String TAG ="NotificationWorker" ;
    String call_ongoing_call_user_id;
    NotificationCompat.MessagingStyle inboxStyle ;
    ClassSharedPreferences classSharedPreferences;
    Notification.BubbleMetadata bubbleData;


    /**
     * Creates an instance of the {@link Worker}.
     *
     * @param appContext   the application {@link Context}
     * @param workerParams the set of {@link WorkerParameters}
     */
    public NotificationWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
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
        final String chatId = getInputData().getString("chat_id" );

        final String blockedFor = getInputData().getString("blockedFor" );
        int id =1;
        String GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL";
        String title = "";
        try {





            Intent intent
                    = new Intent(applicationContext, ConversationActivity.class);
            intent.putExtra("reciver_id",channel);
            intent.putExtra("sender_id", classSharedPreferences.getUser().getUserId());
            intent.putExtra("fcm_token",fcmToken);
            intent.putExtra("name",name);
            intent.putExtra("image",imageUrl);
            intent.putExtra("chat_id",chatId);
            intent.putExtra("special", specialNumber);
            intent.putExtra("blockedFor",blockedFor);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK  |Intent.FLAG_ACTIVITY_CLEAR_TOP );


            TaskStackBuilder stackBuilder = TaskStackBuilder.create(applicationContext);
            stackBuilder.addNextIntentWithParentStack(intent);
            PendingIntent pendingIntent
                    = stackBuilder.getPendingIntent(
                     0,
                    PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT );
//                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT  );

            String channel_id = "notification_channelllllll";
            NotificationCompat.Builder builder
                    = new NotificationCompat
                    .Builder(getApplicationContext(),
                    channel_id)
//                    .setNumber(id++)
                    .setFullScreenIntent(null, true)

                    .setPriority(NotificationCompat.PRIORITY_MAX)
//                            .setContentTitle(name)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .setContentText(message)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setSmallIcon(R.drawable.ic_memo_logo)
                    .setAutoCancel(false)
                    .setNumber(5)
                    .setOnlyAlertOnce(true)

                    .setGroup(GROUP_KEY_WORK_EMAIL)
                    .setVibrate(new long[]{1000, 1000, 1000,
                            1000, 1000})
                    .setContentIntent(!checkThereIsOngoingCall()?pendingIntent:null)
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
                notificationChannel.setSound(Uri.parse("android.resource://" + applicationContext.getPackageName() + "/" + R.raw.notification_sound), audioAttributes);
                notificationChannel.setSound(RingtoneManager. getDefaultUri (RingtoneManager.TYPE_NOTIFICATION), audioAttributes);
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
//                notificationChannel.setAllowBubbles(true);


// Set the interruption filter for the app
//                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);

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

