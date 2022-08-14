package com.yawar.memo.call;
import static org.webrtc.SessionDescription.Type.ANSWER;
import static org.webrtc.SessionDescription.Type.OFFER;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
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
import android.media.AudioDeviceInfo;
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

import com.baoyz.swipemenulistview.SwipeMenuView;
import com.yawar.memo.databinding.ActivityCompleteBinding;
import com.yawar.memo.databinding.ActivityRequestCallBinding;
import com.yawar.memo.fragment.RequestCallAudioAndVideoFragment;
import com.yawar.memo.modelView.RequestCallViewModel;
import com.yawar.memo.notification.CancelCallFromCallOngoingNotification;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.Api.ServerApi;
import com.yawar.memo.R;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.service.SocketIOService;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.CandidatePairChangeEvent;
import org.webrtc.DataChannel;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;

import org.webrtc.RtpReceiver;
import org.webrtc.RtpTransceiver;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;
//import org.webrtc.VideoRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.socket.client.Socket;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class RequestCallActivity extends AppCompatActivity {
    private static final String TAG = "CompleteActivity";
    private static final int RC_CALL = 111;
    public static final String VIDEO_TRACK_ID = "ARDAMSv0";
    public static final int VIDEO_RESOLUTION_WIDTH = 1280;
    public static final int VIDEO_RESOLUTION_HEIGHT = 720;
    public static final int FPS = 30;
    boolean isVideo = false;
    private AudioManager audioManager;
    private boolean isSpeakerOn = false;


    private boolean isInitiator;
    private boolean isChannelReady;
    private boolean isStarted;
    String anthor_user_id;
    VideoCapturer videoCapturer;


    MediaConstraints audioConstraints;
    MediaConstraints videoConstraints;
    MediaConstraints sdpConstraints;
    VideoSource videoSource;
    VideoTrack localVideoTrack;
    AudioSource audioSource;
    AudioTrack localAudioTrack;
    SurfaceTextureHelper surfaceTextureHelper;
    ClassSharedPreferences classSharedPreferences;
    public static final String ON_RECIVE_MESSAGE_VIDEO_CALL = "ON_RECIVE_MESSAGE_VIDEO_CALL";


    private ActivityRequestCallBinding binding;
    private PeerConnection peerConnection;
    private EglBase rootEglBase;
    private PeerConnectionFactory factory;
    private VideoTrack videoTrackFromCamera;

    Boolean isPeerConnected = false;
    boolean isRining = false;
    boolean isVideoForMe = false;
    boolean isVideoForYou = false;
    String onGoingTitle = "";


    RequestCallViewModel requestCallViewModel;


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


//    TextView userNameTv;
    UserModel userModel;
    String fcm_token;
//    String TAG = "RequestCallActivity";
    Boolean isAudio = true;
    LinearLayout layoutCallProperties;
//    ImageButton imageStopCalling;
//    ImageButton btnImageOpenCamera;
//    ImageButton btnImageOpenAudio;
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
//    WebView webView;
    TextView incomingCallTxt;
//    LinearLayout callLayout;
    RelativeLayout inputLayout;
    LinearLayout callControlLayout;
    String callString = null;

    String userName;
    String my_id;
