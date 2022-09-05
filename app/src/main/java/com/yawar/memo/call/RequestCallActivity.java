package com.yawar.memo.call;
import static org.webrtc.SessionDescription.Type.ANSWER;
import static org.webrtc.SessionDescription.Type.OFFER;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
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
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.databinding.ActivityRequestCallBinding;
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
import java.util.Timer;
import java.util.TimerTask;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class RequestCallActivity extends AppCompatActivity {
    private static final String TAG = "CompleteActivity";
    private static final int RC_CALL = 111;
    public static final String VIDEO_TRACK_ID = "ARDAMSv0";
//    public static final int VIDEO_RESOLUTION_WIDTH = 1280;
    public static final int VIDEO_RESOLUTION_WIDTH = 480;
    float dX, dY;



    //    public static final int VIDEO_RESOLUTION_HEIGHT = 720;
    public static final int VIDEO_RESOLUTION_HEIGHT = 360;


    public static final int FPS = 25;
    boolean isVideo = false;
    private AudioManager audioManager;
    private boolean isSpeakerOn = false;
    ImageButton imgBtnSwitchMic;
    LinearLayout ly_image_switch_mic;
    String imageUrl;
    String typeCall;
    int time = 0;
    Timer callTimer = new Timer();





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
    public static final String ON_CLOSE_CALL_FROM_NOTIFICATION_CALL_ACTIVITY = "ON_CLOSE_CALL_FROM_NOTIFICATION_CALL_ACTIVITY";

    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
    CountDownTimer countDownTimer;
    CountDownTimer countEndCallDownTimer;

    AlertDialog.Builder dialogForMe;


    UserModel userModel;
    String fcm_token;
    Boolean isAudio = true;
    LinearLayout layoutCallProperties;

    ImageButton imgBtnStopCallLp;
    ImageButton imgBtnOpenCameraCallLp;
    ImageButton imgBtnOpenAudioCallLp;
    ImageButton imgBtnSwitchCamera;
    final Handler handler = new Handler();
    boolean isMinimize = false;




    TextView callStatusTV;

    public static final String FETCH_PEER_ID = "RequestCallActivity.FETCH_PEER_ID";
    public static final String ON_RECIVED_SETTINGS_CALL = "RequestCallActivity.ON_RECIVED_SETTINGS_CALL";




    String userName;
    String my_id;
    private ImageView minimizeBtn;





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
                         System.out.println("requestCallViewModel.setRining(rining)");
                         requestCallViewModel.setRining("rining");


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
//                        requestCallViewModel.isVideoForYou.setValue(isVideoForYou);
                        requestCallViewModel.setIsVideoForYou(isVideoForYou);

                        if (!isVideoForYou){
//
                            requestCallViewModel.setIsVideoForMe(false);

                        }
                        else {
                            requestCallViewModel.setIsSpeaker(isVideoForYou);

                        }
                     if(alertDialogForME!=null){

                        alertDialogForME.dismiss();
                     }





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
//                        requestCallViewModel.isVideoForYou.setValue(isVideoForYou);
                        requestCallViewModel.setIsVideoForYou(isVideoForYou);

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

//                         requestCallViewModel.isVideoForYou.setValue(isVideoForYou);
                        requestCallViewModel.setIsVideoForYou(isVideoForYou);



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
//                    requestCallViewModel.rining.setValue(false);
                    requestCallViewModel.setRining("answare");

                    String call_peer_id = "no connect";
                    JSONObject message = null;
                    ///////////
                    try {

                        message = new JSONObject(callString);
                        System.out.println("this is message"+message.getString("peerId"));
                        if(message.getString("peerId")!=null){
                            call_peer_id = message.getString("peerId");
                            requestCallViewModel.setPeerId(call_peer_id);

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
                            finishCall();

                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



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



                    String id = "";
                    String type = "";
                    String sdp = "";

                    try {
                        JSONObject jsonObject = new JSONObject(unBlockString);
                        id = jsonObject.getString("your_id");
                        type = jsonObject.getString("type");
                        String anthor_id = jsonObject.getString("my_id");



                        if (type.equals("offer")) {

                            System.out.println("type offer");
                            sdp = jsonObject.getString("sdp");





                            if (id.equals(classSharedPreferences.getUser().getUserId())) {
                                System.out.println("iduser" + sdp);
                                requestCallViewModel.setRining("answare");
                                System.out.println("requestCallViewModel.setRining(answare)");

                                peerConnection.setRemoteDescription(new SimpleSdpObserver(), new SessionDescription(OFFER, sdp));
                                requestCallViewModel.setPeerId("opwn");




                                doAnswer();
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

///////////////////////////
    private void sendMessage( Object object) {
        Intent service = new Intent(this, SocketIOService.class);

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

        ////

        try {
            type.put("video", requestCallViewModel.getIsVideoForMe().getValue());

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
//        binding.callStatue.setText(R.string.calling);

        int i=0;
        countDownTimer = new CountDownTimer(30000, 1000) { //40000 milli seconds is total time, 1000 milli seconds is time interval

            public void onTick(long millisUntilFinished) {
                System.out.println(i+1);
            }
            public void onFinish() {
                requestCallViewModel.setEndCall(true);
                SendMissingCall();
                finishCall ();

            }
        }.start();

    }
    private void startEndCallCounter() {
        System.out.println("startEndCallCounter");
        callTimer.cancel();
        binding.callStatue.setText(R.string.call_ended);




        int j=0;
        countEndCallDownTimer = new CountDownTimer(4000, 1000) { //40000 milli seconds is total time, 1000 milli seconds is time interval

            public void onTick(long millisUntilFinished) {

            }
            public void onFinish() {
                System.out.println("EndEndCallCounter");

//                if(requestCallViewModel.getRining().getValue()){
//
//                    SendMissingCall();
//                    finishCall ();
//                }
//                else
                if (!requestCallViewModel.getPeerId().getValue().equals("no connect")&&!requestCallViewModel.getPeerId().getValue().equals("null")) {

                    closeCall();
                    finishCall();
                }
                else if (requestCallViewModel.getPeerId().getValue().equals("no connect")){

                        finishCall ();
                   SendMissingCall();

                    }
            }
        }.start();

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

                                              time += 1;
                                              int seconds = time % 60;
                                              int minutes = time / 60;
                                              int hour = minutes/60;
                                              String stringTime = String.format("%02d:%02d:%02d",hour, minutes, seconds);
                                              binding.callStatue.setText(stringTime);

                                          }

                                      });
                                  }

                              },
                //Set how long before to start calling the TimerTask (in milliseconds)
                0,
                //Set the amount of time between each execution (in milliseconds)
                1000);

    }

    private void closeCall() {
        Intent service = new Intent(this, SocketIOService.class);
        JSONObject data = new JSONObject();
        JSONObject type = new JSONObject();
        JSONObject userObject = new JSONObject();
        try {
            data.put("id", anthor_user_id);
            data.put("snd_id", my_id);
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
        System.out.println("SendMissingCall");
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


    private int LAYOUT_TYPE;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        CallProperty.setStatusBarOrScreenStatus(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_request_call);


        LocalBroadcastManager.getInstance(this).registerReceiver(recivePeerId, new IntentFilter(FETCH_PEER_ID));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveRining, new IntentFilter(ON_RINING_REQUEST));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveSettingsCalling, new IntentFilter(ON_RECIVED_SETTINGS_CALL));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveStopCalling, new IntentFilter(ON_STOP_CALLING_REQUEST));
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
        imageUrl = bundle.getString("image_profile", " mm");
        initalCallProperties();
        layoutCallProperties = findViewById(R.id.video_rl);

        imgBtnStopCallLp = findViewById(R.id.close_call_layout);
        imgBtnOpenCameraCallLp = findViewById(R.id.image_video_call_layout);
        imgBtnOpenAudioCallLp = findViewById(R.id.image_audio_call_layout);
        imgBtnSwitchCamera = findViewById(R.id.image_switch_camera);
        imgBtnSwitchMic = findViewById(R.id.image_switch_mic);
        classSharedPreferences = new ClassSharedPreferences(this);
        my_id = classSharedPreferences.getUser().getUserId();
        userModel = classSharedPreferences.getUser();
        dialogForMe = new AlertDialog.Builder(this);
        start();
        startCall();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);




        requestCallViewModel.getIsVideoForMe().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
                if (s) {
                    if (!requestCallViewModel.getPeerId().getValue().equals("no connect")) {

                        binding.remoteVideoView.setVisibility(View.VISIBLE);
                        binding.localVideoView.setVisibility(View.VISIBLE);
                        binding.audioOnlyLayout.setVisibility(View.GONE);
                        layoutCallProperties.setVisibility(View.VISIBLE);
                        imgBtnSwitchMic.setVisibility(View.GONE);
                        imgBtnOpenCameraCallLp.setBackground(null);
                        imgBtnSwitchCamera.setVisibility(View.VISIBLE);
//                        binding.remoteVideoView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
//                        binding.localVideoView.setLayoutParams(new RelativeLayout.LayoutParams(500,500));


                        if(videoCapturer!=null) {
                            videoCapturer.startCapture(VIDEO_RESOLUTION_WIDTH, VIDEO_RESOLUTION_HEIGHT, FPS);

                        }

                        imgBtnOpenCameraCallLp.setImageResource(R.drawable.ic_baseline_videocam_off_24);
                    }
                } else {
                    imgBtnOpenCameraCallLp.setBackground(getDrawable(R.drawable.bv_background_white));

                    try {
                        videoCapturer.stopCapture();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(!requestCallViewModel.getIsVideoForYou().getValue()) {
                        imgBtnSwitchMic.setVisibility(View.VISIBLE);
                        imgBtnSwitchCamera.setVisibility(View.GONE);


                        binding.remoteVideoView.setVisibility(View.GONE);
                        binding.localVideoView.setVisibility(View.GONE);
                        binding.audioOnlyLayout.setVisibility(View.VISIBLE);
                        binding.audioOnlyLayout.setBackground(getDrawable(R.drawable.background_call));

                    }
                    else {
//                        binding.remoteVideoView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
//                        binding.localVideoView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));


                    }

                }
                localVideoTrack.setEnabled(s);



            }
        });
        requestCallViewModel.getIsVideoForYou().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
                if (s) {
                    if (!requestCallViewModel.getPeerId().getValue().equals("no connect")) {

                        binding.remoteVideoView.setVisibility(View.VISIBLE);
                        binding.localVideoView.setVisibility(View.VISIBLE);
                        binding.audioOnlyLayout.setVisibility(View.GONE);
                        imgBtnSwitchMic.setVisibility(View.GONE);
                        imgBtnSwitchCamera.setVisibility(View.VISIBLE);
//                        binding.remoteVideoView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
//                        binding.localVideoView.setLayoutParams(new RelativeLayout.LayoutParams(500,500));
                    }
                } else {

                    if (!requestCallViewModel.getIsVideoForMe().getValue()) {

                        binding.remoteVideoView.setVisibility(View.GONE);
                        binding.localVideoView.setVisibility(View.GONE);
                        binding.audioOnlyLayout.setVisibility(View.VISIBLE);
                        binding.audioOnlyLayout.setBackground(getDrawable(R.drawable.background_call));
                        imgBtnSwitchMic.setVisibility(View.VISIBLE);
                        imgBtnSwitchCamera.setVisibility(View.GONE);

                    }
                    else{
//                        binding.localVideoView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
//                        binding.remoteVideoView.setLayoutParams(new RelativeLayout.LayoutParams(500,500));
                    }


                }

            }
        });
        requestCallViewModel.getRining().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("rining")) {
                    if (!requestCallViewModel.getEndCall().getValue()) {
                        mMediaPlayer.release();
                        mMediaPlayer = MediaPlayer.create(RequestCallActivity.this, R.raw.ring);
                        binding.callStatue.setText(R.string.rining);
                        mMediaPlayer.setLooping(true);

                        mMediaPlayer.start();
                        countDownTimer.cancel();
                        countDownTimer.start();
                    }


                }

            }
        });
        requestCallViewModel.getEndCall().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
                if (s) {
                    startEndCallCounter();


                }
            }
        });

        requestCallViewModel.getAudio().observe(this, new Observer<Boolean>() {
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
        requestCallViewModel.getIsSpeaker().observe(this, new Observer<Boolean>() {
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

        requestCallViewModel.getPeerId().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("null")) {

                    finishCall();

                } else if (s.equals("no connect")) {

                } else {

                    if (!requestCallViewModel.getEndCall().getValue()) {

                        requestCallViewModel.setIsVideoForMe(requestCallViewModel.getIsVideoForMe().getValue());
                        requestCallViewModel.setIsSpeaker(isVideoForMe);
                        binding.callStatue.setText(R.string.ongoing_call);
                        binding.remoteVideoView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
                        final float scale = getResources().getDisplayMetrics().density;
                        int pixelsWidth = (int) (120 * scale + 0.5f);
                        int pixelsHeight= (int) (170 * scale + 0.5f);


//                        binding.localVideoView.setLayoutParams(new RelativeLayout.LayoutParams(300,300));
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                pixelsWidth,
                                pixelsHeight
                        );
                        params.setMargins(20, 50, 20, 20);
                        binding.localVideoView.setLayoutParams(params);







                        ///for stop 15 sec counter
                        countDownTimer.cancel();
                        ////for close rining
                        mMediaPlayer.release();
                        ///for shw call notification
                        showInCallNotification();



                    }
                }
            }
        });



        ////// for call layout_call_properties
        imgBtnStopCallLp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startEndCallCounter();

            }
        });
        /////
        imgBtnOpenCameraCallLp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (!requestCallViewModel.getPeerId().getValue().equals("no connect")) {
                if(!requestCallViewModel.getIsVideoForYou().getValue() && !requestCallViewModel.getIsVideoForMe().getValue()){
                    requestCallViewModel.setIsVideoForMe(!requestCallViewModel.getIsVideoForMe().getValue());

                    sendAskForVideoCall(requestCallViewModel.getIsVideoForMe().getValue());
                    showSwitchToVideoDialog(getResources().getString(R.string.requesting_to_switch_to_video_call));


                }
                else{
                    closeOpenVideo();
                }


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
                requestCallViewModel.setIsSpeaker(!requestCallViewModel.isSpeaker.getValue());
            }
        });
        binding.localVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!requestCallViewModel.getPeerId().getValue().equals("no connect")) {
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
                }
                    return true;
                }


        });


