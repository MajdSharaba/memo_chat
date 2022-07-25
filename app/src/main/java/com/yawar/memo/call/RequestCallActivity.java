package com.yawar.memo.call;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
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
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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

import com.yawar.memo.notification.CancelCallFromCallOngoingNotification;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.Api.ServerApi;
import com.yawar.memo.R;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.service.SocketIOService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;
public class RequestCallActivity extends AppCompatActivity {

    Boolean isPeerConnected = false;
    boolean isRining = false;
    boolean isVideoForMe = true;
    boolean isVideoForYou = true;
    boolean isVideoCall = true;
    boolean endCall = false;
    ServerApi serverApi;
    AlertDialog alertDialog,alertDialogForME;
    private final int requestcode = 1;
    public static final String ON_STOP_CALLING_REQUEST = "RequestCallActivity.ON_STOP_CALLING_REQUEST";
    public static final String ON_RINING_REQUEST = "RequestCallActivity.ON_RINING_REQUEST";
    public static final String ON_RECIVED_ASK_FOR_VIDEO = "on_recived_ask_for_video";
    public static final String ON_RECIVED_RESPONE_FOR_VIDEO = "on_recived_respone_for_video";

    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
    CountDownTimer countDownTimer;
    CountDownTimer countEndCallDownTimer;

    AlertDialog.Builder dialogForMe;


    TextView userNameTv;
    UserModel userModel;
    String fcm_token;
    String TAG = "RequestCallActivity";


//    var firebaseRef = Firebase.database.getReference("users")

    Boolean isAudio = true;
//    Boolean isVideo = true;
    Button callBtn;
    String uniqueId = "";
    ImageView toggleAudioBtn;
    LinearLayout layoutCallProperties;
    ImageButton acceptBtn;
    ImageButton openCloseIb;
    ImageView rejectBtn;
    ImageButton imageStopCalling;
    ImageButton btnImageOpenCamera;
    ImageButton imgBtnStopCallLp;
    ImageButton imgBtnOpenCameraCallLp;
    ImageButton imgBtnOpenAudioCallLp;
    ImageButton imgBtnSwitchCamera;
    final Handler handler = new Handler();





    TextView callStatusTV;

    public static final String FETCH_PEER_ID = "RequestCallActivity.FETCH_PEER_ID";
    public static final String ON_RECIVED_SETTINGS_CALL = "RequestCallActivity.ON_RECIVED_SETTINGS_CALL";


    private static final String[] permissions = {android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO};
    EditText friendNameEdit;
    WebView webView;
    TextView incomingCallTxt;
    LinearLayout callLayout;
    RelativeLayout inputLayout;
    LinearLayout callControlLayout;
    String callString = null;
    ClassSharedPreferences classSharedPreferences;
    String anthor_user_id;
    String userName;
    String my_id;
    String PeerIdRecived = "no connect";
    public static final String ON_CLOSE_CALL_FROM_NOTIFICATION = "CallNotificationActivity.ON_RINING_REQUEST";




