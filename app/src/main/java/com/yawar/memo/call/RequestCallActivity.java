package com.yawar.memo.call;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

import com.yawar.memo.Api.ClassSharedPreferences;
import com.yawar.memo.Api.ServerApi;
import com.yawar.memo.R;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.service.SocketIOService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;
public class RequestCallActivity extends AppCompatActivity {

    Boolean isPeerConnected = false;
    ServerApi serverApi;
    private int requestcode = 1;
    public static final String ON_STOP_CALLING_REQUEST = "RequestCallActivity.ON_STOP_CALLING_REQUEST";
    public static final String ON_RINING_REQUEST = "RequestCallActivity.ON_RINING_REQUEST";

    TextView userNameTv;
    UserModel userModel;
    String fcm_token;


//    var firebaseRef = Firebase.database.getReference("users")

    Boolean isAudio = true;
    Boolean isVideo = true;
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
    String PeerIdRecived = "null";



    private BroadcastReceiver reciveRining = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String stopCallString = intent.getExtras().getString("get rining");
                    JSONObject message = null;
                    try {
                        message = new JSONObject(stopCallString);
                        boolean isRining = message.getBoolean("call");
                        if(isRining){
                            callStatusTV.setText(getResources().getString(R.string.rining));
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
                                callLayout.setVisibility(View.GONE);
                                layoutCallProperties.setVisibility(View.VISIBLE);
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
    private BroadcastReceiver recivePeerId = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String callString = intent.getExtras().getString("fetchPeer");
                    System.out.println("FETCH_PEER_ID" + callString);
                    JSONObject message = null;
                    ///////////
                    try {

                        message = new JSONObject(callString);
                        if(message.getString("peerId")!=null){
                        PeerIdRecived = message.getString("peerId");}


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.println(PeerIdRecived + "reciveeeePeeer");
                    if (!PeerIdRecived.equals("null")) {
                        callStatusTV.setText("تم الرد");
                        callJavascriptFunction("javascript:startCall(\"" + PeerIdRecived + "\"," + "\"" + isVideo + "\")");
                        if (isVideo) {
                            callLayout.setVisibility(View.GONE);
                            layoutCallProperties.setVisibility(View.VISIBLE);
                        } else {
//                            callStatusTV.setText("تم الرد");
                            callLayout.setVisibility(View.VISIBLE);
                            layoutCallProperties.setVisibility(View.GONE);

                        }
                    }
                    else {
//                        finish();
                    }
                }
            });
        }
    };
    private BroadcastReceiver reciveStopCalling = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String stopCallString = intent.getExtras().getString("get stopCalling");
                    JSONObject message = null;
                    finish();

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
        Intent service = new Intent(this, SocketIOService.class);
        JSONObject data = new JSONObject();
        JSONObject type = new JSONObject();
        JSONObject userObject = new JSONObject();
        System.out.println("this is notification"+userModel.getUserName()+fcm_token+anthor_user_id);
//        serverApi.sendNotification(userModel.getUserName(), "call",fcm_token,my_id);

        ////

        try {
            type.put("video", isVideo);
            type.put("audio", true);
            userObject.put("name", userModel.getUserName());
            userObject.put("image_profile", userModel.getImage());
            data.put("rcv_id", anthor_user_id);
            data.put("user", userObject.toString());
            data.put("type", type);
            data.put("message", "");
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




        Bundle bundle = getIntent().getExtras();
        serverApi = new ServerApi(this);
        anthor_user_id = bundle.getString("anthor_user_id", null);
        userName = bundle.getString("user_name", null);
        fcm_token = bundle.getString("fcm_token", null);
        isVideo = bundle.getBoolean("isVideo", true);
        classSharedPreferences = new ClassSharedPreferences(this);
        my_id = classSharedPreferences.getUser().getUserId();
        userModel = classSharedPreferences.getUser();


        startCall();


        webView = findViewById(R.id.webView);
        if (isVideo) {
            webView.setVisibility(View.VISIBLE);
        } else {
            webView.setVisibility(View.GONE);

        }

        callLayout = findViewById(R.id.audio_only_Layout);
        userNameTv = findViewById(R.id.user_name);
        userNameTv.setText(userName);
        layoutCallProperties = findViewById(R.id.video_rl);
        callStatusTV = findViewById(R.id.txt_view_call_status);
//        openCloseIb = findViewById(R.id.image_video_call_layout);
        btnImageOpenCamera = findViewById(R.id.open_close_video);
        imgBtnStopCallLp = findViewById(R.id.close_call_layout);
        imgBtnOpenCameraCallLp = findViewById(R.id.image_video_call_layout);
        imgBtnOpenAudioCallLp = findViewById(R.id.image_audio_call_layout);
        imgBtnSwitchCamera = findViewById(R.id.image_switch_camera);


        btnImageOpenCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!PeerIdRecived.equals("null")) {
                    closeOpenVideo();}
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
                closeCall();
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
                System.out.println("callinggggggggggg");
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






        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();

        super.onBackPressed();
    }
    public void closeOpenVideo() {
        isVideo = !isVideo;
        sendSettingsCall(isVideo,true);
        if(webView.getVisibility()==View.GONE){
            webView.setVisibility(View.VISIBLE);
            callLayout.setVisibility(View.GONE);
            layoutCallProperties.setVisibility(View.VISIBLE);

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
                if (isAudio) {
                    imgBtnOpenAudioCallLp.setImageResource(R.drawable.ic_baseline_mic_off_24);
                } else {
                    imgBtnOpenAudioCallLp.setImageResource(R.drawable.ic_baseline_mic_24);
                }

    }
}