//        binding.localVideoView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (binding.localVideoView.getWidth() != ViewGroup.LayoutParams.MATCH_PARENT ){
////                    RelativeLayout.LayoutParams paramsLocal = new RelativeLayout.LayoutParams(
////                            ViewGroup.LayoutParams.MATCH_PARENT,
////                            ViewGroup.LayoutParams.MATCH_PARENT
////                    );
////                    paramsLocal.addRule(RelativeLayout.BELOW , binding.remoteVideoView.getId());
////
////                    binding.localVideoView.setLayoutParams(paramsLocal);
//                    RelativeLayout.LayoutParams paramsLocal = new RelativeLayout.LayoutParams(
//                            100,
//                            100
//                    );
////                    paramsLocal.addRule(RelativeLayout.BELOW , binding.remoteVideoView.getId());
//
//
//                    binding.localVideoView.setLayoutParams(paramsLocal);
//                    final float scale = getResources().getDisplayMetrics().density;
//                    int pixelsWidth = (int) (120 * scale + 0.5f);
//                    int pixelsHeight= (int) (170 * scale + 0.5f);
//
//
//                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//                            pixelsWidth,
//                            pixelsHeight
//                    );
//
//                    params.setMargins((int) (7 * scale + 0.5f), (int) (15 * scale + 0.5f), (int) (7 * scale + 0.5f), 0);
////                    params.addRule(RelativeLayout.ABOVE , binding.localVideoView.getId());
////                    binding.remoteVideoView.setElevation(10);
//                    binding.remoteVideoView.setLayoutParams(params);
//
////                    RelativeLayout.LayoutParams paramsLocal = new RelativeLayout.LayoutParams(
////                            400,
////                            400
////                    );
//////                    paramsLocal.addRule(RelativeLayout.BELOW , binding.remoteVideoView.getId());
////
////
////                    binding.localVideoView.setLayoutParams(paramsLocal);
//                }
//            }
//        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //If API Level is more than 26, we need TYPE_APPLICATION_OVERLAY
            LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            //If API Level is lesser than 26, then we can use TYPE_SYSTEM_ERROR,
            //TYPE_SYSTEM_OVERLAY, TYPE_PHONE, TYPE_PRIORITY_PHONE. But these are all
            //deprecated in API 26 and later. Here TYPE_TOAST works best.
            LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_TOAST;
        }
        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        WindowManager.LayoutParams floatWindowLayoutParam = new WindowManager.LayoutParams(
                (int) (width * (0.55f)),
                (int) (height * (0.58f)),
                LAYOUT_TYPE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        //The Gravity of the Floating Window is set. The Window will appear in the center of the screen
        floatWindowLayoutParam.gravity = Gravity.CENTER;
        //X and Y value of the window is set
        floatWindowLayoutParam.x = 0;
        floatWindowLayoutParam.y = 0;

        //The ViewGroup that inflates the floating_layout.xml is
        //added to the WindowManager with all the parameters
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        //inflate a new view hierarchy from the floating_layout xml
         RelativeLayout floatView = findViewById(R.id.parent);
        floatView.setOnTouchListener(new View.OnTouchListener() {

            final WindowManager.LayoutParams floatWindowLayoutUpdateParam = floatWindowLayoutParam;
            double x;
            double y;
            double px;
            double py;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    //When the window will be touched, the x and y position of that position will be retrieved
                    case MotionEvent.ACTION_DOWN:
                        x = floatWindowLayoutUpdateParam.x;
                        y = floatWindowLayoutUpdateParam.y;
                        //returns the original raw X coordinate of this event
                        px = event.getRawX();
                        //returns the original raw Y coordinate of this event
                        py = event.getRawY();
                        break;
                    //When the window will be dragged around, it will update the x, y of the Window Layout Parameter
                    case MotionEvent.ACTION_MOVE:
                        floatWindowLayoutUpdateParam.x = (int) ((x + event.getRawX()) - px);
                        floatWindowLayoutUpdateParam.y = (int) ((y + event.getRawY()) - py);

                        //updated parameter is applied to the WindowManager

                        v.setLayoutParams(floatWindowLayoutUpdateParam);
                        getWindowManager().updateViewLayout(v, floatWindowLayoutUpdateParam);
                        break;
                }

                return false;
            }
        });

        //The Main Button that helps to minimize the app
        minimizeBtn = findViewById(R.id.iv_minimize);
        minimizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //First it confirms whether the 'Display over other apps' permission in given
                if (checkOverlayDisplayPermission()) {
                    setWindowParams();
                } else {
                    requestOverlayDisplayPermission();
                }

            }
        });
    }

    void initalCallProperties(){
        if (isVideoForMe) {

            onGoingTitle = getResources().getString(R.string.ongoing_video_call);
            binding.audioOnlyLayout.setVisibility(View.VISIBLE);
            binding.audioOnlyLayout.setBackground(null);
            binding.remoteVideoView.setVisibility(View.VISIBLE);
            binding.localVideoView.setVisibility(View.VISIBLE);
            binding.localVideoView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
            typeCall = "video";
        } else {
            onGoingTitle = getResources().getString(R.string.ongoing_audio_call);
            binding.audioOnlyLayout.setVisibility(View.VISIBLE);
            binding.audioOnlyLayout.setBackground(getDrawable(R.drawable.background_call));
            binding.remoteVideoView.setVisibility(View.GONE);
            binding.localVideoView.setVisibility(View.GONE);
            typeCall = "audio";


        }
        binding.userName.setText(userName);
        if (!imageUrl.isEmpty()) {
            Glide.with(binding.imageUserCalling).load(AllConstants.imageUrl+imageUrl).apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th)).into(binding.imageUserCalling);
        }
        requestCallViewModel.setIsSpeaker(isVideoForMe);

        requestCallViewModel.setIsVideoForYou(isVideoForMe);
        requestCallViewModel.setIsVideoForMe(isVideoForMe);

        layoutCallProperties = findViewById(R.id.video_rl);

        imgBtnStopCallLp = findViewById(R.id.close_call_layout);
        imgBtnOpenCameraCallLp = findViewById(R.id.image_video_call_layout);
        imgBtnOpenAudioCallLp = findViewById(R.id.image_audio_call_layout);
        imgBtnSwitchCamera = findViewById(R.id.image_switch_camera);
        imgBtnSwitchMic = findViewById(R.id.image_switch_mic);
    }
    void starCallVoice(){
        mMediaPlayer = MediaPlayer.create(this, R.raw.outputcall);
        requestCallViewModel.isSpeaker.setValue(false);


        mMediaPlayer.setLooping(true);

        mMediaPlayer.start();
    }


