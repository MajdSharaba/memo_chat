package com.yawar.memo.ui.responeCallPage;

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
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.app.RemoteAction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Icon;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Rational;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.databinding.ActivityCallMainBinding;
import com.yawar.memo.notification.CancelCallFromCallOngoingNotification;
//import com.yawar.memo.service.FloatingViewService;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.R;
import com.yawar.memo.service.SocketIOService;
import com.yawar.memo.ui.requestCall.SimpleSdpObserver;
import com.yawar.memo.utils.BaseApp;
import com.yawar.memo.utils.CallProperty;
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
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
//import org.webrtc.VideoRenderer;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import dagger.hilt.android.AndroidEntryPoint;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

@AndroidEntryPoint
public class ResponeCallActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_OVERLAY_PERMISSION = 122;
    ///// set floating page
    private static final String ACTION_MUTE = "com.yawar.memo.action.mute";
    private static final String ACTION_CLOSE_CALL = "com.yawar.memo.action.closeCall";
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
    VideoCapturer videoCapturer;
    String callId;
    LinearLayout layoutCallProperties;
    ImageButton imgBtnStopCallLp;
    ImageButton imgBtnOpenCameraCallLp;
    ImageButton imgBtnOpenAudioCallLp;
    ImageButton imgBtnSwitchCamera;
    ImageButton imgBtnSwitchMic;
    ClassSharedPreferences classSharedPreferences;
    String callString = null;
    AlertDialog alertDialog,alertDialogForME;
    AlertDialog.Builder dialogForMe ;
    ResponeCallViewModel responeCallViewModel;
    private static final String TAG = "CompleteActivity";
    private static final int RC_CALL = 111;
    private static final int DO_CALL = 222;
    public static final String VIDEO_TRACK_ID = "ARDAMSv0";
    public static final int VIDEO_RESOLUTION_WIDTH = 480;
    public static final int VIDEO_RESOLUTION_HEIGHT = 360;
    public static final int FPS = 25;
    private boolean isInitiator = true;
    private boolean isChannelReady = true;
    private boolean isStarted = false;
    MediaConstraints audioConstraints;
    VideoSource videoSource;
    VideoTrack localVideoTrack;
    AudioSource audioSource;
    AudioTrack localAudioTrack;
    SurfaceTextureHelper surfaceTextureHelper;
    public static final String ON_RECIVE_MESSAGE = "ON_RECIVE_MESSAGE";
    private ActivityCallMainBinding binding;
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
                                responeCallViewModel.getPeerConnection().setRemoteDescription(new SimpleSdpObserver(), new SessionDescription(OFFER, sdp));
                                doAnswer(anthor_id);
                            }
                        }


                        else if (type.equals("answer") ) {
                            sdp = jsonObject.getString("sdp");
                            if (id.equals(classSharedPreferences.getUser().getUserId())) {

                                responeCallViewModel.getPeerConnection().setRemoteDescription(new SimpleSdpObserver(), new SessionDescription(ANSWER, sdp));}}
                        else if (type.equals("candidate")) {
                            if (id.equals(classSharedPreferences.getUser().getUserId())) {

                                IceCandidate candidate = new IceCandidate(jsonObject.getString("id"), jsonObject.getInt("label"), jsonObject.getString("candidate"));
                                responeCallViewModel.getPeerConnection().addIceCandidate(candidate);
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
                        String callId = message.getString("call_id");

                        if(id.equals(responeCallViewModel.getAnthor_user_id())&&callId.equals(responeCallViewModel.getCallId())){
                            finish();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



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
                    String callId = intent.getExtras().getString("call_id");

                    Log.i("CallNotificatio", callId+"mmmm"+responeCallViewModel.getCallId());
                    if(callId.equals(responeCallViewModel.getCallId())) {
                        closeCall();
                        finish();
                    }


                    ///////////


                }
            });
        }
    };
    private final BroadcastReceiver reciveSettingsCalling = new BroadcastReceiver() {
        Boolean isVideoForyou;
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
                        responeCallViewModel.setIsVideoForYou(isVideoForyou);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    private final BroadcastReceiver reciveAskForCall = new BroadcastReceiver() {
        Boolean isVideoForyou;
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
                        if(isVideoForyou) {
                         showSwitchToVideoWhenANthorUserRequestDialog(responeCallViewModel.getUsername() + " " + getResources().getString(R.string.alert_switch_to_video_from_anthor_message));
                        } else {
                          if(alertDialog!=null){
                         alertDialog.dismiss();
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
    private final BroadcastReceiver reciveAcceptChangeToVideoCall = new BroadcastReceiver() {
        Boolean isVideoForyou;
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
                        responeCallViewModel.setIsVideoForYou(isVideoForyou);

                        if (!isVideoForyou){
                            responeCallViewModel.setIsVideoForMe(false);

                        }
                        else {
                            responeCallViewModel.setIsVideoForMe(!responeCallViewModel.isVideoForMe().getValue());
                            responeCallViewModel.setIsSpeaker(isVideoForyou);
                            setScreenSizes();


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
            data.put("rcv_id",responeCallViewModel.getAnthor_user_id());
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
            data.put("your_id", responeCallViewModel.getAnthor_user_id());
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
            data.put("id", responeCallViewModel.getAnthor_user_id());
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
            data.put("your_id",responeCallViewModel.getAnthor_user_id());
            data.put("video", video);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        service.putExtra(SocketIOService.EXTRA_SEND_ASK_VIDEO_CALL_PARAMTERS, data.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_SEND_VIDEO_CALL_REQUEST);
        startService(service);

    }
    private void sendMessage( Object object) {
        System.out.println("sendMessage"+object.toString());
        Log.d(TAG, "sendMessage: "+object.toString());
        Intent service = new Intent(this, SocketIOService.class);

        service.putExtra(SocketIOService.EXTRA_SEND_MESSAGE_FOR_CALL_PARAMTES, object.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_SEND_MESSAGE_FOR_CALL);
        this.startService(service);
    }
    private void startCallTimeCounter() {
           System.out.println("startCallTimeCounter");
        //Set the schedule function and rate
        callTimer.scheduleAtFixedRate(new TimerTask() {

        public void run()
         {
        runOnUiThread(new Runnable() {

        public void run()
        {
            responeCallViewModel.setTime(responeCallViewModel.getTime()+1);


        binding.callStatue.setText(responeCallViewModel.getTimeString());
//        callStatusFloatingView.setText(responeCallViewModel.getTimeString());

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
        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id", "0");
        callString = bundle.getString("callRequest", "code");
        title = bundle.getString("title", "");
        responeCallViewModel = new ViewModelProvider(this).get(ResponeCallViewModel.class);
        start();
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveStopCalling, new IntentFilter(ON_STOP_CALLING_REQUEST));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveSettingsCalling, new IntentFilter(ON_RECIVED_SETTINGS_CALL));
        Intent closeIntent = new Intent(CallNotificationActivity.ON_CLOSE_CALL_FROM_NOTIFICATION);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(closeIntent);
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveCloseCallFromNotification, new IntentFilter(ON_CLOSE_CALL_FROM_NOTIFICATION_CALL_ACTIVITY));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveAskForCall, new IntentFilter(ON_RECIVED_ASK_FOR_VIDEO));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveAcceptChangeToVideoCall, new IntentFilter(ON_RECIVED_RESPONE_FOR_VIDEO));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveMessageCall, new IntentFilter(ON_RECIVE_MESSAGE));

        classSharedPreferences = BaseApp.getInstance().getClassSharedPreferences();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        imgBtnStopCallLp = findViewById(R.id.close_call_layout);
        imgBtnOpenCameraCallLp = findViewById(R.id.image_video_call_layout);
        imgBtnOpenAudioCallLp = findViewById(R.id.image_audio_call_layout);
        imgBtnSwitchCamera = findViewById(R.id.image_switch_camera);
        imgBtnSwitchMic = findViewById(R.id.image_switch_mic);

        layoutCallProperties = findViewById(R.id.video_rl);
        layoutCallProperties.setVisibility(View.VISIBLE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_MUTE);
        filter.addAction(ACTION_CLOSE_CALL);
        registerReceiver(mReceiver, filter);




        dialogForMe = new AlertDialog.Builder(this);



        responeCallViewModel.isVideoForMe().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
                if (s) {
                    if(responeCallViewModel.getConnected().getValue()) {
                        imgBtnOpenCameraCallLp.setBackground(null);
                        imgBtnSwitchMic.setVisibility(View.GONE);
                        imgBtnSwitchCamera.setVisibility(View.VISIBLE);
                        binding.callAudioButtons.setVisibility(View.GONE);
                        binding.remoteVideoView.setVisibility(View.VISIBLE);
                        binding.localVideoView.setVisibility(View.VISIBLE);
                        binding.audioOnlyLayout.setVisibility(View.GONE);
                        layoutCallProperties.setVisibility(View.VISIBLE);
                        if(!responeCallViewModel.getBackPressClicked().getValue()) {
                            binding.callBottomCheet.bottomSheetLayout.setVisibility(View.VISIBLE);
                        }

                        if (videoCapturer != null) {
                            videoCapturer.startCapture(VIDEO_RESOLUTION_WIDTH, VIDEO_RESOLUTION_HEIGHT, FPS);
                            binding.openCloseVideo.setBackground(getDrawable(R.drawable.btx_custom));
                        }
                    }

                } else {
                    imgBtnOpenCameraCallLp.setBackground(getDrawable(R.drawable.bv_background_white));
                    binding.openCloseVideo.setBackground(getDrawable(R.drawable.bv_background_white));

                    try {
                        if (videoCapturer != null) {
                            videoCapturer.stopCapture();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    if (!responeCallViewModel.isVideoForYou().getValue()) {
                        imgBtnSwitchMic.setVisibility(View.VISIBLE);
                        imgBtnSwitchCamera.setVisibility(View.GONE);
                        binding.remoteVideoView.setVisibility(View.GONE);
                        binding.localVideoView.setVisibility(View.GONE);
                        binding.callAudioButtons.setVisibility(View.VISIBLE);
                        layoutCallProperties.setVisibility(View.GONE);
                        binding.callBottomCheet.bottomSheetLayout.setVisibility(View.GONE);
                        binding.audioOnlyLayout.setVisibility(View.VISIBLE);
                        binding.audioOnlyLayout.setBackground(getDrawable(R.drawable.background_call));


                    } else {
                        binding.audioOnlyLayout.setVisibility(View.GONE);
                        if(!responeCallViewModel.getBackPressClicked().getValue()) {
                            binding.callBottomCheet.bottomSheetLayout.setVisibility(View.VISIBLE);
                        }
                    }

                }


            }
        });
        responeCallViewModel.isVideoForYou().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
                if (s) {
                    if(responeCallViewModel.getConnected().getValue()) {
                        binding.remoteVideoView.setVisibility(View.VISIBLE);
                        binding.localVideoView.setVisibility(View.VISIBLE);
                        binding.audioOnlyLayout.setVisibility(View.GONE);
                        if(!responeCallViewModel.getBackPressClicked().getValue()) {
                            binding.callBottomCheet.bottomSheetLayout.setVisibility(View.VISIBLE);
                        }

                        layoutCallProperties.setVisibility(View.VISIBLE);
                        binding.audioOnlyLayout.setVisibility(View.GONE);
                        imgBtnSwitchMic.setVisibility(View.GONE);
                        imgBtnSwitchCamera.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (!responeCallViewModel.isVideoForMe().getValue()) {
                        binding.remoteVideoView.setVisibility(View.GONE);
                        binding.localVideoView.setVisibility(View.GONE);

                        layoutCallProperties.setVisibility(View.VISIBLE);
                        binding.audioOnlyLayout.setVisibility(View.VISIBLE);
                        binding.audioOnlyLayout.setBackground(getDrawable(R.drawable.background_call));
                        imgBtnSwitchMic.setVisibility(View.VISIBLE);
                        imgBtnSwitchCamera.setVisibility(View.GONE);
                        binding.callAudioButtons.setVisibility(View.VISIBLE);
                        binding.callBottomCheet.bottomSheetLayout.setVisibility(View.GONE);

                    } else {
                        binding.audioOnlyLayout.setVisibility(View.GONE);
                        if(!responeCallViewModel.getBackPressClicked().getValue()) {

                            binding.callBottomCheet.bottomSheetLayout.setVisibility(View.VISIBLE);
                        }

                    }


                }

            }
        });


        responeCallViewModel.isAudio().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {

                if (s) {
                    imgBtnOpenAudioCallLp.setBackground(null);
                    binding.mute.setBackground(getDrawable(R.drawable.btx_custom));

                } else {
                    imgBtnOpenAudioCallLp.setBackground(getDrawable(R.drawable.bv_background_white));
                    binding.mute.setBackground(getDrawable(R.drawable.bv_background_white));

                }
                if(localAudioTrack!= null) {
                    localAudioTrack.setEnabled(s);
                }


            }


        });
        responeCallViewModel.isSpeaker().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
                if (s) {
                    imgBtnSwitchMic.setBackground(getDrawable(R.drawable.bv_background_white));
                    binding.speaker.setBackground(getDrawable(R.drawable.bv_background_white));

                } else {
                    imgBtnSwitchMic.setBackground(null);
                    binding.speaker.setBackground(getDrawable(R.drawable.btx_custom));
                }

                toggleSpeaker(s);
            }
        });
        responeCallViewModel.getConnected().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {

                if (s) {
                    startCallTimeCounter();
                    responeCallViewModel.setIsVideoForMe(responeCallViewModel.isVideoForYou().getValue());
                    responeCallViewModel.setIsVideoForYou(responeCallViewModel.isVideoForYou().getValue());
                    if(responeCallViewModel.isVideoForYou().getValue()) {
                        setScreenSizes();
                    }
                }
                else {
                    binding.localVideoView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }
            }
        });
        responeCallViewModel.getBackPressClicked().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    binding.callBottomCheet.bottomSheetLayout.setVisibility(View.GONE);

                }
                else if(responeCallViewModel.isVideoForYou().getValue()||responeCallViewModel.isVideoForMe().getValue()){
                    binding.callBottomCheet.bottomSheetLayout.setVisibility(View.VISIBLE);

                }
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

                if (!responeCallViewModel.isVideoForMe().getValue() && !responeCallViewModel.isVideoForYou().getValue()) {
                    showSwitchToVideoWhenIAskDialog(getResources().getString(R.string.alert_switch_to_video_message));
                    sendAskForVideoCall(true);

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

                responeCallViewModel.setIsSpeaker(!responeCallViewModel.isSpeaker().getValue());

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

        binding.mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: "+"linerMute");
                closeOpenAudio();

            }
        });

        binding.openCloseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: "+"linerVideo");
                if (!responeCallViewModel.isVideoForMe().getValue() && !responeCallViewModel.isVideoForYou().getValue()) {
                    binding.localVideoView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
                    showSwitchToVideoWhenIAskDialog(getResources().getString(R.string.alert_switch_to_video_message));
                    sendAskForVideoCall(true);

                } else {
                    closeOpenVideo();
                }







            }
        });

        binding.speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                responeCallViewModel.setIsSpeaker(!responeCallViewModel.isSpeaker().getValue());
            }
        });
        binding.imgButtonStopCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeCall();
                finish();
            }
        });

    }



    @Override
    protected void onDestroy() {

            LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveStopCalling);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveSettingsCalling);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.cancel(AllConstants.onGoingCallChannelId);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveCloseCallFromNotification);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveAskForCall);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveMessageCall);
            callDisconnect();
            callTimer.cancel();
            super.onDestroy();
