package com.yawar.memo.call;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.videocallapp.JavascriptInterface;
import com.yawar.memo.modelView.RequestCallViewModel;
import com.yawar.memo.modelView.ResponeCallViewModel;
import com.yawar.memo.notification.CancelCallFromCallOngoingNotification;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.R;
import com.yawar.memo.service.SocketIOService;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ResponeCallActivity extends AppCompatActivity {
    String username = "";
    String friendsUsername = "";
    String peerId = null;
    Boolean isPeerConnected = false;
    String id ="0";
    private final int requestcode = 1;
    public static final String ON_CALL_REQUEST = "CallMainActivity.ON_CALL_REQUEST";
    public static final String ON_STOP_CALLING_REQUEST = "CallMainActivity.ON_CALL_REQUEST";
    public static final String ON_RECIVED_SETTINGS_CALL = "CallMainActivity.ON_RECIVED_SETTINGS_CALL";
    public static final String ON_CLOSE_CALL_FROM_NOTIFICATION_Call_ACTIVITY = "ON_CLOSE_CALL_FROM_NOTIFICATION_Call_ACTIVITY";
    public static final String ON_RECIVED_ASK_FOR_VIDEO = "on_recived_ask_for_video";
    public static final String ON_RECIVED_RESPONE_FOR_VIDEO = "on_recived_respone_for_video";









    Boolean isAudio = true;
    Boolean isVideoForMe = false;
    Boolean isVideoForyou = false;

    Boolean isVideoCall = false;
    Button callBtn;
    String uniqueId = "";
    ImageView acceptBtn;
    ImageView rejectBtn;
    String anotherUserId;
    private static final String[] permissions = {android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO};
    EditText friendNameEdit;
    WebView webView;
    TextView incomingCallTxt;
    RelativeLayout callLayout;
    RelativeLayout inputLayout;
    LinearLayout layoutCallProperties;
    ImageButton imgBtnStopCallLp;
    ImageButton imgBtnOpenCameraCallLp;
    ImageButton imgBtnOpenAudioCallLp;
    ImageButton imgBtnSwitchCamera;
    ClassSharedPreferences classSharedPreferences;
    CircleImageView imageCallUser;
    String callString = null;
    AlertDialog alertDialog,alertDialogForME;
    AlertDialog.Builder dialogForMe ;
    ResponeCallViewModel responeCallViewModel;



    private final BroadcastReceiver reciveStopCalling = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String stopCallString = intent.getExtras().getString("get stopCalling");
                    finish();
                    JSONObject message = null;


                }
            });
        }
    };
    private final BroadcastReceiver reciveclosecallfromnotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("CallNotificatio000000", "run: close ");
                    finish();
                    ///////////


                }
            });
        }
    };
    private final BroadcastReceiver reciveSettingsCalling = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    String stopCallString = intent.getExtras().getString("get settings");
                    JSONObject message = null;
                    try {
                        message = new JSONObject(stopCallString);
                         isVideoForyou = message.getBoolean("camera");
                        boolean audioSetting = message.getBoolean("microphone");
                        responeCallViewModel.isVideoForYou.setValue(isVideoForyou);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ///////////


                }
            });
        }
    };
    private final BroadcastReceiver reciveAskForCall = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("the view visabilty is"+webView.getVisibility());

                    String stopCallString = intent.getExtras().getString("get askVideo");
                    JSONObject message = null;
                    try {
                        message = new JSONObject(stopCallString);
                        isVideoForyou = message.getBoolean("video");

                        responeCallViewModel.isVideoForYou.setValue(isVideoForyou);


                        if(isVideoForyou) {
                                showSwitchToVideoWhenANthorUserRequestDialog(username + " " + getResources().getString(R.string.alert_switch_to_video_from_anthor_message));


                        } else {
//                                webView.setVisibility(View.GONE);
//                                imageCallUser.setVisibility(View.VISIBLE);

                                if(alertDialog!=null){
                                 alertDialog.dismiss();}

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ///////////


                }
            });
        }
    };
    private final BroadcastReceiver reciveAcceptChangeToVideoCall = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    String stopCallString = intent.getExtras().getString("get responeAskVideo");
                    System.out.println("get responeAskVideo");
                    JSONObject message = null;
                    try {
                        message = new JSONObject(stopCallString);
                        isVideoForyou = message.getBoolean("video");
                        responeCallViewModel.isVideoForYou.setValue(isVideoForyou);

                        if (!isVideoForyou){
                            responeCallViewModel.isVideoForMe.setValue(false);

                        }


                        if(alertDialogForME!=null){

                        alertDialogForME.dismiss();}





                    } catch (JSONException e) {
                        e.printStackTrace();
                    }





                }
            });
        }
    };
    private void sendPeerId(String object,String peer_id) {
        System.out.println(object + "this is object ");
        JSONObject message = null;
        /////////


        try {
            message = new JSONObject(object);
            message.put("peerId", peer_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(message.toString() + "peeeeeeeeeeeId object");


        Intent service = new Intent(this, SocketIOService.class);

        service.putExtra(SocketIOService.EXTRA_SEND_PEER_ID_PARAMTERS, message.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_SEND_PEER_ID);
        startService(service);

    }
    private void sendSettingsCall(boolean video, boolean mic) {
        Intent service = new Intent(this, SocketIOService.class);
        JSONObject data = new JSONObject();
        try {
            data.put("snd_id", classSharedPreferences.getUser().getUserId());
            data.put("rcv_id", anotherUserId);
            data.put("microphone", mic);
            data.put("camera", video);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        service.putExtra(SocketIOService.EXTRA_SETTINGS_CALL_PARAMTERS, data.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_SETTING_CALL);
        startService(service);

    }
    private void SendSwitchTOVideoCallRespone(boolean video) {
        Intent service = new Intent(this, SocketIOService.class);
        JSONObject data = new JSONObject();
        try {
            data.put("my_id", classSharedPreferences.getUser().getUserId());
            data.put("your_id", anotherUserId);
            data.put("video", video);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        service.putExtra(SocketIOService.EXTRA_RESPONE_VIDEO_CALL_PARAMTERS, data.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_RESPONE_VIDEO_CALL);
        startService(service);

    }
    private void closeCall() {
        Intent service = new Intent(this, SocketIOService.class);
        JSONObject data = new JSONObject();
        try {
//            data.put("close_call", true);
            data.put("id",anotherUserId );//            data.put("snd_id", classSharedPreferences.getUser().getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("close Call");
        service.putExtra(SocketIOService.EXTRA_STOP_CALL_PARAMTERS, data.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_STOP_CALLING);
        startService(service);

    }
    private void sendAskForVideoCall(boolean video) {
        Intent service = new Intent(this, SocketIOService.class);
        JSONObject data = new JSONObject();
        System.out.println("isVideosetting"+video);
        try {
            data.put("my_id", classSharedPreferences.getUser().getUserId());
            data.put("your_id", anotherUserId);
            data.put("video", video);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        System.out.println("call");
        service.putExtra(SocketIOService.EXTRA_SEND_ASK_VIDEO_CALL_PARAMTERS, data.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_SEND_VIDEO_CALL_REQUEST);
        startService(service);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        showWhenLockedAndTurnScreenOn();
        setContentView(R.layout.activity_call_main);
        System.out.println("Starrrrrrrrrrrt call activity");
        if (!isPermissionGranted()) {
            askPermissions();
        }




        LocalBroadcastManager.getInstance(this).registerReceiver(reciveStopCalling, new IntentFilter(ON_STOP_CALLING_REQUEST));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveSettingsCalling, new IntentFilter(ON_RECIVED_SETTINGS_CALL));
        Intent closeIntent = new Intent(CallNotificationActivity.ON_CLOSE_CALL_FROM_NOTIFICATION);

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(closeIntent);
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveclosecallfromnotification, new IntentFilter(ON_CLOSE_CALL_FROM_NOTIFICATION_Call_ACTIVITY));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveAskForCall, new IntentFilter(ON_RECIVED_ASK_FOR_VIDEO));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveAcceptChangeToVideoCall, new IntentFilter(ON_RECIVED_RESPONE_FOR_VIDEO));



        responeCallViewModel = new ViewModelProvider(this).get(ResponeCallViewModel.class);

        classSharedPreferences = new ClassSharedPreferences(this);
        webView = findViewById(R.id.webView);
        incomingCallTxt = findViewById(R.id.user_name);
        callLayout = findViewById(R.id.audio_only_Layout);
        acceptBtn = findViewById(R.id.acceptBtn);
        rejectBtn = findViewById(R.id.rejectBtn);
        imgBtnStopCallLp = findViewById(R.id.close_call_layout);
        imgBtnOpenCameraCallLp = findViewById(R.id.image_video_call_layout);
        imgBtnOpenAudioCallLp = findViewById(R.id.image_audio_call_layout);
        imgBtnSwitchCamera = findViewById(R.id.image_switch_camera);
        imageCallUser = findViewById(R.id.image_user_calling);

        layoutCallProperties = findViewById(R.id.video_rl);
        layoutCallProperties.setVisibility(View.VISIBLE);

        Bundle bundle = getIntent().getExtras();
         id = bundle.getString("id", "0");
        callString = bundle.getString("callRequest", "code");
        System.out.println("call string is that"+callString);


        setupWebView();
        dialogForMe = new AlertDialog.Builder(this);


        JSONObject message = null;
        JSONObject userObject;
        JSONObject typeObject;

        try {
            message = new JSONObject(callString);
            userObject = new JSONObject(message.getString("user"));
            typeObject = new JSONObject(message.getString("type"));
            isVideoForyou = typeObject.getBoolean("video");
            responeCallViewModel.isVideoForMe.setValue(isVideoForyou);
            responeCallViewModel.isVideoForYou.setValue(isVideoForyou);

            username = userObject.getString("name");
            anotherUserId = message.getString("snd_id");





        } catch (JSONException e) {
            e.printStackTrace();
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(-1);

        incomingCallTxt.setText(getResources().getString(R.string.call_from)+" "+username);
        showInCallNotification();




        responeCallViewModel.isVideoForMe.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
                if(s){

                    webView.setVisibility(View.VISIBLE);
                    layoutCallProperties.setVisibility(View.VISIBLE);
                    imageCallUser.setVisibility(View.GONE);


                }
                else {
                    if(!responeCallViewModel.isVideoForYou.getValue()) {
                        webView.setVisibility(View.GONE);
                        layoutCallProperties.setVisibility(View.VISIBLE);
                        imageCallUser.setVisibility(View.VISIBLE);

                    }

                }
                callJavascriptFunction("javascript:toggleVideo(\"" + s + "\")");

            }
        });
        responeCallViewModel.isVideoForYou.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
                if(s){
                    webView.setVisibility(View.VISIBLE);
                    layoutCallProperties.setVisibility(View.VISIBLE);
                    imageCallUser.setVisibility(View.GONE);}
                else{
                    if(!responeCallViewModel.isVideoForMe.getValue()){
                        webView.setVisibility(View.GONE);
                        layoutCallProperties.setVisibility(View.VISIBLE);
                        imageCallUser.setVisibility(View.VISIBLE);

                    }




                }
                callJavascriptFunction("javascript:toggleStream(\"" + s+ "\")");

            }
        });



        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("acceptBtn"+callString);

                if(callString!=null){
                    System.out.println("call Request"+callString);
                    if(peerId!=null){
                    callLayout.setVisibility(View.GONE);
                    layoutCallProperties.setVisibility(View.VISIBLE);




                    }
                }


            }
        });


        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callLayout.setVisibility(View.GONE);
                finish();

            }
        });
        ////// for call layout_call_properties
        imgBtnStopCallLp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeCall();
                finish();
            }
        });
        /////
        imgBtnOpenCameraCallLp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!responeCallViewModel.isVideoForMe.getValue()&&!responeCallViewModel.isVideoForYou.getValue()){
                    showSwitchToVideoWhenIAskDialog(getResources().getString(R.string.alert_switch_to_video_message));
                   responeCallViewModel.isVideoForMe.setValue(!responeCallViewModel.isVideoForMe.getValue());
                    sendAskForVideoCall(responeCallViewModel.isVideoForMe.getValue());

                }else {
                    closeOpenVideo();
                }


            }
        });
        //////
        imgBtnOpenAudioCallLp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeOpenAudio();

            }
        });
        imgBtnSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callJavascriptFunction("javascript:toggleCamera()");

            }
        });
        //////////////////////////////


    }




    private void sendCallRequest() {
        if (!isPeerConnected) {
            Toast.makeText(this, "You're not connected. Check your internet", Toast.LENGTH_LONG).show();
            return;
        }


    }


    private void setupWebView() {

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                request.grant(request.getResources());

            }