    private final BroadcastReceiver reciveRining = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String reciveCallString = intent.getExtras().getString("get rining");
                    JSONObject message = null;
                    try {
                        message = new JSONObject(reciveCallString);
                         isRining = message.getBoolean("state");
                        if(isRining){
//                            Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.record_start);

                            if(!endCall){
                            callStatusTV.setText(getResources().getString(R.string.rining));
                            mMediaPlayer.release();
                                mMediaPlayer = MediaPlayer.create(RequestCallActivity.this, R.raw.ring);
                            mMediaPlayer.setLooping(true);

                            mMediaPlayer.start();
                                countDownTimer.cancel();
                                countDownTimer.start();}



                        }
                        else {
                            callStatusTV.setText(getResources().getString(R.string.calling));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ///////////


                }
            });
        }
    };
    private final BroadcastReceiver reciveCloseCallFromNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("CallNotificatio", "run: close ");
                    finish();
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
                    isVideoForYou = !isVideoForYou;

                    String stopCallString = intent.getExtras().getString("get responeAskVideo");
                    System.out.println("get responeAskVideo");
                    JSONObject message = null;
                    try {
                        message = new JSONObject(stopCallString);
                        isVideoForYou = message.getBoolean("video");
                        if (isVideoForYou){
                            callJavascriptFunction("javascript:toggleStream(\"" + isVideoForYou + "\")");

                    }

                    else{
                        isVideoForMe=!isVideoForMe;
                            System.out.println("elseeee isVideoForYou "+ isVideoForMe);

                            callJavascriptFunction("javascript:toggleVideo(\"" + isVideoForMe + "\")");

                            webView.setVisibility(View.GONE);
                            callLayout.setVisibility(View.VISIBLE);
                            layoutCallProperties.setVisibility(View.GONE);
//                            alertDialogForME.setMessage(getResources().getString(R.string.reject));


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
    private final BroadcastReceiver reciveSettingsCalling = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("the view visabilty is"+webView.getVisibility());

                    String stopCallString = intent.getExtras().getString("get settings");
                    JSONObject message = null;
                    try {
                        message = new JSONObject(stopCallString);
                         isVideoForYou = message.getBoolean("camera");
                        boolean audioSetting = message.getBoolean("microphone");
                        callJavascriptFunction("javascript:toggleStream(\"" +isVideoForYou  + "\")");
                        if(isVideoForYou){

                            if(webView.getVisibility()==View.GONE){
                                String mess=userName.concat(getResources().getString(R.string.alert_switch_to_video_from_anthor_message));
                                webView.setVisibility(View.VISIBLE);
                                callLayout.setVisibility(View.GONE);
                                layoutCallProperties.setVisibility(View.VISIBLE);
                            }}
                            else {
                                if(!isVideoForMe){
                                    webView.setVisibility(View.GONE);
                                    callLayout.setVisibility(View.VISIBLE);
                                    layoutCallProperties.setVisibility(View.GONE);
                                }



                        }
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
                        isVideoForYou = message.getBoolean("video");
//                        boolean audioSetting = message.getBoolean("microphone");
                        callJavascriptFunction("javascript:toggleStream(\"" +isVideoForYou  + "\")");

                        if(isVideoForYou) {
//                            if (webView.getVisibility() == View.GONE) {
                            webView.setVisibility(View.VISIBLE);
                            callLayout.setVisibility(View.GONE);
                            layoutCallProperties.setVisibility(View.VISIBLE);
                            showSwitchToVideoWhenANthorUserRequestDialog( getResources().getString(R.string.alert_switch_to_video_from_anthor_message));

                            //                            imageCallUser.setVisibility(View.GONE);


//                            }

                        } else {
                            webView.setVisibility(View.GONE);
                            callLayout.setVisibility(View.VISIBLE);
                            layoutCallProperties.setVisibility(View.GONE);
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
    private final BroadcastReceiver recivePeerId = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String callString = intent.getExtras().getString("fetchPeer");
                    isRining = false;
                    String call_peer_id = "no connect";
                    System.out.println("FETCH_PEER_ID" + callString);
                    JSONObject message = null;
                    ///////////
                    try {

                        message = new JSONObject(callString);
                        if(message.getString("peerId")!=null){
                            call_peer_id = message.getString("peerId");}


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.println(call_peer_id + "reciveeeePeeer");
                    if(call_peer_id.equals("null")){
                        PeerIdRecived= call_peer_id;

                        finishCall();

                    }
                    else if(call_peer_id.equals("no connect")){
                        Log.i("whataboutcall", "run: not connected");

                    }
                    else {
                        PeerIdRecived = call_peer_id;

                        if (!endCall) {
                            callStatusTV.setText(R.string.replied);


                            ///for stop 15 sec counter
                            countDownTimer.cancel();
                            ////for close rining
                            mMediaPlayer.release();
                            ///for shw call notification
                            showInCallNotification();


                            callJavascriptFunction("javascript:startCall(\"" + PeerIdRecived + "\"," + "\"" + isVideoForMe + "\")");
                            if (isVideoForMe) {
                                callLayout.setVisibility(View.GONE);
                                layoutCallProperties.setVisibility(View.VISIBLE);
                            } else {
//                            callStatusTV.setText("تم الرد");
                                callLayout.setVisibility(View.VISIBLE);
                                layoutCallProperties.setVisibility(View.GONE);

                            }
                        }
                    }
                }
            });
        }
    };



    private final BroadcastReceiver reciveStopCalling = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String stopCallString = intent.getExtras().getString("get stopCalling");
                    System.out.println("stopCallString");
                    JSONObject message = null;
                    finishCall();