//        }
    }

    @Override
    protected void onResume() {
        responeCallViewModel.setIsVideoForMe(responeCallViewModel.isVideoForMe().getValue());
        responeCallViewModel.setBackPressClicked(false);
        setScreenSizes();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        showFloatingWindow();
        responeCallViewModel.setBackPressClicked(true);

    }

    @Override
    protected void onPause() {
//        showFloatingWindow();
//        responeCallViewModel.setBackPressClicked(true);
        super.onPause();
    }

    public void closeOpenVideo() {
        responeCallViewModel.setIsVideoForMe(!responeCallViewModel.isVideoForMe().getValue());
        System.out.println("close open video");
        System.out.println(responeCallViewModel.isVideoForMe().getValue());
        sendSettingsCall(responeCallViewModel.isVideoForMe().getValue(),true);
    }
    public void closeOpenAudio(){
        responeCallViewModel.setAudio(!responeCallViewModel.isAudio().getValue());
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    @AfterPermissionGranted(RC_CALL)
    private void start() {
        closeAllNotification();
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, perms)) {



            initializeSurfaceViews();

            initializePeerConnectionFactory();

            createVideoTrackFromCameraAndShowIt();

            initializePeerConnections();

            startStreamingVideo();
            connectToSignallingServer();

                initalCallProperties();
                showInCallNotification();
                maybeStart();

;

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

        responeCallViewModel.getPeerConnection().createAnswer(new SimpleSdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                responeCallViewModel.getPeerConnection().setLocalDescription(new SimpleSdpObserver(), sessionDescription);

                JSONObject message = new JSONObject();
                try {
                    message.put("type", "answer");
                    message.put("sdp", sessionDescription.description);
                    message.put("my_id", classSharedPreferences.getUser().getUserId());
                    message.put("your_id",responeCallViewModel.getAnthor_user_id());
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
        responeCallViewModel.getPeerConnection().createOffer(new SimpleSdpObserver() {
                @Override
                public void onCreateSuccess(SessionDescription sessionDescription) {
                    Log.d(TAG, "onCreateSuccess: ");
                    responeCallViewModel.getPeerConnection().setLocalDescription(new SimpleSdpObserver(), sessionDescription);
                    JSONObject message = new JSONObject();
                    JSONObject jsonObject = new JSONObject();


                    try {


                        message.put("type", "offer");
                        message.put("callType", calltType);

                        message.put("sdp", sessionDescription.description);
                        message.put("your_id", responeCallViewModel.getAnthor_user_id());
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

        audioSource = factory.createAudioSource(audioConstraints);
        localAudioTrack = factory.createAudioTrack("101", audioSource);

    }

    private void initializePeerConnections() {
       PeerConnection peerConnection = createPeerConnection(factory);
        responeCallViewModel.setPeerConection(peerConnection);
    }

    private void startStreamingVideo() {
        MediaStream mediaStream = factory.createLocalMediaStream("ARDAMS");
        mediaStream.addTrack(videoTrackFromCamera);
        mediaStream.addTrack(localAudioTrack);
        responeCallViewModel.getPeerConnection().addStream(mediaStream);
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
            if(iceConnectionState.toString().equals("CONNECTED")){
                if(!responeCallViewModel.getConnected().getValue()) {
                    responeCallViewModel.setConnected(true);
                }
            }

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
            JSONObject message = new JSONObject();

            try {
                message.put("type", "candidate");
                message.put("label", iceCandidate.sdpMLineIndex);
                message.put("id", iceCandidate.sdpMid);
                message.put("candidate", iceCandidate.sdp);
                message.put("your_id",responeCallViewModel.getAnthor_user_id());
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
                        responeCallViewModel.setIsVideoForMe(!responeCallViewModel.isVideoForMe().getValue());
                        responeCallViewModel.setIsSpeaker(true);
                        responeCallViewModel.setIsVideoForYou(!responeCallViewModel.isVideoForYou().getValue());
                        setScreenSizes();
                        SendSwitchTOVideoCallRespone(responeCallViewModel.isVideoForMe().getValue());
                        dialog.dismiss();
                    }
                });
        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SendSwitchTOVideoCallRespone(responeCallViewModel.isVideoForMe().getValue());
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

        Intent intent
                = new Intent(this, ResponeCallActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent
                = PendingIntent.getActivity(ResponeCallActivity.this
                , 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        /////////intent for reject
        Intent intentCancel
                = new Intent(this, CancelCallFromCallOngoingNotification.class);

        intentCancel.putExtra("id", responeCallViewModel.getAnthor_user_id());
        intentCancel.putExtra("call_id", responeCallViewModel.getCallId());


        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntentCancell = PendingIntent.getBroadcast(this, 0,
                intentCancel, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        String channel_id = "notification_channel";


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
                .setGroup(responeCallViewModel.getAnthor_user_id())
                .setUsesChronometer(true)
                .setVibrate(new long[]{10000, 10000})
                .setTicker("Call_STATUS")
//                .addAction(R.drawable.btx_custom,  getResources().getString(R.string.cancel), pendingIntentCancell)


                .setSmallIcon(R.drawable.ic_memo_logo)
                        .setContentTitle(responeCallViewModel.getUsername())
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

        if (responeCallViewModel.getPeerConnection() != null) {
            responeCallViewModel.getPeerConnection().close();
            responeCallViewModel.setPeerConection(null);
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



        }
        if (binding.surfaceView2 != null) {
            binding.surfaceView2.release();



        }
    }

     void initalCallProperties() {

                if(responeCallViewModel.isVideoForYou().getValue()){
                    onGoingTitle = getResources().getString(R.string.ongoing_video_call);
                    binding.audioOnlyLayout.setBackground(null);
                    binding.callAudioButtons.setVisibility(View.GONE);
                    binding.remoteVideoView.setVisibility(View.VISIBLE);
                    binding.localVideoView.setVisibility(View.VISIBLE);
                    binding.localVideoView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
                    calltType = "video";

                }

                else {
                    onGoingTitle = getResources().getString(R.string.ongoing_audio_call);
                    binding.audioOnlyLayout.setBackground(getDrawable(R.drawable.background_call));
                    binding.callAudioButtons.setVisibility(View.VISIBLE);
                    binding.remoteVideoView.setVisibility(View.GONE);
                    binding.localVideoView.setVisibility(View.GONE);

                    calltType = "audio";


                }
                binding.callStatue.setText(R.string.key_exchange);

                responeCallViewModel.setIsSpeaker(responeCallViewModel.isVideoForYou().getValue());
                if (responeCallViewModel.getImageUrl()!=null) {
                    Glide.with(binding.imageUserCalling).load(AllConstants.imageUrl+responeCallViewModel.getImageUrl()).apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th)).into(binding.imageUserCalling);
                }
                binding.userName.setText(responeCallViewModel.getUsername());
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.cancel(-1);

        }

    private void setScreenSizes(){
        binding.remoteVideoView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        binding.localVideoView.setLayoutParams(new RelativeLayout.LayoutParams((int) (screenWidth / 3.5),(int)(screenHeight / 4.5)));

    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE_OVERLAY_PERMISSION) {
//            if (Settings.canDrawOverlays(this)) {
//                // Permission was granted, you can show the floating window
//                showFloatingWindow();
//            } else {
//                showFloatingWindow();
//            }
//        }
//    }
//   private void  checkFlowatingWindowPermission(){
//
//       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//           if (!Settings.canDrawOverlays(this)) {
//               Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                       Uri.parse("package:" + getPackageName()));
//               startActivityForResult(intent, REQUEST_CODE_OVERLAY_PERMISSION);
//
//           } else {
//               showFloatingWindow();
//           }
//       } else {
//           // Permission is granted by default on older versions of Android
//           showFloatingWindow();
//       }
//
//   }

    private void showFloatingWindow() {
        binding.callBottomCheet.bottomSheetLayout.setVisibility(View.GONE);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Rational aspectRatio = new Rational(size.x, size.y);
        binding.localVideoView.setLayoutParams(new RelativeLayout.LayoutParams((int) (size.x / 7),(int)(size.y / 8)));


        // Set the actions that can be performed in PiP mode
        ArrayList<RemoteAction> actions = new ArrayList<>();

        actions.add(new RemoteAction(
                Icon.createWithResource(this, R.drawable.ic_baseline_call_end_24),
                "Pause", "Pause video", PendingIntent.getBroadcast(this, 0,
                new Intent(ACTION_CLOSE_CALL), PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_MUTABLE)));
//        actions.add(new RemoteAction(
//                Icon.createWithResource(this, R.drawable.),
//                "Play", "Play video", PendingIntent.getBroadcast(this, 0,
//                new Intent(ACTION_PLAY), 0)));
//        actions.add(new RemoteAction(
//                Icon.createWithResource(this, R.drawable.ic_pause),
//                "Pause", "Pause video", PendingIntent.getBroadcast(this, 0,
//                new Intent(ACTION_PAUSE), 0)));

        // Create a Picture-in-Picture params builder
        PictureInPictureParams.Builder pipBuilder =
                new PictureInPictureParams.Builder();

        // Set the aspect ratio and actions for PiP mode
        pipBuilder.setAspectRatio(aspectRatio).setActions(actions);

        // Enter PiP mode
        enterPictureInPictureMode(pipBuilder.build());

        }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_MUTE)) {

                closeOpenAudio();

            } else if (intent.getAction().equals(ACTION_CLOSE_CALL)) {
                closeCall();
                finish();
            }
        }
    };
    @Override
    public void onUserLeaveHint () {
        responeCallViewModel.setBackPressClicked(true);
        if (responeCallViewModel.getBackPressClicked().getValue()) {
            showFloatingWindow();
        }
    }
    public void closeAllNotification () {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String packageName = getApplicationContext().getPackageName();
        notificationManager.cancelAll();
    }

    }