//            override fun onPermissionRequest(request: PermissionRequest?) {
//                request?.grant(request.resources)
//            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.addJavascriptInterface(new JavascriptInterface(ResponeCallActivity.this), "Android");

        loadVideoCall();
    }


    private void loadVideoCall() {
//        String filePath = "file:android_asset/call.html";
        String filePath = "file:android_asset/call.html";

        System.out.println("load videooooooooooooooo" + filePath);
        webView.loadUrl(filePath);


        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                initializePeer();

            }
        });
    }


    private void initializePeer() {

        String uniqueId = getUniqueID();


        callJavascriptFunction("javascript:init(\"" + responeCallViewModel.isVideoForMe.getValue() + "\")");
//        callJavascriptFunction("javascript:init(\"" + uniqueId + "\","+"\"" + classSharedPreferences.getUser().getUserId() + "\")");



    }

    private void onCallRequest(String caller) {
        if (caller == null) return;

//                callLayout.visibility = View.VISIBLE
//        callLayout.setVisibility(View.VISIBLE);
        incomingCallTxt.setText("$caller is calling...");
        layoutCallProperties.setVisibility(View.VISIBLE);

    }






private String getUniqueID() {
    return UUID.randomUUID().toString();
}
    private void callJavascriptFunction(String functionString) {
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.evaluateJavascript(functionString, null);
            }
        });
    }
    public void onPeerConnected( String string) {
        System.out.println("the key is"+string);
        peerId= string;
        if(string!=null){
            Log.i("FirebaseMessageReceiver", "sendPeerId(callString,peerId); "+callString);
        sendPeerId(callString,peerId);
        layoutCallProperties.setVisibility(View.VISIBLE);}
        else{
            callJavascriptFunction("javascript:init(\"" + responeCallViewModel.isVideoForMe.getValue() + "\")");

        }
    }



    private void askPermissions() {
        ActivityCompat.requestPermissions(this, permissions, requestcode);
    }

    private Boolean isPermissionGranted() {

        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        webView.loadUrl("about:blank");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveStopCalling);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveSettingsCalling);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(Integer.parseInt("0"));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//         NotificationManager notificationManager1 = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//        for (StatusBarNotification statusBarNotification : notificationManager1.getActiveNotifications()) {