//    String PeerIdRecived = "no connect";
//    public static final String ON_CLOSE_CALL_FROM_NOTIFICATION = "CallNotificationActivity.ON_RINING_REQUEST";




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
                         requestCallViewModel.rining.setValue(isRining);


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
//                    isVideoForYou = !isVideoForYou;

                    String stopCallString = intent.getExtras().getString("get responeAskVideo");
                    System.out.println("get responeAskVideo");
                    JSONObject message = null;
                    try {
                        message = new JSONObject(stopCallString);
                        isVideoForYou = message.getBoolean("video");
                        requestCallViewModel.isVideoForYou.setValue(isVideoForYou);

                        if (!isVideoForYou){
                            requestCallViewModel.isVideoForMe.setValue(false);
                    }
                        else {
                            requestCallViewModel.setIsSpeaker(isVideoForYou);

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

                    String stopCallString = intent.getExtras().getString("get settings");
                    JSONObject message = null;
                    try {
                        message = new JSONObject(stopCallString);
                         isVideoForYou = message.getBoolean("camera");
                        boolean audioSetting = message.getBoolean("microphone");
//                        callJavascriptFunction("javascript:toggleStream(\"" +isVideoForYou  + "\")");
                        requestCallViewModel.isVideoForYou.setValue(isVideoForYou);

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

                    String stopCallString = intent.getExtras().getString("get askVideo");
                    JSONObject message = null;
                    try {
                        message = new JSONObject(stopCallString);
                        isVideoForYou = message.getBoolean("video");

                         requestCallViewModel.isVideoForYou.setValue(isVideoForYou);


                        if(isVideoForYou) {

                            showSwitchToVideoWhenANthorUserRequestDialog( getResources().getString(R.string.alert_switch_to_video_from_anthor_message));

                        } else {

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
                    requestCallViewModel.rining.setValue(false);
                    String call_peer_id = "no connect";
                    JSONObject message = null;
                    ///////////
                    try {

                        message = new JSONObject(callString);
                        System.out.println("this is message"+message.getString("peerId"));
                        if(message.getString("peerId")!=null){
                            call_peer_id = message.getString("peerId");
                            requestCallViewModel.peerIdRecived.setValue(call_peer_id);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
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



                    ///////////


                }
            });
        }
    };


    private final BroadcastReceiver reciveMessageCall = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String unBlockString = intent.getExtras().getString("Call Sdp");
                    System.out.println("unBlockString"+unBlockString);



                    String id = "";
                    String type = "";
                    String sdp = "";

                    try {
                        JSONObject jsonObject = new JSONObject(unBlockString);
                        id = jsonObject.getString("your_id");
                        type = jsonObject.getString("type");
                        String anthor_id = jsonObject.getString("my_id");

//                        if (type.equals("got user media")) {
//                            if(!anthor_id.equals(classSharedPreferences.getUser().getUserId())){
//                                System.out.println("got user media");
//                                maybeStart();}
//
//                        }

//                        if (type.equals("got user media") && isStarted) {
//                            maybeStart();
//
//                        }


                        if (type.equals("offer")) {

                            System.out.println("type offer");
                            sdp = jsonObject.getString("sdp");




//                                maybeStart();

//                            peerConnection.setRemoteDescription(new SimpleSdpObserver(), new SessionDescription(ANSWER, message.getString("sdp")));
                            if (id.equals(classSharedPreferences.getUser().getUserId())) {
                                System.out.println("iduser" + sdp);

                                peerConnection.setRemoteDescription(new SimpleSdpObserver(), new SessionDescription(OFFER, sdp));
                                requestCallViewModel.peerIdRecived.setValue("opwn");
                                requestCallViewModel.rining.setValue(false);

                                doAnswer();
                            }
                        }


                        else if (type.equals("answer") ) {
                            sdp = jsonObject.getString("sdp");
                            if (id.equals(classSharedPreferences.getUser().getUserId())) {
                                System.out.println("answermessageeeee"+sdp);

                                peerConnection.setRemoteDescription(new SimpleSdpObserver(), new SessionDescription(ANSWER, sdp));}}
                        else if (type.equals("candidate")) {
                            if (id.equals(classSharedPreferences.getUser().getUserId())) {

                                Log.d(TAG, "connectToSignallingServer: receiving candidates");
                                IceCandidate candidate = new IceCandidate(jsonObject.getString("id"), jsonObject.getInt("label"), jsonObject.getString("candidate"));
                                peerConnection.addIceCandidate(candidate);
                            }
                        }




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

///////////////////////////
    private void sendMessage( Object object) {
//        System.out.println("sendMessage"+object.toString());
        Log.d(TAG, "sendMessage: "+object.toString());
        Intent service = new Intent(this, SocketIOService.class);
//        JSONObject object = new JSONObject();

        service.putExtra(SocketIOService.EXTRA_SEND_MESSAGE_FOR_CALL_PARAMTES, object.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_SEND_MESSAGE_FOR_CALL);
        this.startService(service);
    }
////////////////////////////

    private void startCall() {
        startCounter();
        Intent service = new Intent(this, SocketIOService.class);
        JSONObject data = new JSONObject();
        JSONObject type = new JSONObject();
        JSONObject userObject = new JSONObject();
//        serverApi.sendNotification(userModel.getUserName(), "call",fcm_token,my_id);

        ////

        try {
            type.put("video", requestCallViewModel.isVideoForMe.getValue());
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


        callStatusTV.setText(getResources().getString(R.string.call_ended));

        int j=0;
        countEndCallDownTimer = new CountDownTimer(4000, 1000) { //40000 milli seconds is total time, 1000 milli seconds is time interval

            public void onTick(long millisUntilFinished) {
                System.out.println(j+1);
            }
            public void onFinish() {
                System.out.println("EndEndCallCounter");

                if(requestCallViewModel.rining.getValue()){
                    SendMissingCall();
                    finishCall ();
                }
                else  if (!requestCallViewModel.peerIdRecived.getValue().equals("no connect")) {

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
        service.putExtra(SocketIOService.EXTRA_SEND_ASK_VIDEO_CALL_PARAMTERS, data.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_SEND_VIDEO_CALL_REQUEST);
        startService(service);

    }
    private void SendMissingCall() {
        Intent service = new Intent(this, SocketIOService.class);
        JSONObject data = new JSONObject();
        JSONObject type = new JSONObject();
        JSONObject userObject = new JSONObject();

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
        service.putExtra(SocketIOService.EXTRA_RESPONE_VIDEO_CALL_PARAMTERS, data.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_RESPONE_VIDEO_CALL);
        startService(service);

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_request_call);


        LocalBroadcastManager.getInstance(this).registerReceiver(recivePeerId, new IntentFilter(FETCH_PEER_ID));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveRining, new IntentFilter(ON_RINING_REQUEST));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveSettingsCalling, new IntentFilter(ON_RECIVED_SETTINGS_CALL));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveStopCalling, new IntentFilter(ON_STOP_CALLING_REQUEST));
//        LocalBroadcastManager.getInstance(this).registerReceiver(reciveCloseCallFromNotification, new IntentFilter(ON_CLOSE_CALL_FROM_NOTIFICATION));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveAcceptChangeToVideoCall, new IntentFilter(ON_RECIVED_RESPONE_FOR_VIDEO));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveAskForCall, new IntentFilter(ON_RECIVED_ASK_FOR_VIDEO));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveMessageCall, new IntentFilter(ON_RECIVE_MESSAGE_VIDEO_CALL));


        requestCallViewModel = new ViewModelProvider(this).get(RequestCallViewModel.class);


        Bundle bundle = getIntent().getExtras();
        serverApi = new ServerApi(this);
        anthor_user_id = bundle.getString("anthor_user_id", null);
        userName = bundle.getString("user_name", null);
        fcm_token = bundle.getString("fcm_token", null);
        isVideoForMe = bundle.getBoolean("isVideo", true);
        if (isVideoForMe) {
            changeFragment();
            onGoingTitle = getResources().getString(R.string.ongoing_video_call);
        } else {
            onGoingTitle = getResources().getString(R.string.ongoing_audio_call);

        }
        requestCallViewModel.setIsSpeaker(isVideoForMe);

        requestCallViewModel.isVideoForMe.setValue(isVideoForMe);
        requestCallViewModel.isVideoForYou.setValue(isVideoForMe);
//        callLayout = findViewById(R.id.audio_only_Layout);

        binding.userName.setText(userName);


        layoutCallProperties = findViewById(R.id.video_rl);
        callStatusTV = findViewById(R.id.txt_view_call_status);
        callStatusTV.setText(getResources().getString(R.string.calling));

        imgBtnStopCallLp = findViewById(R.id.close_call_layout);
        imgBtnOpenCameraCallLp = findViewById(R.id.image_video_call_layout);
        imgBtnOpenAudioCallLp = findViewById(R.id.image_audio_call_layout);
        imgBtnSwitchCamera = findViewById(R.id.image_switch_camera);


        classSharedPreferences = new ClassSharedPreferences(this);
        my_id = classSharedPreferences.getUser().getUserId();
        userModel = classSharedPreferences.getUser();
        dialogForMe = new AlertDialog.Builder(this);
        start();

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
        binding.audioOnlyLayout.setVisibility(View.VISIBLE);
        binding.webRtcRelativeLayout.setVisibility(View.GONE);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);



//        webView = findViewById(R.id.webView);

        requestCallViewModel.isVideoForMe.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
                if (s) {
                    if (!requestCallViewModel.peerIdRecived.getValue().equals("no connect")) {

                        binding.webRtcRelativeLayout.setVisibility(View.VISIBLE);
                        binding.audioOnlyLayout.setVisibility(View.GONE);
                        layoutCallProperties.setVisibility(View.VISIBLE);
                        if(videoCapturer!=null) {
                            videoCapturer.startCapture(VIDEO_RESOLUTION_WIDTH, VIDEO_RESOLUTION_HEIGHT, FPS);

                        }

                        imgBtnOpenCameraCallLp.setImageResource(R.drawable.ic_baseline_videocam_off_24);
                        binding.openCloseVideo.setImageResource(R.drawable.ic_baseline_videocam_off_24);
                    }
                } else {
                    try {
                        videoCapturer.stopCapture();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    imgBtnOpenCameraCallLp.setImageResource(R.drawable.ic_baseline_videocam_24);
                    binding.openCloseVideo.setImageResource(R.drawable.ic_baseline_videocam_24);
                    if(!requestCallViewModel.isVideoForYou.getValue()) {

                        binding.webRtcRelativeLayout.setVisibility(View.GONE);
                        binding.audioOnlyLayout.setVisibility(View.VISIBLE);
                        layoutCallProperties.setVisibility(View.GONE);

                    }

                }
//                local.setEnabled(s);
                localVideoTrack.setEnabled(s);


            }
        });
        requestCallViewModel.isVideoForYou.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
                if (s) {
                    if (!requestCallViewModel.peerIdRecived.getValue().equals("no connect")) {

                        binding.webRtcRelativeLayout.setVisibility(View.VISIBLE);
                        binding.audioOnlyLayout.setVisibility(View.GONE);
                        layoutCallProperties.setVisibility(View.VISIBLE);
                    }
                } else {

                    if (!requestCallViewModel.isVideoForMe.getValue()) {
                        binding.webRtcRelativeLayout.setVisibility(View.GONE);
                        binding.audioOnlyLayout.setVisibility(View.VISIBLE);
                        layoutCallProperties.setVisibility(View.GONE);

                    }


                }
//               callJavascriptFunction("javascript:toggleStream(\"" + s+ "\")");

            }
        });
        requestCallViewModel.rining.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
                if (s) {
                    if (!requestCallViewModel.endCall.getValue()) {
                        callStatusTV.setText(getResources().getString(R.string.rining));
                        mMediaPlayer.release();
                        mMediaPlayer = MediaPlayer.create(RequestCallActivity.this, R.raw.ring);
                        mMediaPlayer.setLooping(true);

                        mMediaPlayer.start();
                        countDownTimer.cancel();
                        countDownTimer.start();
                    }


                }

            }
        });
        requestCallViewModel.endCall.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
                if (s) {
                    startEndCallCounter();


                }
            }
        });

        requestCallViewModel.isAudio.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
