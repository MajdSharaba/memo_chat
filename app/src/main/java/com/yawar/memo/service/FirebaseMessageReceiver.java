package com.yawar.memo.service;

import android.annotation.SuppressLint;
import android.app.Notification;
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
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.yawar.memo.R;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.repositry.ChatRoomRepo;
import com.yawar.memo.utils.BaseApp;
import com.yawar.memo.utils.ImageProperties;
import com.yawar.memo.views.DashBord;
import com.yawar.memo.views.SplashScreen;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class FirebaseMessageReceiver
        extends FirebaseMessagingService implements Observer {
    int id =1;
    BaseApp myBase;
    ChatRoomRepo chatRoomRepo;
    String chat_id;

    private  int NOTIFICATION_ID = 237;
    Notification.InboxStyle inboxStyle = new Notification.InboxStyle();
    String GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL";




    // Override onMessageReceived() method to extract the
    // title and
    // body from the message passed in FCM
    @SuppressLint("WrongThread")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        myBase = (BaseApp) getApplication();
        chatRoomRepo=myBase.getChatRoomRepo();
        myBase.getObserver().addObserver(this);
        String message = "";
        Map<String, String> data = remoteMessage.getData();
        String myCustomKey = data.get("body");
        // First case when notifications are received via
        // data event
        // Here, 'title' and 'message' are the assumed names
        // of JSON
        // attributes. Since here we do not have any data
        // payload, This section is commented out. It is
        // here only for reference purposes.
//		if(remoteMessage.getData().size()>0){
//			showNotification(remoteMessage.getData().get("title"),
//						remoteMessage.getData().get("message"));
//		}

        // Second case when notification payload is
        // received.
        if(remoteMessage.getData().size()>0){
            System.out.println(remoteMessage.getData().toString()+"jjjjjjjjjjjjjjjjjjjjjjj");


            // Since the notification is received directly from
            // FCM, the title and the body can be fetched
            // directly as below.
            if(!chatRoomRepo.checkInChat(remoteMessage.getData().get("chat_id"))){
                switch (remoteMessage.getData().get("type")){
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
                            message = remoteMessage.getData().get("body") ;

            }

               new showNotification(this).execute( remoteMessage.getData().get("title"), remoteMessage.getData().get("image"),message,remoteMessage.getData().get("chat_id"));
//            showNotification(
//                    remoteMessage.getData().get("title"),
//                    remoteMessage.getData().get("image"),
//                    remoteMessage.getData().get("body"));
        }}
    }

    // Method to get the custom Design for the display of
    // notification.
    private RemoteViews getCustomDesign(String title,
                                        String message) {
        @SuppressLint("RemoteViewLayout") RemoteViews remoteViews = new RemoteViews(
                getApplicationContext().getPackageName(),
                R.layout.notification);
        remoteViews.setTextViewText(R.id.title, title);
        remoteViews.setTextViewText(R.id.message, message);
        remoteViews.setImageViewResource(R.id.icon,
              R.drawable.ic_launcher_foreground);

//        CircleImage circleImage = remoteViews.setImageViewUri(R.id.icon,image);

//        if(bitmap.equals(null)){
//        remoteViews.setImageViewResource(R.id.icon,
//                R.drawable.ic_launcher_foreground);}
//        else {
//            remoteViews.setImageViewBitmap(R.id.icon,
//                    bitmap);
//        }
        return remoteViews;
    }

    // Method to display the notifications
    public void showNotification(String title,String image,
                                 String message) {

        // Pass the intent to switch to the MainActivity
        Intent intent
                = new Intent(this, DashBord.class);
        // Assign channel ID
        String channel_id = "notification_channel";
        // Here FLAG_ACTIVITY_CLEAR_TOP flag is set to clear
        // the activities present in the activity stack,
        // on the top of the Activity that is to be launched
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Pass the intent to PendingIntent to start the
        // next Activity
        PendingIntent pendingIntent
                = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        // Create a Builder object using NotificationCompat
        // class. This will allow control over all the flags
        NotificationCompat.Builder builder
                = new NotificationCompat
                .Builder(getApplicationContext(),
                channel_id)
                .setNumber(id++)
//               .setLargeIcon(CircleImage.getCircleBitmap())
//                .setLargeIcon(Glide)


                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true).setStyle(new NotificationCompat.InboxStyle().setBigContentTitle("Majd")
                        .addLine(message)
                        )
                .setSound(Uri.parse("http://commondatastorage.googleapis.com/codeskulptor-assets/Evillaugh.ogg"))
                //specify which group this notification belongs to
                //set this notification as the summary for the group
                .setVibrate(new long[]{1000, 1000, 1000,
                        1000, 1000})
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);



        // A customized design for the notification can be
        // set only for Android versions 4.1 and above. Thus
        // condition for the same is checked here.
//        if (Build.VERSION.SDK_INT
//                >= Build.VERSION_CODES.JELLY_BEAN) {
//            builder = builder.setContent(
//                    getCustomDesign(message, message)
//            );
//        } // If Android Version is lower than Jelly Beans,
//        // customized layout cannot be used and thus the
//        // layout is set as follows
//        else {
//            builder = builder.setContentTitle(title)
//                    .setContentText(message)
//
//                    .setSmallIcon(R.drawable.ic_launcher_foreground);
//
//        }
        // Create an object of NotificationManager class to
        // notify the
        // user of events that happen in the background.
        NotificationManager notificationManager
                = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        // Check if the Android Version is greater than Oreo
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = new NotificationChannel(
                    channel_id, "web_app",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(
                    notificationChannel);
        }


        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public void update(Observable observable, Object o) {

    }

    private class showNotification extends AsyncTask<String, Void, Bitmap> {

        Context ctx;
        String message;
        String title;
        String chat_id;

        public showNotification(Context context) {
            super();
            this.ctx = context;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            InputStream in;
           message = params[2] ;
           title = params[0];
           chat_id = params[3];

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
                Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.record_start);
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
                        .setNumber(id++).
                          setContentTitle(title)
                        .setContentText(message)
                        .setLargeIcon(ImageProperties.getCircleBitmap(bitmap))



                        .setSmallIcon(R.drawable.ic_memo_logo)
                        .setAutoCancel(true)
                        .setSound((RingtoneManager. getDefaultUri (RingtoneManager. TYPE_NOTIFICATION)))
                        .setGroup(GROUP_KEY_WORK_EMAIL)




                        //specify which group this notification belongs to
                        //set this notification as the summary for the group
                        .setVibrate(new long[]{1000, 1000, 1000,
                                1000, 1000})
                        .setOnlyAlertOnce(true)
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

                notificationManager.notify(Integer.parseInt(chat_id), builder.build());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

