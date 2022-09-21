package com.yawar.memo.call;

import static org.webrtc.SessionDescription.Type.ANSWER;
import static org.webrtc.SessionDescription.Type.OFFER;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.databinding.ActivityCallMainBinding;
import com.yawar.memo.modelView.ResponeCallViewModel;
import com.yawar.memo.notification.CancelCallFromCallOngoingNotification;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.R;
import com.yawar.memo.service.SocketIOService;
import com.yawar.memo.utils.BaseApp;

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
//import org.webrtc.VideoRenderer;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ResponeCallActivity extends AppCompatActivity {
    String username = "";

    String id ="0";
    String title = "";
    String onGoingTitle = "";
    private AudioManager audioManager;
    int time = 0;
    Timer callTimer = new Timer();
    String calltType;
    float dX, dY;




    public static final String ON_CALL_REQUEST = "CallMainActivity.ON_CALL_REQUEST";
    public static final String ON_STOP_CALLING_REQUEST = "RequestCallActivity.ON_CALL_REQUEST";
    public static final String ON_RECIVED_SETTINGS_CALL = "CallMainActivity.ON_RECIVED_SETTINGS_CALL";
    public static final String ON_CLOSE_CALL_FROM_NOTIFICATION_CALL_ACTIVITY = "ON_CLOSE_CALL_FROM_NOTIFICATION_CALL_ACTIVITY";
    public static final String ON_RECIVED_ASK_FOR_VIDEO = "on_recived_ask_for_video";
    public static final String ON_RECIVED_RESPONE_FOR_VIDEO = "on_recived_respone_for_video";










    Boolean isVideoForMe = false;
    Boolean isVideoForyou = false;
    VideoCapturer videoCapturer;
    String imageUrl;
    String callId;

    LinearLayout layoutCallProperties;
    ImageButton imgBtnStopCallLp;
    ImageButton imgBtnOpenCameraCallLp;
    ImageButton imgBtnOpenAudioCallLp;
    ImageButton imgBtnSwitchCamera;
    ImageButton imgBtnSwitchMic;
    ClassSharedPreferences classSharedPreferences;