//                callJavascriptFunction("javascript:toggleAudio(\"" + s + "\")");

                if (s) {

                    imgBtnOpenAudioCallLp.setImageResource(R.drawable.ic_baseline_mic_off_24);
                    binding.openCloseAudio.setImageResource(R.drawable.ic_baseline_mic_off_24);


                } else {
                    imgBtnOpenAudioCallLp.setImageResource(R.drawable.ic_baseline_mic_24);
                    binding.openCloseVideo.setImageResource(R.drawable.ic_baseline_mic_24);


                }
                localAudioTrack.setEnabled(s);

            }
        });
        requestCallViewModel.isSpeaker.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {

                if (s) {
                    toggleSpeaker(s);
                    binding.closeSpeakers.setImageResource(R.drawable.audio_speaker);




                } else {
                    toggleSpeaker(s);
                    binding.closeSpeakers.setImageResource(R.drawable.ic_speakers_off);


//                    imgBtnOpenAudioCallLp.setImageResource(R.drawable.ic_baseline_mic_24);
//                    binding.openCloseVideo.setImageResource(R.drawable.ic_baseline_mic_24);


                }
            }
        });

        requestCallViewModel.peerIdRecived.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                System.out.println("peer id is" + s);
                if (s.equals("null")) {
//                    PeerIdRecived= call_peer_id;

                    finishCall();

                } else if (s.equals("no connect")) {

                } else {
//                    PeerIdRecived = call_peer_id;

                    if (!requestCallViewModel.endCall.getValue()) {
                        callStatusTV.setText(R.string.replied);

                        requestCallViewModel.isVideoForMe.setValue(requestCallViewModel.isVideoForMe.getValue());


                        ///for stop 15 sec counter
                        countDownTimer.cancel();
                        ////for close rining
                        mMediaPlayer.release();
                        ///for shw call notification
                        showInCallNotification();


//                        callJavascriptFunction("javascript:startCall(\"" + s + "\"," + "\"" + isVideoForMe + "\")");

                    }
                }
            }
        });


        binding.openCloseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!requestCallViewModel.peerIdRecived.getValue().equals("no connect")) {

                    requestCallViewModel.isVideoForMe.setValue(!requestCallViewModel.isVideoForMe.getValue());
                    sendAskForVideoCall(requestCallViewModel.isVideoForMe.getValue());


//
//                        }
                    showSwitchToVideoDialog(getResources().getString(R.string.requesting_to_switch_to_video_call));
                }
            }
        });


        binding.openCloseAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!requestCallViewModel.peerIdRecived.getValue().equals("no connect")) {
                    requestCallViewModel.isAudio.setValue(!requestCallViewModel.isAudio.getValue());


                }


            }
        });
        binding.closeSpeakers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestCallViewModel.setIsSpeaker(!requestCallViewModel.isSpeaker.getValue());
            }
        });