//                    try {
//                        message = new JSONObject(stopCallString);
//                        boolean isStop = message.getBoolean("close_call");
////                        if(isStop){
//                            finish();
////                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }

                    ///////////


                }
            });
        }
    };

    private void startCall() {
        startCounter();
        Intent service = new Intent(this, SocketIOService.class);
        JSONObject data = new JSONObject();
        JSONObject type = new JSONObject();
        JSONObject userObject = new JSONObject();
//        serverApi.sendNotification(userModel.getUserName(), "call",fcm_token,my_id);

        ////

        try {
            type.put("video", isVideoForMe);
            type.put("audio", true);
            userObject.put("name", userModel.getUserName());
            userObject.put("image_profile", userModel.getImage());
            data.put("rcv_id", anthor_user_id);
            data.put("typeCall", "call");
            data.put("user", userObject);
            data.put("type", type);
            data.put("message", "");
            data.put("snd_id", my_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        serverApi.sendNotification(data.toString(), "call",fcm_token,my_id);
        System.out.println("call"+data.toString());
        service.putExtra(SocketIOService.EXTRA_CALL_PARAMTERS, data.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_CALLING);
        startService(service);

    }

    private void startCounter() {
        int i=0;
        countDownTimer = new CountDownTimer(30000, 1000) { //40000 milli seconds is total time, 1000 milli seconds is time interval

            public void onTick(long millisUntilFinished) {
                System.out.println(i+1);
            }
            public void onFinish() {
                SendMissingCall();
                finishCall ();

            }
        }.start();

    }
    private void startEndCallCounter() {
        System.out.println("startEndCallCounter");
        endCall=true;
        callStatusTV.setText(getResources().getString(R.string.call_ended));

        int j=0;
        countEndCallDownTimer = new CountDownTimer(4000, 1000) { //40000 milli seconds is total time, 1000 milli seconds is time interval

            public void onTick(long millisUntilFinished) {
                System.out.println(j+1);
            }
            public void onFinish() {
                System.out.println("EndEndCallCounter");

                if(isRining){
                    SendMissingCall();
                    finishCall ();
                }
                else  if (!PeerIdRecived.equals("no connect")) {

                    closeCall();
                    finishCall();
                }
                else{
                    finishCall ();
                }
            }
        }.start();

    }

    private void closeCall() {
        Intent service = new Intent(this, SocketIOService.class);
        JSONObject data = new JSONObject();
        JSONObject type = new JSONObject();
        JSONObject userObject = new JSONObject();
        try {
//            data.put("close_call", true);
            data.put("id", anthor_user_id);
//            data.put("snd_id", my_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("call");
        service.putExtra(SocketIOService.EXTRA_STOP_CALL_PARAMTERS, data.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_STOP_CALLING);
        startService(service);

    }
    private void sendSettingsCall(boolean video, boolean mic) {
        Intent service = new Intent(this, SocketIOService.class);
        JSONObject data = new JSONObject();
        System.out.println("isVideosetting"+video);
        try {
            data.put("snd_id", my_id);
            data.put("rcv_id", anthor_user_id);
            data.put("microphone", mic);
            data.put("camera", video);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        System.out.println("call");
        service.putExtra(SocketIOService.EXTRA_SETTINGS_CALL_PARAMTERS, data.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_SETTING_CALL);
        startService(service);

    }
    private void sendAskForVideoCall(boolean video) {
        Intent service = new Intent(this, SocketIOService.class);
        JSONObject data = new JSONObject();
        System.out.println("isVideosetting"+video);
        try {
            data.put("my_id", my_id);
            data.put("your_id", anthor_user_id);
            data.put("video", video);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        System.out.println("call");
        service.putExtra(SocketIOService.EXTRA_SEND_ASK_VIDEO_CALL_PARAMTERS, data.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_SEND_VIDEO_CALL_REQUEST);
        startService(service);

    }
    private void SendMissingCall() {
        Intent service = new Intent(this, SocketIOService.class);
        JSONObject data = new JSONObject();
        JSONObject type = new JSONObject();
        JSONObject userObject = new JSONObject();
//        serverApi.sendNotification(userModel.getUserName(), "call",fcm_token,my_id);

        ////

        try {

            userObject.put("name", userModel.getUserName());
            userObject.put("image_profile", userModel.getImage());
            data.put("rcv_id", anthor_user_id);
            data.put("typeCall", "missingCall");
            data.put("user", userObject);
            data.put("snd_id", my_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        serverApi.sendNotification(data.toString(), "call",fcm_token,my_id);
        System.out.println("call");
        service.putExtra(SocketIOService.EXTRA_CALL_PARAMTERS, data.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_CALLING);
        startService(service);

    }
    private void SendSwitchTOVideoCallRespone(boolean video) {
        Intent service = new Intent(this, SocketIOService.class);
        JSONObject data = new JSONObject();
        try {
            data.put("my_id", classSharedPreferences.getUser().getUserId());
            data.put("your_id", anthor_user_id);
            data.put("video", video);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        System.out.println("call");
        service.putExtra(SocketIOService.EXTRA_RESPONE_VIDEO_CALL_PARAMTERS, data.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_RESPONE_VIDEO_CALL);
        startService(service);

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_request_call);
        if (!isPermissionGranted()) {
            askPermissions();
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(recivePeerId, new IntentFilter(FETCH_PEER_ID));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveRining, new IntentFilter(ON_RINING_REQUEST));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveSettingsCalling, new IntentFilter(ON_RECIVED_SETTINGS_CALL));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveStopCalling, new IntentFilter(ON_STOP_CALLING_REQUEST));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveCloseCallFromNotification, new IntentFilter(ON_CLOSE_CALL_FROM_NOTIFICATION));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveAcceptChangeToVideoCall, new IntentFilter(ON_RECIVED_RESPONE_FOR_VIDEO));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveAskForCall, new IntentFilter(ON_RECIVED_ASK_FOR_VIDEO));







        Bundle bundle = getIntent().getExtras();
        serverApi = new ServerApi(this);
        anthor_user_id = bundle.getString("anthor_user_id", null);
        userName = bundle.getString("user_name", null);
        fcm_token = bundle.getString("fcm_token", null);
        isVideoForMe = bundle.getBoolean("isVideo", true);
        isVideoForYou = isVideoForMe;
        classSharedPreferences = new ClassSharedPreferences(this);
        my_id = classSharedPreferences.getUser().getUserId();
        userModel = classSharedPreferences.getUser();
        dialogForMe = new AlertDialog.Builder(this);

        startCall();
        mMediaPlayer = MediaPlayer.create(this, R.raw.outputcall);

        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);


        mAudioManager.setSpeakerphoneOn(false);
        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
//
        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                AudioManager.FX_KEY_CLICK);

        mMediaPlayer.setLooping(true);

        mMediaPlayer.start();


        webView = findViewById(R.id.webView);
        if (isVideoForMe) {
            webView.setVisibility(View.VISIBLE);
        } else {
            webView.setVisibility(View.GONE);

        }

        callLayout = findViewById(R.id.audio_only_Layout);

        userNameTv = findViewById(R.id.user_name);
        userNameTv.setText(userName);
        layoutCallProperties = findViewById(R.id.video_rl);
        callStatusTV = findViewById(R.id.txt_view_call_status);
        callStatusTV.setText(getResources().getString(R.string.calling));

//        openCloseIb = findViewById(R.id.image_video_call_layout);
        btnImageOpenCamera = findViewById(R.id.open_close_video);
        imgBtnStopCallLp = findViewById(R.id.close_call_layout);
        imgBtnOpenCameraCallLp = findViewById(R.id.image_video_call_layout);
        imgBtnOpenAudioCallLp = findViewById(R.id.image_audio_call_layout);
        imgBtnSwitchCamera = findViewById(R.id.image_switch_camera);


        btnImageOpenCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println(" closeOpenVideo()"+PeerIdRecived);

                    if (!PeerIdRecived.equals("no connect")) {
//                    closeOpenVideo();
                        isVideoForMe = !isVideoForMe;
                        sendAskForVideoCall(isVideoForMe);
                        callJavascriptFunction("javascript:toggleVideo(\"" + isVideoForMe + "\")");
                        callJavascriptFunction("javascript:toggleStream(\"" + isVideoForYou + "\")");





                        if(webView.getVisibility()==View.GONE){
                            webView.setVisibility(View.VISIBLE);
                            callLayout.setVisibility(View.GONE);
                            layoutCallProperties.setVisibility(View.VISIBLE);

                        }
                        showSwitchToVideoDialog(getResources().getString(R.string.requesting_to_switch_to_video_call));
                    }
                }
            });

