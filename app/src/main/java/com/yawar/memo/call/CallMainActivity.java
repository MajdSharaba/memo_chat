package com.yawar.memo.call;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
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
import com.yawar.memo.Api.ClassSharedPreferences;
import com.yawar.memo.R;
import com.yawar.memo.service.SocketIOService;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class CallMainActivity extends AppCompatActivity {
    String username = "";
    String friendsUsername = "";
    String peerId = null;
    Boolean isPeerConnected = false;
    private int requestcode = 1;
    public static final String ON_CALL_REQUEST = "CallMainActivity.ON_CALL_REQUEST";
    public static final String ON_STOP_CALLING_REQUEST = "CallMainActivity.ON_CALL_REQUEST";
    public static final String ON_RECIVED_SETTINGS_CALL = "CallMainActivity.ON_RECIVED_SETTINGS_CALL";





    Boolean isAudio = true;
    Boolean isVideo = true;
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

        private BroadcastReceiver reciveStopCalling = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String stopCallString = intent.getExtras().getString("get stopCalling");
                    finish();
                    JSONObject message = null;
//                    try {
////                        message = new JSONObject(stopCallString);
////                        boolean isStop = message.getBoolean("close_call");
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
    private BroadcastReceiver reciveSettingsCalling = new BroadcastReceiver() {
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
                        boolean videoSetting = message.getBoolean("camera");
                        boolean audioSetting = message.getBoolean("microphone");

                        if(videoSetting){
                            if(webView.getVisibility()==View.GONE){
                                webView.setVisibility(View.VISIBLE);
                                imageCallUser.setVisibility(View.GONE);
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
//        System.out.println("call");
        service.putExtra(SocketIOService.EXTRA_SETTINGS_CALL_PARAMTERS, data.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_SETTING_CALL);
        startService(service);

    }
    private void closeCall() {
        Intent service = new Intent(this, SocketIOService.class);
        JSONObject data = new JSONObject();
        try {
//            data.put("close_call", true);
            data.put("id",anotherUserId );
//            data.put("snd_id", classSharedPreferences.getUser().getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("call");
        service.putExtra(SocketIOService.EXTRA_STOP_CALL_PARAMTERS, data.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_STOP_CALLING);
        startService(service);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showWhenLockedAndTurnScreenOn();
        setContentView(R.layout.activity_call_main);
        if (!isPermissionGranted()) {
            askPermissions();
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(0);

        LocalBroadcastManager.getInstance(this).registerReceiver(reciveStopCalling, new IntentFilter(ON_STOP_CALLING_REQUEST));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveSettingsCalling, new IntentFilter(ON_RECIVED_SETTINGS_CALL));


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

//        inputLayout = findViewById(R.id.audio_only_Layout);
        layoutCallProperties = findViewById(R.id.video_rl);
        layoutCallProperties.setVisibility(View.VISIBLE);

//        callBtn = findViewById(R.id.callBtn);
        Bundle bundle = getIntent().getExtras();

        callString = bundle.getString("callRequest", "code");
        System.out.println("call string is that"+callString);
        setupWebView();

        JSONObject message = null;
        JSONObject userObject;
        JSONObject typeObject;

//        try {
//            message = new JSONObject(callString);
//            userObject = new JSONObject(message.getString("user"));
//            typeObject = new JSONObject(message.getString("type"));
//            isVideo = typeObject.getBoolean("video");
//            System.out.println("this is user object"+userObject.toString()+"lll"+isVideo);
//            username = userObject.getString("name");
//            anotherUserId = message.getString("snd_id");
//
//            if(isVideo){
//                webView.setVisibility(View.VISIBLE);
//            }
//            else {
//                webView.setVisibility(View.GONE);
//
//
//            }
//
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        incomingCallTxt.setText(getResources().getString(R.string.call_from)+" "+username);

//        callBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                friendsUsername = friendNameEdit.getText().toString();
//                sendCallRequest();
//
//            }
//        });
//        toggleAudioBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                isAudio = !isAudio;
//                callJavascriptFunction("javascript:toggleAudio(\"${isAudio}\")");
//                if (isAudio) {
//                    toggleAudioBtn.setImageResource(R.drawable.ic_mic);
//
//                } else {
//                    toggleAudioBtn.setImageResource(R.drawable.ic_more);
//
//                }
//
//            }
//        });
//
//        toggleVideoBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                System.out.println("closeOpenVideo()");
//                closeOpenVideo();
//
//
//            }
//        });

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("acceptBtn"+callString);

                if(callString!=null){
                    System.out.println("call Request"+callString);
                    if(peerId!=null){
//                    sendPeerId(callString,peerId);
                    callLayout.setVisibility(View.GONE);
                    layoutCallProperties.setVisibility(View.VISIBLE);
                    if(isVideo){
                        imageCallUser.setVisibility(View.GONE);
                    }
                    else{
                        imageCallUser.setVisibility(View.GONE);

                    }



                    }
                }

//            switchToControls();

            }
        });


        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callLayout.setVisibility(View.GONE);
//                closeCall();
//                sendPeerId(callString,"null");

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

//     setupWebView();

    }


    private void sendCallRequest() {
        if (!isPeerConnected) {
            Toast.makeText(this, "You're not connected. Check your internet", Toast.LENGTH_LONG).show();
            return;
        }

//        firebaseRef.child(friendsUsername).child("incoming").setValue(username)
//        firebaseRef.child(friendsUsername).child("isAvailable").addValueEventListener(object: ValueEventListener {
//            override fun onCancelled(error: DatabaseError) {}
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//
//                if (snapshot.value.toString() == "true") {
//                    listenForConnId()
//                }
//
//            }

//        })

    }

    //    private void setupWebView() {
//
////        webView.webChromeClient = object: WebChromeClient() {
//        webView.setWebChromeClient(new WebChromeClient(){
//            @Override
//            public void onPermissionRequest(final PermissionRequest request) {
//                request.grant(request.getResources());
//
//            }
//
////            override fun onPermissionRequest(request: PermissionRequest?) {
////                request?.grant(request.resources)
////            }
//        });
//
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
////        webView.addJavascriptInterface(new JavascriptInterface(CallMainActivity.this), "Android");
//
//        loadVideoCall();
//    }
    private void setupWebView() {

//        webView.webChromeClient = object: WebChromeClient() {
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
        webView.addJavascriptInterface(new JavascriptInterface(CallMainActivity.this), "Android");

        loadVideoCall();
    }

    //    private void loadVideoCall() {
//        String filePath = "file:android_asset/call.html";
//        webView.loadUrl(filePath);
//
////        webView.webViewClient = object: WebViewClient() {
////            override fun onPageFinished(view: WebView?, url: String?) {
////                initializePeer()
////            }
////        }
//        webView.setWebViewClient(new WebViewClient(){
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                                initializePeer();
//
//            }
//        });
//    }
    private void loadVideoCall() {
        String filePath = "file:android_asset/call.html";
        System.out.println("load videooooooooooooooo" + filePath);
        webView.loadUrl(filePath);

//        webView.webViewClient = object: WebViewClient() {
//            override fun onPageFinished(view: WebView?, url: String?) {
//                initializePeer()
//            }
//        }
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
//        System.out.println(uniqueId+"uniqueId");

        callJavascriptFunction("javascript:init(\"" + isVideo + "\")");
//        firebaseRef.child(username).child("incoming").addValueEventListener(object: ValueEventListener {
//            override fun onCancelled(error: DatabaseError) {}
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//                println("value is "+snapshot.value)
//                onCallRequest(snapshot.value as? String)
//            }

//        })

    }

    private void onCallRequest(String caller) {
        if (caller == null) return;

//                callLayout.visibility = View.VISIBLE
//        callLayout.setVisibility(View.VISIBLE);
        incomingCallTxt.setText("$caller is calling...");
        layoutCallProperties.setVisibility(View.VISIBLE);

    }





    private void switchToControls() {
        inputLayout.setVisibility(View.GONE);
        layoutCallProperties.setVisibility(View.VISIBLE);
    }
//    private String getUniqueID() {
//        return UUID.randomUUID().toString();
//    }
//    private void callJavascriptFunction(String functionString) {
//        webView.post(new Runnable() {
//            @Override
//            public void run() {
//                webView.evaluateJavascript(functionString, null);
//            }
//        });
//    }
//   public void onPeerConnected( String string) {
//        System.out.println("the key is"+string);
//        isPeerConnected = true;
//    }
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
            callJavascriptFunction("javascript:init(\"" + isVideo + "\")");

        }