//        imageStopCalling= findViewById(R.id.img_button_stop_call);
        binding.imgButtonStopCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestCallViewModel.endCall.setValue(true);

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
              switchCamera();

            }
        });
    }
@AfterPermissionGranted(RC_CALL)
private void start() {
    String[] perms = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
    if (EasyPermissions.hasPermissions(this, perms)) {

//            connectToSignallingServer();

        initializeSurfaceViews();

//        initializePeerConnectionFactory();
        createPeerConnectionFactory();


        createVideoTrackFromCameraAndShowIt();


        initializePeerConnections();

        startStreamingVideo();
        connectToSignallingServer();


    } else {
        EasyPermissions.requestPermissions(this, "Need some permissions", RC_CALL, perms);
    }
}

    @Override
    protected void onDestroy() {
//        webView.loadUrl("about:blank");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(recivePeerId);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveRining);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveSettingsCalling);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveStopCalling);
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveCloseCallFromNotification);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveAcceptChangeToVideoCall);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveAskForCall);



        mMediaPlayer.release();

        countDownTimer.cancel();
//        countEndCallDownTimer.cancel();


        mAudioManager.setSpeakerphoneOn(false);
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(0);
        if (!requestCallViewModel.peerIdRecived.getValue().equals("no connect")&&!requestCallViewModel.peerIdRecived.getValue().equals("null")) {

            closeCall();
        }
        callDisconnect();