//           System.out.println(statusBarNotification.getId()+"statusBarNotification.getId()");
//        }
//        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveclosecallfromnotification);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveAskForCall);


        closeCall();

        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
                finish();

        super.onBackPressed();
    }
    public void closeOpenVideo() {
        responeCallViewModel.isVideoForMe.setValue(!responeCallViewModel.isVideoForMe.getValue());
                sendSettingsCall(responeCallViewModel.isVideoForMe.getValue(),true);
    }
    public void closeOpenAudio(){
        isAudio = !isAudio;
        callJavascriptFunction("javascript:toggleAudio(\"" + isAudio + "\")");
        if (isAudio) {
            imgBtnOpenAudioCallLp.setImageResource(R.drawable.ic_baseline_mic_off_24);
        } else {
            imgBtnOpenAudioCallLp.setImageResource(R.drawable.ic_baseline_mic_24);
        }

    }
    void showSwitchToVideoWhenIAskDialog(String message){


        dialogForMe.setTitle(message);
        dialogForMe.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                responeCallViewModel.isVideoForMe.setValue(!responeCallViewModel.isVideoForMe.getValue());
                sendAskForVideoCall(false);
                dialog.dismiss();
            }
        });


        alertDialogForME = dialogForMe.create();
        alertDialogForME.show();
    }
    void showSwitchToVideoWhenANthorUserRequestDialog(String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(message);
        dialog.setPositiveButton(R.string.switch_to_video,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        responeCallViewModel.isVideoForMe.setValue(!responeCallViewModel.isVideoForMe.getValue());
                        SendSwitchTOVideoCallRespone(responeCallViewModel.isVideoForMe.getValue());
                        dialog.dismiss();

                    }
                });
        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                responeCallViewModel.isVideoForYou.setValue(!responeCallViewModel.isVideoForYou.getValue());

                SendSwitchTOVideoCallRespone(isVideoForMe);
                dialog.dismiss();


            }
        });


        alertDialog = dialog.create();
        alertDialog.show();
    }

    private void showWhenLockedAndTurnScreenOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
