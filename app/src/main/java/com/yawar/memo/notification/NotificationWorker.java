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
import com.yawar.memo.call.CallNotificationActivity;
import com.yawar.memo.call.ResponeCallActivity;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.service.FirebaseMessageReceiver;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.utils.BaseApp;
import com.yawar.memo.utils.ImageProperties;
import com.yawar.memo.views.SplashScreen;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class NotificationWorker extends Worker {

    public static final String WORK_NAME ="NotificationWorker" ;
    private static final String TAG ="NotificationWorker" ;
    String call_ongoing_call_user_id;

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
        boolean inCall=false;
        Context applicationContext = getApplicationContext();

        final String imageUrl = getInputData().getString("image");
        final String name = getInputData().getString("name" );
        final String message = getInputData().getString("body" );
        final String channel = getInputData().getString("channel" );
        int id =1;
        String GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL";
        String title = "";







        try {

            Intent intent
                    = new Intent(applicationContext, SplashScreen.class);



            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Pass the intent to PendingIntent to start the
            // next Activity
            PendingIntent pendingIntent
                    = PendingIntent.getActivity(applicationContext
                    , 0, intent,
                    PendingIntent.FLAG_ONE_SHOT|PendingIntent.FLAG_IMMUTABLE);






            String channel_id = "notification_channel";


            NotificationCompat.Builder builder
                    = new NotificationCompat
                    .Builder(getApplicationContext(),
                    channel_id)
                    .setNumber(id++)
                    .setPriority(NotificationCompat.PRIORITY_MAX).
                            setContentTitle(name)
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
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

                notificationManager.createNotificationChannel(
                        notificationChannel);
                notificationManager.createNotificationChannelGroup(new NotificationChannelGroup(channel_id, "Memo"));





            }

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