//        openCloseIb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                closeOpenVideo();
//            }
//        });

        imageStopCalling= findViewById(R.id.img_button_stop_call);
        imageStopCalling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              startEndCallCounter();
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
                closeOpenVideo();

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



        setupWebView();
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

        callJavascriptFunction("javascript:init(\"" + uniqueId + "\")");
//        callJavascriptFunction("javascript:init(\"" + uniqueId + "\","+"\"" + my_id + "\")");



    }

    private void onCallRequest(String caller) {
        if (caller == null) return;

//                callLayout.visibility = View.VISIBLE
        callLayout.setVisibility(View.VISIBLE);
        layoutCallProperties.setVisibility(View.GONE);



        incomingCallTxt.setText("$caller is calling...");
    }


    private void switchToControls() {
        inputLayout.setVisibility(View.GONE);
        callControlLayout.setVisibility(View.VISIBLE);
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

    public void onPeerConnected(String string) {
        System.out.println("the key is" + string);

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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(recivePeerId);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveRining);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveSettingsCalling);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveStopCalling);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveCloseCallFromNotification);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveAcceptChangeToVideoCall);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveAskForCall);



        mMediaPlayer.release();

        countDownTimer.cancel();
//        countEndCallDownTimer.cancel();


        mAudioManager.setSpeakerphoneOn(false);
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(0);
        if (!PeerIdRecived.equals("no connect")) {

            closeCall();}




        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();

        super.onBackPressed();
    }
    public void closeOpenVideo() {
        isVideoForMe = !isVideoForMe;
        sendSettingsCall(isVideoForMe,true);

        if(webView.getVisibility()==View.GONE){
            webView.setVisibility(View.VISIBLE);
            callLayout.setVisibility(View.GONE);
            layoutCallProperties.setVisibility(View.VISIBLE);

        }
        else{
              if(!isVideoForMe&&!isVideoForYou) {
                  webView.setVisibility(View.GONE);
                  callLayout.setVisibility(View.VISIBLE);
                  layoutCallProperties.setVisibility(View.GONE);
              }
        }

        callJavascriptFunction("javascript:toggleVideo(\"" + isVideoForMe + "\")");
        if (isVideoForMe) {
            imgBtnOpenCameraCallLp.setImageResource(R.drawable.ic_baseline_videocam_off_24);
        } else {
            imgBtnOpenCameraCallLp.setImageResource(R.drawable.ic_baseline_videocam_24);
        }


//        toggleVideoBtn.setImageResource(if (isVideo) R.drawable.ic_baseline_videocam_24 else R.drawable.ic_baseline_videocam_off_24 )

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
    private void finishCall() {
        countDownTimer.cancel();
        callStatusTV.setText(R.string.call_ended);
//        mMediaPlayer.release();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                finish();
            }
        }, 500);
    }

    void showSwitchToVideoDialog(String message){
//        dialog.setMessage(getString(R.string.alert_delete_message));

        dialogForMe.setTitle(message);
        dialogForMe.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isVideoForMe = !isVideoForMe;
                    webView.setVisibility(View.GONE);
                    callLayout.setVisibility(View.VISIBLE);
                    layoutCallProperties.setVisibility(View.GONE);
                    sendAskForVideoCall(false);



            callJavascriptFunction("javascript:toggleVideo(\"" + isVideoForMe + "\")");

                dialog.dismiss();
            }
        });


        alertDialogForME = dialogForMe.create();
        alertDialogForME.show();
    }
    void showSwitchToVideoWhenANthorUserRequestDialog(String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//        dialog.setMessage(getString(R.string.alert_delete_message));
        dialog.setTitle(message);
        dialog.setPositiveButton(R.string.switch_to_video,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
//                        closeOpenVideo();
                        isVideoForMe = !isVideoForMe;
                        callJavascriptFunction("javascript:toggleVideo(\"" + isVideoForMe + "\")");


                        SendSwitchTOVideoCallRespone(isVideoForMe);
                        dialog.dismiss();

                    }
                });
        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                isVideoForYou=!isVideoForYou;
                SendSwitchTOVideoCallRespone(isVideoForMe);
                webView.setVisibility(View.GONE);
                callLayout.setVisibility(View.VISIBLE);
                layoutCallProperties.setVisibility(View.GONE);

            }
        });


        alertDialog = dialog.create();
        alertDialog.show();
    }
    private void showInCallNotification() {
//        Intent intent
//                = new Intent(this, CallNotificationActivity.class);
//        intent.putExtra("callRequest",message );
        Intent intent
                = new Intent(this, CallMainActivity.class);
        // Assign channel ID
        // Here FLAG_ACTIVITY_CLEAR_TOP flag is set to clear
        // the activities present in the activity stack,
        // on the top of the Activity that is to be launched
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Pass the intent to PendingIntent to start the
        // next Activity
        PendingIntent pendingIntent
                = PendingIntent.getActivity(this
                , 0, intent,
                PendingIntent.FLAG_ONE_SHOT|PendingIntent.FLAG_IMMUTABLE);
        /////////intent for reject
        Intent intentCancel
                = new Intent(this, CancelCallFromCallOngoingNotification.class);

        intentCancel.putExtra("id", anthor_user_id);

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

                .setFullScreenIntent(pendingIntent, true)

                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setSound(null)
                .setOngoing(true)


                .setVibrate(new long[]{10000, 10000})
                .setTicker("Call_STATUS")
                .addAction(R.drawable.btx_custom, HtmlCompat.fromHtml("<font color=\"" + ContextCompat.getColor(this, R.color.red) + "\">" +getResources().getString(R.string.cancel)+ " </font>", HtmlCompat.FROM_HTML_MODE_LEGACY), pendingIntentCancell)


                .setColorized(true)
                .setSmallIcon(R.drawable.ic_memo_logo);






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