//            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
//            window.addFlags(
//                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
//            )
        }
    }
    private void showInCallNotification() {
//        Intent intent
//                = new Intent(this, CallNotificationActivity.class);
//        intent.putExtra("callRequest",message );
        Intent intent
                = new Intent(ResponeCallActivity.this, ResponeCallActivity.class);
        // Assign channel ID
        // Here FLAG_ACTIVITY_CLEAR_TOP flag is set to clear
        // the activities present in the activity stack,
        // on the top of the Activity that is to be launched
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Pass the intent to PendingIntent to start the
        // next Activity
        PendingIntent pendingIntent
                = PendingIntent.getActivity(ResponeCallActivity.this
                , 0, intent,
                PendingIntent.FLAG_ONE_SHOT|PendingIntent.FLAG_IMMUTABLE);
        /////////intent for reject
        Intent intentCancel
                = new Intent(this, CancelCallFromCallOngoingNotification.class);

        intentCancel.putExtra("id", anotherUserId);

        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                PendingIntent pendingIntentCancell = PendingIntent.getBroadcast(FirebaseMessageReceiver.this, 0,
//                        intentCancel,   PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntentCancell = PendingIntent.getBroadcast(this, 0,
                intentCancel, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        String channel_id = "notification_channel";

//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
//                intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder
                = new NotificationCompat
                .Builder(getApplicationContext(),
                channel_id)
                .setPriority(NotificationCompat.PRIORITY_MAX)

                .setFullScreenIntent(null, false)

                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setSound(null)
                .setOngoing(true)
                .setGroup(anotherUserId)


                .setVibrate(new long[]{10000, 10000})
                .setTicker("Call_STATUS")
                .addAction(R.drawable.btx_custom, HtmlCompat.fromHtml("<font color=\"" + ContextCompat.getColor(ResponeCallActivity.this, R.color.red) + "\">" +getResources().getString(R.string.cancel)+ " </font>", HtmlCompat.FROM_HTML_MODE_LEGACY), pendingIntentCancell)


                .setColorized(true)
                .setSmallIcon(R.drawable.ic_memo_logo)
                        .setContentTitle(username)
                .setContentText(getResources().getString(R.string.ongoing_call));
        NotificationManager notificationManager
                = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        // Check if the Android Version is greater than Oreo
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = new NotificationChannel(
                    channel_id, "Memo",

                    NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            notificationManager.createNotificationChannel(
                    notificationChannel);
            notificationManager.createNotificationChannelGroup(new NotificationChannelGroup(channel_id, "Memo"));

        }


        Notification note = builder.build();
        note.flags |= Notification.FLAG_INSISTENT;
        notificationManager.notify(0,note);
    }
}