//        isPeerConnected = true;
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




        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
                finish();

        super.onBackPressed();
    }
    public void closeOpenVideo() {
        isVideo = !isVideo;
        if(webView.getVisibility()==View.GONE){
            webView.setVisibility(View.VISIBLE);
            layoutCallProperties.setVisibility(View.VISIBLE);
            imageCallUser.setVisibility(View.GONE);

            sendSettingsCall(isVideo,true);

        }
        callJavascriptFunction("javascript:toggleVideo(\"" + isVideo + "\")");
        if (isVideo) {
            imgBtnOpenCameraCallLp.setImageResource(R.drawable.ic_baseline_videocam_off_24);
        } else {
            imgBtnOpenCameraCallLp.setImageResource(R.drawable.ic_baseline_videocam_24);
        }


//        toggleVideoBtn.setImageResource(if (isVideo) R.drawable.ic_baseline_videocam_24 else R.drawable.ic_baseline_videocam_off_24 )

    }
    public void closeOpenAudio(){
        isAudio = !isAudio;
        callJavascriptFunction("javascript:toggleAudio(\"" + isAudio + "\")");
//        callJavascriptFunction("javascript:toggleAudio(\"${isAudio}\")");
        if (isAudio) {
            imgBtnOpenAudioCallLp.setImageResource(R.drawable.ic_baseline_mic_off_24);
        } else {
            imgBtnOpenAudioCallLp.setImageResource(R.drawable.ic_baseline_mic_24);
        }

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
}