//    CircleImageView imageCallUser;
    String callString = null;
    AlertDialog alertDialog,alertDialogForME;
    AlertDialog.Builder dialogForMe ;
    ResponeCallViewModel responeCallViewModel;
    private static final String TAG = "CompleteActivity";
    private static final int RC_CALL = 111;
    public static final String VIDEO_TRACK_ID = "ARDAMSv0";
    public static final int VIDEO_RESOLUTION_WIDTH = 480;
    public static final int VIDEO_RESOLUTION_HEIGHT = 360;
    public static final int FPS = 25;

    private boolean isInitiator = true;
    private boolean isChannelReady = true;
    private boolean isStarted = false;
    String anthor_user_id;


    MediaConstraints audioConstraints;
    MediaConstraints videoConstraints;
    MediaConstraints sdpConstraints;
    VideoSource videoSource;
    VideoTrack localVideoTrack;
    AudioSource audioSource;
    AudioTrack localAudioTrack;
    SurfaceTextureHelper surfaceTextureHelper;
    public static final String ON_RECIVE_MESSAGE = "ON_RECIVE_MESSAGE";


    private ActivityCallMainBinding binding;
    private PeerConnection peerConnection;
    private EglBase rootEglBase;
    private PeerConnectionFactory factory;
    private VideoTrack videoTrackFromCamera;





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

                        if (type.equals("got user media")) {
                            if(!anthor_id.equals(classSharedPreferences.getUser().getUserId())){
                                System.out.println("got user media");
                                maybeStart();
                            }

                        }



                        if (type.equals("offer")) {

                            System.out.println("type offer");
                            sdp = jsonObject.getString("sdp");


                            if (id.equals(classSharedPreferences.getUser().getUserId())) {

                                peerConnection.setRemoteDescription(new SimpleSdpObserver(), new SessionDescription(OFFER, sdp));
                                doAnswer(anthor_id);
                            }
                        }


                        else if (type.equals("answer") ) {
                            sdp = jsonObject.getString("sdp");
                            if (id.equals(classSharedPreferences.getUser().getUserId())) {

                                peerConnection.setRemoteDescription(new SimpleSdpObserver(), new SessionDescription(ANSWER, sdp));}}
                        else if (type.equals("candidate")) {
                            if (id.equals(classSharedPreferences.getUser().getUserId())) {

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



    private final BroadcastReceiver reciveStopCalling = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String stopCallString = intent.getExtras().getString("get stopCalling");
                    System.out.println("stopCallString"+stopCallString);
                    JSONObject message = null;
                    try {

                        message = new JSONObject(stopCallString);
                        String id = message.getString("snd_id");
                        if(id.equals(anthor_user_id)){
                            finish();

                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



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
                    System.out.println("reciveclosecallfromnotification");
                    closeCall();
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

                    String stopCallString = intent.getExtras().getString("get askVideo");
                    JSONObject message = null;
                    try {
                        message = new JSONObject(stopCallString);
                        isVideoForyou = message.getBoolean("video");

                        responeCallViewModel.isVideoForYou.setValue(isVideoForyou);


                        if(isVideoForyou) {
                                showSwitchToVideoWhenANthorUserRequestDialog(username + " " + getResources().getString(R.string.alert_switch_to_video_from_anthor_message));


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
    private final BroadcastReceiver reciveAcceptChangeToVideoCall = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    String stopCallString = intent.getExtras().getString("get responeAskVideo");
                    JSONObject message = null;
                    try {
                        message = new JSONObject(stopCallString);
                        isVideoForyou = message.getBoolean("video");
                        responeCallViewModel.isVideoForYou.setValue(isVideoForyou);

                        if (!isVideoForyou){
                            responeCallViewModel.isVideoForMe.setValue(false);

                        }
                        else {
                            responeCallViewModel.setIsSpeaker(isVideoForyou);

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
    private void closeCall() {
        Intent service = new Intent(this, SocketIOService.class);
        JSONObject data = new JSONObject();
        try {
            data.put("id", anthor_user_id);
            data.put("snd_id", classSharedPreferences.getUser().getUserId());
            data.put("call_id", responeCallViewModel.getCallId());
            data.put("call_duration", responeCallViewModel.getTimeString());

        }  catch (JSONException e) {
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
    private void sendMessage( Object object) {
        System.out.println("sendMessage"+object.toString());
        Log.d(TAG, "sendMessage: "+object.toString());
        Intent service = new Intent(this, SocketIOService.class);
//        JSONObject object = new JSONObject();

        service.putExtra(SocketIOService.EXTRA_SEND_MESSAGE_FOR_CALL_PARAMTES, object.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_SEND_MESSAGE_FOR_CALL);
        this.startService(service);
    }
    private void startCallTimeCounter() {

        //Set the schedule function and rate
        callTimer.scheduleAtFixedRate(new TimerTask() {

                                          public void run()
                                          {
                                              //Called each time when 1000 milliseconds (1 second) (the period parameter)
        runOnUiThread(new Runnable() {

        public void run()
        {
            responeCallViewModel.setTime(responeCallViewModel.getTime()+1);


        binding.callStatue.setText(responeCallViewModel.getTimeString());

        }

        });
        }

        },
                //Set how long before to start calling the TimerTask (in milliseconds)
        0,
                //Set the amount of time between each execution (in milliseconds)
        1000);

    }



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showWhenLockedAndTurnScreenOn();
        CallProperty.setStatusBarOrScreenStatus(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_call_main);
        start();
        startCallTimeCounter();

        LocalBroadcastManager.getInstance(this).registerReceiver(reciveStopCalling, new IntentFilter(ON_STOP_CALLING_REQUEST));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveSettingsCalling, new IntentFilter(ON_RECIVED_SETTINGS_CALL));
        Intent closeIntent = new Intent(CallNotificationActivity.ON_CLOSE_CALL_FROM_NOTIFICATION);

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(closeIntent);
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveclosecallfromnotification, new IntentFilter(ON_CLOSE_CALL_FROM_NOTIFICATION_CALL_ACTIVITY));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveAskForCall, new IntentFilter(ON_RECIVED_ASK_FOR_VIDEO));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveAcceptChangeToVideoCall, new IntentFilter(ON_RECIVED_RESPONE_FOR_VIDEO));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveMessageCall, new IntentFilter(ON_RECIVE_MESSAGE));


        responeCallViewModel = new ViewModelProvider(this).get(ResponeCallViewModel.class);


        classSharedPreferences = BaseApp.getInstance().getClassSharedPreferences();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        imgBtnStopCallLp = findViewById(R.id.close_call_layout);
        imgBtnOpenCameraCallLp = findViewById(R.id.image_video_call_layout);
        imgBtnOpenAudioCallLp = findViewById(R.id.image_audio_call_layout);
        imgBtnSwitchCamera = findViewById(R.id.image_switch_camera);
        imgBtnSwitchMic = findViewById(R.id.image_switch_mic);


//
        layoutCallProperties = findViewById(R.id.video_rl);
        layoutCallProperties.setVisibility(View.VISIBLE);
        binding.localVideoView.bringToFront();

        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id", "0");
        callString = bundle.getString("callRequest", "code");
        title = bundle.getString("title", "");


        dialogForMe = new AlertDialog.Builder(this);
        initalCallProperties();
        showInCallNotification();
        maybeStart();


        responeCallViewModel.isVideoForMe.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
                if (s) {
                    ;
                    imgBtnOpenCameraCallLp.setBackground(null);
                    imgBtnSwitchMic.setVisibility(View.GONE);
                    imgBtnSwitchCamera.setVisibility(View.VISIBLE);


                    binding.remoteVideoView.setVisibility(View.VISIBLE);
                    binding.localVideoView.setVisibility(View.VISIBLE);
                    binding.audioOnlyLayout.setVisibility(View.GONE);
                    layoutCallProperties.setVisibility(View.VISIBLE);
//                    binding.localVideoView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
//                    binding.remoteVideoView.setLayoutParams(new RelativeLayout.LayoutParams(300,500));
//                    binding.remoteVideoView.setPadding(2,2,2,2);

                    if (videoCapturer != null) {
                        videoCapturer.startCapture(VIDEO_RESOLUTION_WIDTH, VIDEO_RESOLUTION_HEIGHT, FPS);

                    }


                } else {
                    imgBtnOpenCameraCallLp.setBackground(getDrawable(R.drawable.bv_background_white));

                    try {
                        videoCapturer.stopCapture();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    if (!responeCallViewModel.isVideoForYou.getValue()) {
                        imgBtnSwitchMic.setVisibility(View.VISIBLE);
                        imgBtnSwitchCamera.setVisibility(View.GONE);

                        binding.remoteVideoView.setVisibility(View.GONE);
                        binding.localVideoView.setVisibility(View.GONE);

                        binding.audioOnlyLayout.setVisibility(View.VISIBLE);
                        binding.audioOnlyLayout.setBackground(getDrawable(R.drawable.background_call));

                    } else {
//                        binding.localVideoView.setLayoutParams(new RelativeLayout.LayoutParams(300,500));
//                        binding.localVideoView.setPadding(2,2,2,2);
//
//                        binding.remoteVideoView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));


                    }

                }


            }
        });
        responeCallViewModel.isVideoForYou.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
                if (s) {
                    binding.remoteVideoView.setVisibility(View.VISIBLE);
                    binding.localVideoView.setVisibility(View.VISIBLE);


                    layoutCallProperties.setVisibility(View.VISIBLE);
                    binding.audioOnlyLayout.setVisibility(View.GONE);
                    imgBtnSwitchMic.setVisibility(View.GONE);
                    imgBtnSwitchCamera.setVisibility(View.VISIBLE);
//                    binding.remoteVideoView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
//                    binding.localVideoView.setLayoutParams(new RelativeLayout.LayoutParams(300,500));

                } else {
                    if (!responeCallViewModel.isVideoForMe.getValue()) {
                        binding.remoteVideoView.setVisibility(View.GONE);
                        binding.localVideoView.setVisibility(View.GONE);

                        layoutCallProperties.setVisibility(View.VISIBLE);
                        binding.audioOnlyLayout.setVisibility(View.VISIBLE);
                        binding.audioOnlyLayout.setBackground(getDrawable(R.drawable.background_call));
                        imgBtnSwitchMic.setVisibility(View.VISIBLE);
                        imgBtnSwitchCamera.setVisibility(View.GONE);


                    } else {

//                        binding.localVideoView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
//
//
//                        binding.remoteVideoView.setLayoutParams(new RelativeLayout.LayoutParams(300,500));


                    }


                }

            }
        });


        responeCallViewModel.isAudio.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {

                if (s) {
                    imgBtnOpenAudioCallLp.setBackground(null);


                } else {
                    imgBtnOpenAudioCallLp.setBackground(getDrawable(R.drawable.bv_background_white));


                }
                localAudioTrack.setEnabled(s);


            }


        });
        responeCallViewModel.isSpeaker.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
                if (s) {
                    imgBtnSwitchMic.setBackground(getDrawable(R.drawable.bv_background_white));


                } else {
                    imgBtnSwitchMic.setBackground(null);


                }

                toggleSpeaker(s);
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

                if (!responeCallViewModel.isVideoForMe.getValue() && !responeCallViewModel.isVideoForYou.getValue()) {
                    showSwitchToVideoWhenIAskDialog(getResources().getString(R.string.alert_switch_to_video_message));
                    responeCallViewModel.isVideoForMe.setValue(!responeCallViewModel.isVideoForMe.getValue());
                    sendAskForVideoCall(responeCallViewModel.isVideoForMe.getValue());

                } else {
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

                switchCamera();

            }
        });
        imgBtnSwitchMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                responeCallViewModel.setIsSpeaker(!responeCallViewModel.getIsSpeaker().getValue());

            }
        });
        //////////////////////////////
        binding.localVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                        dX = view.getX() - motionEvent.getRawX();
                        dY = view.getY() - motionEvent.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:

                        view.animate()
                                .x(motionEvent.getRawX() + dX)
                                .y(motionEvent.getRawY() + dY)
                                .setDuration(0)
                                .start();
                        break;
                    default:
                        return false;
                }
                return true;
            }


        });
    }
    @Override
    protected void onDestroy() {
//        webView.loadUrl("about:blank");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveStopCalling);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveSettingsCalling);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel( AllConstants.onGoingCallChannelId);


        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveclosecallfromnotification);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveAskForCall);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveMessageCall);

        callDisconnect();