//        if (surfaceTextureHelper != null) {
//            surfaceTextureHelper.dispose();
//            surfaceTextureHelper = null;
//
//        }
//        if( binding.surfaceView2 != null){
//            binding.surfaceView2.init(null,null);
//        }
//        if( binding.surfaceView != null){
//            binding.surfaceView.init(null,null);
//        }
//        peerConnection.dispose();
//        videoCapturer.dispose();
//        factory.dispose();;
//        rootEglBase.release();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveMessageCall);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();

        super.onBackPressed();
    }
    public void closeOpenVideo() {
        requestCallViewModel.isVideoForMe.setValue(!requestCallViewModel.isVideoForMe.getValue());


        sendSettingsCall(requestCallViewModel.isVideoForMe.getValue(),true);


    }
   public void closeOpenAudio(){
        requestCallViewModel.isAudio.setValue(!requestCallViewModel.isAudio.getValue());
//       isAudio = !isAudio;
//       callJavascriptFunction("javascript:toggleAudio(\"" + isAudio + "\")");
//                if (isAudio) {
//                    imgBtnOpenAudioCallLp.setImageResource(R.drawable.ic_baseline_mic_off_24);
//                } else {
//                    imgBtnOpenAudioCallLp.setImageResource(R.drawable.ic_baseline_mic_24);
//                }

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

        dialogForMe.setTitle(message);
        dialogForMe.setCancelable(false);

        dialogForMe.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestCallViewModel.isVideoForMe.setValue(!requestCallViewModel.isVideoForMe.getValue());

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
        dialog.setCancelable(false);

        dialog.setPositiveButton(R.string.switch_to_video,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {

                       requestCallViewModel.isVideoForMe.setValue(!requestCallViewModel.isVideoForMe.getValue());
                        requestCallViewModel.setIsSpeaker(true);



                        SendSwitchTOVideoCallRespone(requestCallViewModel.isVideoForMe.getValue());
                        dialog.dismiss();

                    }
                });
        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 requestCallViewModel.isVideoForYou.setValue(!requestCallViewModel.isVideoForYou.getValue());

                SendSwitchTOVideoCallRespone(requestCallViewModel.isVideoForMe.getValue());
                                dialog.dismiss();


            }
        });


        alertDialog = dialog.create();
        alertDialog.show();
    }
    private void showInCallNotification() {

        Intent intent
                = new Intent(this, ResponeCallActivity.class);
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

                .setFullScreenIntent(null, true)

                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setSound(null)
                .setOngoing(true)


                .setVibrate(new long[]{10000, 10000})
                .setTicker("Call_STATUS")
                .addAction(R.drawable.btx_custom, getResources().getString(R.string.cancel), pendingIntentCancell)


                .setSmallIcon(R.drawable.ic_memo_logo)
                .setContentTitle(userName)
                .setContentText(onGoingTitle);










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
    private void connectToSignallingServer() {

        isInitiator = true;

        isChannelReady = true;

    }
    //MirtDPM4
    private void doAnswer() {
        System.out.println("doAnswermajdddd");

        peerConnection.createAnswer(new SimpleSdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                System.out.println("onCreateSuccessdoAnswer");
                peerConnection.setLocalDescription(new SimpleSdpObserver(), sessionDescription);
                System.out.println();

                JSONObject message = new JSONObject();
                try {
                    message.put("type", "answer");
                    message.put("sdp", sessionDescription.description);
                    message.put("my_id", classSharedPreferences.getUser().getUserId());
                    message.put("your_id", anthor_user_id);
                } catch (JSONException e) {
                    System.out.println("ERRRRRORRRRRRR");
                    e.printStackTrace();
                }
                System.out.println("message.message");

                sendMessage(message);

            }
        }, new MediaConstraints());
    }

    private void maybeStart() {
        Log.d(TAG, "maybeStart: " + isStarted + " " + isChannelReady);
        if (!isStarted && isChannelReady) {
            isStarted = true;
            if (isInitiator) {
                doCall();
            }
        }
    }

    private void doCall() {
        MediaConstraints sdpMediaConstraints = new MediaConstraints();
        sdpMediaConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        sdpMediaConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        peerConnection.createOffer(new SimpleSdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                Log.d(TAG, "onCreateSuccess: ");
                peerConnection.setLocalDescription(new SimpleSdpObserver(), sessionDescription);
                JSONObject message = new JSONObject();

                try {
                    message.put("type", "offer");
                    message.put("sdp", sessionDescription.description);
                    message.put("your_id", anthor_user_id);
                    message.put("my_id", classSharedPreferences.getUser().getUserId());

//                    if(!classSharedPreferences.getUser().getUserId().equals("191")){
                    sendMessage(message);
//                }
//                    System.out.println("messageeeeeeeee"+message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, sdpMediaConstraints);
    }

//    private void sendMessage(Object message) {
//        socket.emit("message", message);
//    }


    private void initializeSurfaceViews() {
        rootEglBase = EglBase.create();
        binding.surfaceView.init(rootEglBase.getEglBaseContext(), null);
        binding.surfaceView.setEnableHardwareScaler(true);
        binding.surfaceView.setMirror(true);

        binding.surfaceView2.init(rootEglBase.getEglBaseContext(), null);
        binding.surfaceView2.setEnableHardwareScaler(true);
        binding.surfaceView2.setMirror(true);

        //add one more
    }

//    private void initializePeerConnectionFactory() {
//        System.out.println("initializePeerConnectionFactory");
////        PeerConnectionFactory.initializeAndroidGlobals(this, true, true, true);
////        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
////
////
////        factory = new PeerConnectionFactory(options);
////        factory.setVideoHwAccelerationOptions(rootEglBase.getEglBaseContext(), rootEglBase.getEglBaseContext());
//        PeerConnectionFactory.InitializationOptions initializationOptions =
//                PeerConnectionFactory.InitializationOptions.builder(this)
//                        .createInitializationOptions();
////        PeerConnectionFactory.initialize(PeerConnectionFactory.InitializationOptions.builder(this).createInitializationOptions());
//
//
//        PeerConnectionFactory.initialize(initializationOptions);
//        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
//
//        VideoEncoderFactory encoderFactory = new DefaultVideoEncoderFactory(rootEglBase.getEglBaseContext(), true, true);
//        VideoDecoderFactory decoderFactory = new DefaultVideoDecoderFactory(rootEglBase.getEglBaseContext());
//
//        factory = PeerConnectionFactory.builder()
//                .setOptions(options)
//                .setVideoDecoderFactory(decoderFactory)
//                .setVideoEncoderFactory(encoderFactory)
//                .createPeerConnectionFactory();
//    }
//private void createPeerConnectionFactory() {
//    PeerConnectionFactory.InitializationOptions initializationOptions =
//            PeerConnectionFactory.InitializationOptions.builder(this)
//                    .createInitializationOptions();
//    PeerConnectionFactory.initialize(initializationOptions);
//
//    //Create a new PeerConnectionFactory instance - using Hardware encoder and decoder.
//    PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
//    DefaultVideoEncoderFactory defaultVideoEncoderFactory = new DefaultVideoEncoderFactory(
//            rootEglBase.getEglBaseContext(),  /* enableIntelVp8Encoder */true,  /* enableH264HighProfile */true);
//    DefaultVideoDecoderFactory defaultVideoDecoderFactory = new DefaultVideoDecoderFactory(rootEglBase.getEglBaseContext());
////    peerConnectionFactory = new PeerConnectionFactory(options, defaultVideoEncoderFactory, defaultVideoDecoderFactory);
////    PeerConnectionFactory.InitializationOptions initializationOptions =
////            PeerConnectionFactory.InitializationOptions.builder(this)
////                    .createInitializationOptions();
//
////    PeerConnectionFactory.initialize(null);
////    PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
////
////    EglBase eglBase = EglBase.create();
////    VideoDecoderFactory decoderFactory = new DefaultVideoDecoderFactory(eglBase.getEglBaseContext());
////
////    factory = PeerConnectionFactory.builder()
////            .setOptions(options)
////            .setVideoDecoderFactory(decoderFactory)
////            .createPeerConnectionFactory();
//
////    PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(new ArrayList<>());
////    createPeerConnection(rtcConfig);
//}

    private void createVideoTrackFromCameraAndShowIt() {
//        audioConstraints = new MediaConstraints();
//        VideoCapturer videoCap = createVideoCapturer();
////        VideoSource videoSource = factory.createVideoSource(videoCap);
//        if (videoCapturer != null) {
//            SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", rootEglBase.getEglBaseContext());
//            videoSource = factory.createVideoSource(videoCapturer.isScreencast());
//            videoCapturer.initialize(surfaceTextureHelper, this, videoSource.getCapturerObserver());
//        }
//        VideoSource videoSource = factory.createVideoSource(videoCap.isScreencast());
//
//        videoCap.startCapture(VIDEO_RESOLUTION_WIDTH, VIDEO_RESOLUTION_HEIGHT, FPS);
//
//        videoTrackFromCamera = factory.createVideoTrack(VIDEO_TRACK_ID, videoSource);
//        videoTrackFromCamera.setEnabled(true);
//        localVideoTrack.addSink(binding.surfaceView);
//
////        videoTrackFromCamera.addRenderer(new VideoRenderer(binding.surfaceView));
//
//        //create an AudioSource instance
//        audioSource = factory.createAudioSource(audioConstraints);
//        localAudioTrack = factory.createAudioTrack("101", audioSource);

        VideoCapturer videoCapturer = createVideoCapturer();
//        VideoSource videoSource;
        //Create a VideoSource instance
        if (videoCapturer != null) {
             surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", rootEglBase.getEglBaseContext());
            videoSource = factory.createVideoSource(videoCapturer.isScreencast());
            videoCapturer.initialize(surfaceTextureHelper, this, videoSource.getCapturerObserver());
            System.out.println("my video camera");

        }

//        videoSource = factory.createVideoSource(videoCapturer.isScreencast());

        localVideoTrack = factory.createVideoTrack("100", videoSource);
        localVideoTrack.setEnabled(true);

        //Create MediaConstraints - Will be useful for specifying video and audio constraints.
        audioConstraints = new MediaConstraints();
        videoConstraints = new MediaConstraints();

        //create an AudioSource instance
        audioSource = factory.createAudioSource(audioConstraints);
        localAudioTrack = factory.createAudioTrack("101", audioSource);


        if (videoCapturer != null) {
            videoCapturer.startCapture(VIDEO_RESOLUTION_WIDTH, VIDEO_RESOLUTION_HEIGHT, FPS);
        }

//        binding.surfaceView.setVisibility(View.VISIBLE);
        // And finally, with our VideoRenderer ready, we
        // can add our renderer to the VideoTrack.
        localVideoTrack.addSink(binding.surfaceView);

    }

    private void initializePeerConnections() {
        peerConnection = createPeerConnection(factory);
    }

    private void startStreamingVideo() {
        MediaStream mediaStream = factory.createLocalMediaStream("ARDAMS");
//        mediaStream.addTrack(videoTrackFromCamera);
   mediaStream.addTrack(localVideoTrack);


        mediaStream.addTrack(localAudioTrack);
        peerConnection.addStream(mediaStream);
        JSONObject message = new JSONObject();
        try {
            message.put("type", "got user media");
            message.put("your_id", anthor_user_id);
            message.put("my_id", classSharedPreferences.getUser().getUserId());

//            if(!classSharedPreferences.getUser().getUserId().equals("171")){
//            sendMessage(message);
//            System.out.println("messageeeeeeeee"+message);}
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        sendMessage("got user media");
    }

    private PeerConnection createPeerConnection(PeerConnectionFactory factory) {
        ArrayList<PeerConnection.IceServer> iceServers = new ArrayList<>();
        String URL = "stun:stun.l.google.com:19302";
        iceServers.add(new PeerConnection.IceServer(URL));

        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(iceServers);
        MediaConstraints pcConstraints = new MediaConstraints();
        PeerConnection.Observer pcObserver = new PeerConnection.Observer() {
            @Override
            public void onSignalingChange(PeerConnection.SignalingState signalingState) {

            }

            @Override
            public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {

            }

            @Override
            public void onStandardizedIceConnectionChange(PeerConnection.IceConnectionState newState) {
                PeerConnection.Observer.super.onStandardizedIceConnectionChange(newState);
            }

            @Override
            public void onConnectionChange(PeerConnection.PeerConnectionState newState) {
                PeerConnection.Observer.super.onConnectionChange(newState);
            }

            @Override
            public void onIceConnectionReceivingChange(boolean b) {

            }

            @Override
            public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {

            }

            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                Log.d(TAG, "onIceCandidate: ");
                JSONObject message = new JSONObject();

                try {
                    message.put("type", "candidate");
                    message.put("label", iceCandidate.sdpMLineIndex);
                    message.put("id", iceCandidate.sdpMid);
                    message.put("candidate", iceCandidate.sdp);
                    message.put("your_id", anthor_user_id);
                    message.put("my_id", classSharedPreferences.getUser().getUserId());


                    Log.d(TAG, "onIceCandidate: sending candidate " + message);
                    sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }



            @Override
            public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {

            }

            @Override
            public void onSelectedCandidatePairChanged(CandidatePairChangeEvent event) {
                PeerConnection.Observer.super.onSelectedCandidatePairChanged(event);
            }

            @Override
            public void onAddStream(MediaStream mediaStream) {
                                Log.d(TAG, "onAddStream: " + mediaStream.videoTracks.size());
                VideoTrack remoteVideoTrack = mediaStream.videoTracks.get(0);
                AudioTrack remoteAudioTrack = mediaStream.audioTracks.get(0);
                remoteAudioTrack.setEnabled(true);
                remoteVideoTrack.setEnabled(true);
                remoteVideoTrack.addSink(binding.surfaceView2);


            }

            @Override
            public void onRemoveStream(MediaStream mediaStream) {

            }

            @Override
            public void onDataChannel(DataChannel dataChannel) {

            }

            @Override
            public void onRenegotiationNeeded() {

            }

            @Override
            public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {

            }

            @Override
            public void onTrack(RtpTransceiver transceiver) {
                PeerConnection.Observer.super.onTrack(transceiver);
            }

//        PeerConnection.Observer pcObserver = new PeerConnection.Observer() {
//            @Override
//            public void onSignalingChange(PeerConnection.SignalingState signalingState) {
//                Log.d(TAG, "onSignalingChange: ");
//            }
//
//            @Override
//            public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
//                Log.d(TAG, "onIceConnectionChange: ");
//            }
//
//            @Override
//            public void onIceConnectionReceivingChange(boolean b) {
//                Log.d(TAG, "onIceConnectionReceivingChange: ");
//            }
//
//            @Override
//            public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
//                Log.d(TAG, "onIceGatheringChange: ");
//            }
//
//            @Override
//            public void onIceCandidate(IceCandidate iceCandidate) {
//                Log.d(TAG, "onIceCandidate: ");
//                JSONObject message = new JSONObject();
//
//                try {
//                    message.put("type", "candidate");
//                    message.put("label", iceCandidate.sdpMLineIndex);
//                    message.put("id", iceCandidate.sdpMid);
//                    message.put("candidate", iceCandidate.sdp);
//                    message.put("your_id", anthor_user_id);
//                    message.put("my_id", classSharedPreferences.getUser().getUserId());
//
//
//                    Log.d(TAG, "onIceCandidate: sending candidate " + message);
//                    sendMessage(message);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
//                Log.d(TAG, "onIceCandidatesRemoved: ");
//            }
//
//            @Override
//            public void onAddStream(MediaStream mediaStream) {
//                Log.d(TAG, "onAddStream: " + mediaStream.videoTracks.size());
//                VideoTrack remoteVideoTrack = mediaStream.videoTracks.get(0);
//                AudioTrack remoteAudioTrack = mediaStream.audioTracks.get(0);
//                remoteAudioTrack.setEnabled(true);
//                remoteVideoTrack.setEnabled(true);
//                remoteVideoTrack.addRenderer(new VideoRenderer(binding.surfaceView2));
//
//            }
//
//
//            @Override
//            public void onRemoveStream(MediaStream mediaStream) {
//                Log.d(TAG, "onRemoveStream: ");
//            }
//
//            @Override
//            public void onDataChannel(DataChannel dataChannel) {
//                Log.d(TAG, "onDataChannel: ");
//            }
//
//            @Override
//            public void onRenegotiationNeeded() {
//                Log.d(TAG, "onRenegotiationNeeded: ");
//            }
//        };
        };
        return factory.createPeerConnection(rtcConfig, pcConstraints, pcObserver);
    }

    private VideoCapturer createVideoCapturer() {
        VideoCapturer videoCap;
        if (useCamera2()) {
            videoCap = createCameraCapturer(new Camera2Enumerator(this));
        } else {
            videoCap = createCameraCapturer(new Camera1Enumerator(true));
        }
        return videoCap;
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }

    private boolean useCamera2() {
        return Camera2Enumerator.isSupported(this);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void  closeMyCamera(){
        isVideo = !isVideo;
        videoTrackFromCamera.setEnabled(isVideo);


    }
    private boolean isSwitch = false;

    private void  switchCamera(){
        if (isSwitch) return;
        isSwitch = true;
        if (videoCapturer == null) return;

        CameraVideoCapturer cameraVideoCapturer = (CameraVideoCapturer) videoCapturer;
        try {

            cameraVideoCapturer.switchCamera(new CameraVideoCapturer.CameraSwitchHandler() {
                @Override
                public void onCameraSwitchDone(boolean isFrontCamera) {
                    isSwitch = false;
                }

                @Override
                public void onCameraSwitchError(String errorDescription) {
                    isSwitch = false;
                }
            });
        } catch (Exception e) {
            isSwitch = false;
        }

//    } else {
//        Log.d(TAG, "Will not switch camera, video caputurer is not a camera");
//    }



    }
    public boolean toggleSpeaker(boolean enable) {
        if (audioManager != null) {
//            isSpeakerOn = enable;
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            if (enable) {
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                        AudioManager.FX_KEY_CLICK);
                audioManager.setSpeakerphoneOn(true);
            } else {
                //5.0以上
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //设置mode
                    audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                } else {
                    //设置mode
                    audioManager.setMode(AudioManager.MODE_IN_CALL);
                }
                //设置音量，解决有些机型切换后没声音或者声音突然变大的问题
                audioManager.setStreamVolume(
                        AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL),
                        AudioManager.FX_KEY_CLICK
                );
                audioManager.setSpeakerphoneOn(false);
            }
            return true;
        }
        return false;

    }
    void changeFragment(){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.title_fragment, new RequestCallAudioAndVideoFragment()).commit();
    }
    private void createPeerConnectionFactory() {
        PeerConnectionFactory.InitializationOptions initializationOptions =
                PeerConnectionFactory.InitializationOptions.builder(this)
                        .createInitializationOptions();

        PeerConnectionFactory.initialize(initializationOptions);
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();

        VideoEncoderFactory encoderFactory = new DefaultVideoEncoderFactory(rootEglBase.getEglBaseContext(), true, true);
        VideoDecoderFactory decoderFactory = new DefaultVideoDecoderFactory(rootEglBase.getEglBaseContext());

         factory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setVideoDecoderFactory(decoderFactory)
                .setVideoEncoderFactory(encoderFactory)
                .createPeerConnectionFactory();

        List<PeerConnection.IceServer> iceServers = new ArrayList<>();
        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(iceServers);
//        createPeerConnection(rtcConfig);
    }
    private void callDisconnect() {
        if (factory != null) {
            factory.stopAecDump();
        }
        Log.d("ZCF", "Closing audio source.");
        if (audioSource != null) {
            audioSource.dispose();
            audioSource = null;
        }
        Log.d("ZCF", "Stopping capture.");
        if (videoCapturer != null) {
            try {
                videoCapturer.stopCapture();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            videoCapturer.dispose();
            videoCapturer = null;
        }
        Log.d("ZCF", "Closing video source.");
        if (videoSource != null) {
            videoSource.dispose();
            videoSource = null;
        }

        Log.d("ZCF", "Closing peer connection.");
        if (peerConnection != null) {
            peerConnection.dispose();
            peerConnection = null;
        }

        Log.d("ZCF", "Closing peer connection factory.");
        if (factory != null) {
            factory.dispose();
            factory = null;
        }

        rootEglBase.release();
        Log.d("ZCF", "Closing peer connection done.");
        PeerConnectionFactory.stopInternalTracingCapture();
        PeerConnectionFactory.shutdownInternalTracer();
    }

}