@AfterPermissionGranted(RC_CALL)
private void start() {
    String[] perms = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
    if (EasyPermissions.hasPermissions(this, perms)) {


        initializeSurfaceViews();

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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(recivePeerId);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveRining);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveSettingsCalling);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveStopCalling);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveAcceptChangeToVideoCall);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveAskForCall);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveCloseCallFromNotification);





        mMediaPlayer.release();

        countDownTimer.cancel();

        requestCallViewModel.setIsSpeaker(false);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(AllConstants.onGoingCallChannelId);
        if (!requestCallViewModel.getPeerId().getValue().equals("no connect")&&!requestCallViewModel.getPeerId().getValue().equals("null")) {
              System.out.println("send close call");
            closeCall();
        }
        callDisconnect();
        callTimer.cancel();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveMessageCall);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();

        super.onBackPressed();
    }
    public void closeOpenVideo() {
        requestCallViewModel.setIsVideoForMe(!requestCallViewModel.getIsVideoForMe().getValue());
        sendSettingsCall(requestCallViewModel.getIsVideoForMe().getValue(),true);


    }
   public void closeOpenAudio(){
        requestCallViewModel.setAudio(!requestCallViewModel.getAudio().getValue());


    }



    private void finishCall() {
        countDownTimer.cancel();
        callTimer.cancel();

        finish();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                finish();
//            }
//        }, 100);
    }

    void showSwitchToVideoDialog(String message){

        dialogForMe.setTitle(message);
        dialogForMe.setCancelable(false);

        dialogForMe.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestCallViewModel.setIsVideoForMe(!requestCallViewModel.getIsVideoForMe().getValue());


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

                        requestCallViewModel.setIsVideoForMe(!requestCallViewModel.getIsVideoForMe().getValue());

                        requestCallViewModel.setIsSpeaker(true);



                        SendSwitchTOVideoCallRespone(requestCallViewModel.getIsVideoForMe().getValue());
                        dialog.dismiss();

                    }
                });
        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestCallViewModel.setIsVideoForYou(!requestCallViewModel.getIsVideoForYou().getValue());


                SendSwitchTOVideoCallRespone(requestCallViewModel.getIsVideoForMe().getValue());
                                dialog.dismiss();


            }
        });


        alertDialog = dialog.create();
        alertDialog.show();
    }

    private void connectToSignallingServer() {

        isInitiator = true;

        isChannelReady = true;

    }
    //MirtDPM4
    private void doAnswer() {


        peerConnection.createAnswer(new SimpleSdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                System.out.println("onCreateSuccessdoAnswer");
                peerConnection.setLocalDescription(new SimpleSdpObserver(), sessionDescription);
                System.out.println();

                JSONObject message = new JSONObject();
                try {

                    message.put("callType",typeCall);
                    message.put("type", "answer");
                    message.put("sdp", sessionDescription.description);
                    message.put("my_id", classSharedPreferences.getUser().getUserId());
                    message.put("your_id", anthor_user_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                sendMessage(message);
//                requestCallViewModel.setRining(false);


            }
        }, new MediaConstraints());
    }

    private void maybeStart() {
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

                    sendMessage(message);

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


    private void createVideoTrackFromCameraAndShowIt() {


        VideoCapturer videoCapturer = createVideoCapturer();
//        VideoSource videoSource;
        //Create a VideoSource instance
        if (videoCapturer != null) {
             surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", rootEglBase.getEglBaseContext());
            videoSource = factory.createVideoSource(videoCapturer.isScreencast());
            videoCapturer.initialize(surfaceTextureHelper, this, videoSource.getCapturerObserver());
            System.out.println("my video camera");

        }


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
        mediaStream.addTrack(localVideoTrack);


        mediaStream.addTrack(localAudioTrack);
        peerConnection.addStream(mediaStream);
        JSONObject message = new JSONObject();
        try {
            message.put("type", "got user media");
            message.put("your_id", anthor_user_id);
            message.put("my_id", classSharedPreferences.getUser().getUserId());


        } catch (JSONException e) {
            e.printStackTrace();
        }

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

        }
        if (binding.surfaceView2 != null) {
            binding.surfaceView2.release();

        }
    }
    private void showInCallNotification() {

        Intent intent
                = new Intent(this, RequestCallActivity.class);
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
                PendingIntent.FLAG_CANCEL_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        /////////intent for reject
        Intent intentCancel
                = new Intent(this, CancelCallFromCallOngoingNotification.class);

        intentCancel.putExtra("id", anthor_user_id);

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
                .setUsesChronometer(true)


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

                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            notificationManager.createNotificationChannel(
                    notificationChannel);
            notificationManager.createNotificationChannelGroup(new NotificationChannelGroup(channel_id, "Memo"));

        }


        Notification note = builder.build();
        note.flags |= Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(AllConstants.onGoingCallChannelId,note);
    }
    public void setWindowParams() {
        isMinimize = !isMinimize;

        WindowManager.LayoutParams wlp = getWindow().getAttributes();
        if(isMinimize) {
            wlp.flags =
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

            wlp.gravity = Gravity.BOTTOM | Gravity.END;
            wlp.format = PixelFormat.TRANSLUCENT;
            wlp.width = 600;
            wlp.height = 1000;
            wlp.y = (int) (wlp.height * 0.18);
        }
        else{
            wlp.gravity = Gravity.CENTER;
            wlp.format = PixelFormat.TRANSLUCENT;
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        }
        getWindow().setAttributes(wlp);
    }
    androidx.appcompat.app.AlertDialog dialog;

    private void requestOverlayDisplayPermission() {
        //An AlertDialog is created
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        //This dialog can be closed, just by taping anywhere outside the dialog-box
        builder.setCancelable(true);
        //The title of the Dialog-box is set
        builder.setTitle("Screen Overlay Permission Needed");
        //The message of the Dialog-box is set
        builder.setMessage("Enable 'Display over other apps' from System Settings.");
        //The event of the Positive-Button is set
        builder.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //The app will redirect to the 'Display over other apps' in Settings.
                //This is an Implicit Intent. This is needed when any Action is needed to perform, here it is
                //redirecting to an other app(Settings).
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                //This method will start the intent. It takes two parameter, one is the Intent and the other is
                //an requestCode Integer. Here it is -1.
                startActivityForResult(intent, RESULT_OK);
            }
        });
        dialog = builder.create();
        //The Dialog will show in the screen
        dialog.show();
    }

    private boolean checkOverlayDisplayPermission() {
        //Android Version is lesser than Marshmallow or the API is lesser than 23
        //doesn't need 'Display over other apps' permission enabling.
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            //If 'Display over other apps' is not enabled it will return false or else true
            if (!Settings.canDrawOverlays(this)) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

}