//        closeCall();
        callTimer.cancel();

        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
     closeCall();
      finish();

        super.onBackPressed();
    }
    public void closeOpenVideo() {
        responeCallViewModel.isVideoForMe.setValue(!responeCallViewModel.isVideoForMe.getValue());
                sendSettingsCall(responeCallViewModel.isVideoForMe.getValue(),true);
    }
    public void closeOpenAudio(){

        responeCallViewModel.isAudio.setValue(!responeCallViewModel.isAudio.getValue());


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    @AfterPermissionGranted(RC_CALL)
    private void start() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, perms)) {


            initializeSurfaceViews();

            initializePeerConnectionFactory();

            createVideoTrackFromCameraAndShowIt();

            initializePeerConnections();

            startStreamingVideo();
            connectToSignallingServer();

        } else {
            EasyPermissions.requestPermissions(this, "Need some permissions", RC_CALL, perms);
        }
    }

    private void connectToSignallingServer() {

        isInitiator = true;

        isChannelReady = true;

    }
    //MirtDPM4
    private void doAnswer(String other_id) {

        peerConnection.createAnswer(new SimpleSdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                peerConnection.setLocalDescription(new SimpleSdpObserver(), sessionDescription);

                JSONObject message = new JSONObject();
                try {
                    message.put("type", "answer");
                    message.put("sdp", sessionDescription.description);
                    message.put("my_id", classSharedPreferences.getUser().getUserId());
                    message.put("your_id", anthor_user_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                sendMessage(message);

            }
        }, new MediaConstraints());
    }

    private void maybeStart() {
        if (!isStarted && isChannelReady) {
            isStarted = true;}
        doCall();


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
                JSONObject jsonObject = new JSONObject();


                try {


                    message.put("type" ,"offer");
                    message.put("callType",calltType);

                    message.put("sdp", sessionDescription.description);
                    message.put("your_id", anthor_user_id);
                    message.put("my_id", classSharedPreferences.getUser().getUserId());

                    sendMessage(message);
//                }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, sdpMediaConstraints);
    }



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

    private void initializePeerConnectionFactory() {

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
    }

    private void createVideoTrackFromCameraAndShowIt() {

        videoCapturer = createVideoCapturer();

        audioConstraints = new MediaConstraints();
        if (videoCapturer != null) {
            surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", rootEglBase.getEglBaseContext());
            videoSource = factory.createVideoSource(videoCapturer.isScreencast());
            videoCapturer.initialize(surfaceTextureHelper, this, videoSource.getCapturerObserver());

        }
        localVideoTrack = factory.createVideoTrack("100", videoSource);
        localVideoTrack.setEnabled(true);


        videoCapturer.startCapture(VIDEO_RESOLUTION_WIDTH, VIDEO_RESOLUTION_HEIGHT, FPS);

        videoTrackFromCamera = factory.createVideoTrack(VIDEO_TRACK_ID, videoSource);
        videoTrackFromCamera.setEnabled(true);
        localVideoTrack.addSink(binding.surfaceView);


        //create an AudioSource instance
        audioSource = factory.createAudioSource(audioConstraints);
        localAudioTrack = factory.createAudioTrack("101", audioSource);

    }

    private void initializePeerConnections() {
        peerConnection = createPeerConnection(factory);
    }

    private void startStreamingVideo() {
        MediaStream mediaStream = factory.createLocalMediaStream("ARDAMS");
        mediaStream.addTrack(videoTrackFromCamera);
        mediaStream.addTrack(localAudioTrack);
        peerConnection.addStream(mediaStream);
    }

private PeerConnection createPeerConnection(PeerConnectionFactory factory) {
    ArrayList<PeerConnection.IceServer> iceServers = new ArrayList<>();
//    String URL = "stun:stun.l.google.com:19302";
//    iceServers.add(new PeerConnection.IceServer(URL));
//    iceServers.add(new PeerConnection.IceServer("turn:fr-turn1.xirsys.com:80?transport=udp", "XudckbgEBo-cL8svrlbBS05UmRDbnxLfwP1U8nKrzcppvoj06xoPf4ImOAhonpd8AAAAAGMoDB9mYWRpZGVib3c=", "5178a972-37e4-11ed-95d3-0242ac120004"));
        iceServers.add(new PeerConnection.IceServer("turn:137.184.155.225:3478", "memo", "memoBack_Fadi!2022y"));


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

    };
    return factory.createPeerConnection(rtcConfig, pcConstraints, pcObserver);
}

    private VideoCapturer createVideoCapturer() {


        if (useCamera2()) {
            videoCapturer = createCameraCapturer(new Camera2Enumerator(this));
        } else {
            videoCapturer = createCameraCapturer(new Camera1Enumerator(true));
        }
        return videoCapturer;
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
               VideoCapturer  videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

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





    }
    public boolean toggleSpeaker(boolean enable) {
        if (audioManager != null) {
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            if (enable) {
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                        AudioManager.FX_KEY_CLICK);
                audioManager.setSpeakerphoneOn(true);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                } else {
                    audioManager.setMode(AudioManager.MODE_IN_CALL);
                }
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





    void showSwitchToVideoWhenIAskDialog(String message){


        dialogForMe.setTitle(message);
        dialogForMe.setCancelable(false);
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
        dialog.setCancelable(false);
        dialog.setPositiveButton(R.string.switch_to_video,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        responeCallViewModel.isVideoForMe.setValue(!responeCallViewModel.isVideoForMe.getValue());
                        responeCallViewModel.setIsSpeaker(true);

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

        }
    }
    private void showInCallNotification() {
//        Intent intent
//                = new Intent(this, CallNotificationActivity.class);
//        intent.putExtra("callRequest",message );
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
                = PendingIntent.getActivity(ResponeCallActivity.this
                , 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT|PendingIntent.FLAG_IMMUTABLE);
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
                .setGroup(anthor_user_id)
                .setUsesChronometer(true)


                .setVibrate(new long[]{10000, 10000})
                .setTicker("Call_STATUS")
                .addAction(R.drawable.btx_custom,  getResources().getString(R.string.cancel), pendingIntentCancell)


                .setSmallIcon(R.drawable.ic_memo_logo)
                        .setContentTitle(username)
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

                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            notificationManager.createNotificationChannel(
                    notificationChannel);
            notificationManager.createNotificationChannelGroup(new NotificationChannelGroup(channel_id, "Memo"));

        }


        Notification note = builder.build();
//        note.flags |= Notification.FLAG_INSISTENT;
        notificationManager.notify( AllConstants.onGoingCallChannelId,note);
    }
    private void callDisconnect() {
        responeCallViewModel.setIsSpeaker(false);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        if (factory != null) {
            factory.stopAecDump();
        }
        if (audioSource != null) {
            audioSource.dispose();
            audioSource = null;
        }
        if (videoCapturer != null) {
            try {
                videoCapturer.stopCapture();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            videoCapturer.dispose();
            videoCapturer = null;
        }
        if (videoSource != null) {
            videoSource.dispose();
            videoSource = null;
        }

        if (peerConnection != null) {
            peerConnection.close();
            peerConnection = null;
        }

        if (factory != null) {
            factory.dispose();
            factory = null;
        }

        rootEglBase.release();
        PeerConnectionFactory.stopInternalTracingCapture();
        PeerConnectionFactory.shutdownInternalTracer();
                if (surfaceTextureHelper != null) {
            surfaceTextureHelper.dispose();
            surfaceTextureHelper = null;

        }
        if (binding.surfaceView != null) {
            binding.surfaceView.release();
//            binding.surfaceView = null;

        }
        if (binding.surfaceView2 != null) {
            binding.surfaceView2.release();
//            binding.surfaceView = null;

        }
    }

     void initalCallProperties() {
            JSONObject message = null;
            JSONObject userObject;
            JSONObject typeObject;

            try {
                message = new JSONObject(callString);
                callId = message.getString("call_id");
                responeCallViewModel.setCallId(callId);

                userObject = new JSONObject(message.getString("user"));
                typeObject = new JSONObject(message.getString("type"));
                isVideoForyou = typeObject.getBoolean("video");

                responeCallViewModel.isVideoForMe.setValue(isVideoForyou);
                responeCallViewModel.isVideoForYou.setValue(isVideoForyou);
                if(isVideoForyou){
                    onGoingTitle = getResources().getString(R.string.ongoing_video_call);
                    binding.audioOnlyLayout.setVisibility(View.VISIBLE);
                    binding.audioOnlyLayout.setBackground(null);
                    binding.remoteVideoView.setVisibility(View.VISIBLE);
                    binding.localVideoView.setVisibility(View.VISIBLE);

                    calltType = "video";

                }

                else {
                    onGoingTitle = getResources().getString(R.string.ongoing_audio_call);
                    binding.audioOnlyLayout.setVisibility(View.VISIBLE);
                    binding.audioOnlyLayout.setBackground(getDrawable(R.drawable.background_call));
                    binding.remoteVideoView.setVisibility(View.GONE);
                    binding.localVideoView.setVisibility(View.GONE);

                    calltType = "audio";


                }

                responeCallViewModel.isSpeaker.setValue(isVideoForyou);
                username = userObject.getString("name");
                imageUrl = userObject.getString("image_profile");
                if (imageUrl!=null) {
                    Glide.with(binding.imageUserCalling).load(AllConstants.imageUrl+imageUrl).apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th)).into(binding.imageUserCalling);
                }
                anthor_user_id = message.getString("snd_id");
                binding.userName.setText(username);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.cancel(-1);

        }

}