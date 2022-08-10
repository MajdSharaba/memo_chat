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
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yawar.memo.databinding.ActivityCallMainBinding;
import com.yawar.memo.modelView.ResponeCallViewModel;
import com.yawar.memo.notification.CancelCallFromCallOngoingNotification;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.R;
import com.yawar.memo.service.SocketIOService;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.DataChannel;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;

import io.socket.client.Socket;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ResponeCallActivity extends AppCompatActivity {
    String username = "";
    String friendsUsername = "";
    String peerId = null;
    Boolean isPeerConnected = false;
    String id ="0";
    String title = "";
    String onGoingTitle = "";
    private AudioManager audioManager;


    private final int requestcode = 1;
    public static final String ON_CALL_REQUEST = "CallMainActivity.ON_CALL_REQUEST";
    public static final String ON_STOP_CALLING_REQUEST = "RequestCallActivity.ON_CALL_REQUEST";
    public static final String ON_RECIVED_SETTINGS_CALL = "CallMainActivity.ON_RECIVED_SETTINGS_CALL";
    public static final String ON_CLOSE_CALL_FROM_NOTIFICATION_CALL_ACTIVITY = "ON_CLOSE_CALL_FROM_NOTIFICATION_CALL_ACTIVITY";
    public static final String ON_RECIVED_ASK_FOR_VIDEO = "on_recived_ask_for_video";
    public static final String ON_RECIVED_RESPONE_FOR_VIDEO = "on_recived_respone_for_video";










    Boolean isAudio = true;
    Boolean isVideoForMe = false;
    Boolean isVideoForyou = false;
    VideoCapturer videoCapturer;


    Boolean isVideoCall = false;
    Button callBtn;
    String uniqueId = "";
//    ImageView acceptBtn;
//    ImageView rejectBtn;

    private static final String[] permissions = {android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO};
    EditText friendNameEdit;
//    WebView webView;
    TextView incomingCallTxt;
//    RelativeLayout callLayout;
//    RelativeLayout inputLayout;
    LinearLayout layoutCallProperties;
    ImageButton imgBtnStopCallLp;
    ImageButton imgBtnOpenCameraCallLp;
    ImageButton imgBtnOpenAudioCallLp;
    ImageButton imgBtnSwitchCamera;
    ClassSharedPreferences classSharedPreferences;
//    CircleImageView imageCallUser;
    String callString = null;
    AlertDialog alertDialog,alertDialogForME;
    AlertDialog.Builder dialogForMe ;
    ResponeCallViewModel responeCallViewModel;
    private static final String TAG = "CompleteActivity";
    private static final int RC_CALL = 111;
    public static final String VIDEO_TRACK_ID = "ARDAMSv0";
    public static final int VIDEO_RESOLUTION_WIDTH = 1280;
    public static final int VIDEO_RESOLUTION_HEIGHT = 720;
    public static final int FPS = 30;

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
                                System.out.println("iduser" + id);

                                peerConnection.setRemoteDescription(new SimpleSdpObserver(), new SessionDescription(OFFER, sdp));
                                doAnswer(anthor_id);
                            }
                        }


                        else if (type.equals("answer") ) {
                            sdp = jsonObject.getString("sdp");
                            if (id.equals(classSharedPreferences.getUser().getUserId())) {
                                System.out.println("answermessageeeee");

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
//                    System.out.println("the view visabilty is"+webView.getVisibility());

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
        System.out.println("Close Call Senttt");
        Intent service = new Intent(this, SocketIOService.class);
        JSONObject data = new JSONObject();
        try {
//            data.put("close_call", true);
            data.put("id", anthor_user_id);//            data.put("snd_id", classSharedPreferences.getUser().getUserId());
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        showWhenLockedAndTurnScreenOn();
//        setContentView(R.layout.activity_call_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_call_main);
        start();






        LocalBroadcastManager.getInstance(this).registerReceiver(reciveStopCalling, new IntentFilter(ON_STOP_CALLING_REQUEST));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveSettingsCalling, new IntentFilter(ON_RECIVED_SETTINGS_CALL));
        Intent closeIntent = new Intent(CallNotificationActivity.ON_CLOSE_CALL_FROM_NOTIFICATION);

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(closeIntent);
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveclosecallfromnotification, new IntentFilter(ON_CLOSE_CALL_FROM_NOTIFICATION_CALL_ACTIVITY));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveAskForCall, new IntentFilter(ON_RECIVED_ASK_FOR_VIDEO));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveAcceptChangeToVideoCall, new IntentFilter(ON_RECIVED_RESPONE_FOR_VIDEO));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveMessageCall, new IntentFilter(ON_RECIVE_MESSAGE));




        responeCallViewModel = new ViewModelProvider(this).get(ResponeCallViewModel.class);


        classSharedPreferences = new ClassSharedPreferences(this);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        imgBtnStopCallLp = findViewById(R.id.close_call_layout);
        imgBtnOpenCameraCallLp = findViewById(R.id.image_video_call_layout);
        imgBtnOpenAudioCallLp = findViewById(R.id.image_audio_call_layout);
        imgBtnSwitchCamera = findViewById(R.id.image_switch_camera);
//
        layoutCallProperties = findViewById(R.id.video_rl);
        layoutCallProperties.setVisibility(View.VISIBLE);

        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id", "0");
        callString = bundle.getString("callRequest", "code");
        title = bundle.getString("title", "");
        System.out.println("call string is that"+callString);


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
            if(isVideoForyou){
                onGoingTitle = getResources().getString(R.string.ongoing_video_call);
            }
            else {
                onGoingTitle = getResources().getString(R.string.ongoing_audio_call);

            }
            responeCallViewModel.isSpeaker.setValue(isVideoForyou);

            username = userObject.getString("name");
            anthor_user_id = message.getString("snd_id");





        } catch (JSONException e) {
            e.printStackTrace();
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(-1);


//        incomingCallTxt.setText(title+" "+username);
        showInCallNotification();
        maybeStart();
//        sendPeerId(callString,"majd");




        responeCallViewModel.isVideoForMe.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
                if(s){
                    createVideoCapturer();
                    imgBtnOpenCameraCallLp.setImageResource(R.drawable.ic_baseline_videocam_off_24);
                    binding.webRtcRelativeLayout.setVisibility(View.VISIBLE);

                    layoutCallProperties.setVisibility(View.VISIBLE);
                    binding.imageUserCalling.setVisibility(View.GONE);


                }
                else {
                    imgBtnOpenCameraCallLp.setImageResource(R.drawable.ic_baseline_videocam_24);

                    try {
                        System.out.println("Capture off");
                        videoCapturer.stopCapture();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(!responeCallViewModel.isVideoForYou.getValue()) {

                        binding.webRtcRelativeLayout.setVisibility(View.GONE);
                        layoutCallProperties.setVisibility(View.VISIBLE);
                        binding.imageUserCalling.setVisibility(View.VISIBLE);

                    }

                }
//                videoTrackFromCamera.setEnabled(s);



            }
        });
        responeCallViewModel.isVideoForYou.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
                if(s){
                    binding.webRtcRelativeLayout.setVisibility(View.VISIBLE);

                    layoutCallProperties.setVisibility(View.VISIBLE);
                    binding.imageUserCalling.setVisibility(View.GONE);}
                else{
                    if(!responeCallViewModel.isVideoForMe.getValue()){
//                        webView.setVisibility(View.GONE);
                        binding.webRtcRelativeLayout.setVisibility(View.GONE);
                        layoutCallProperties.setVisibility(View.VISIBLE);
                        binding.imageUserCalling.setVisibility(View.VISIBLE);

                    }




                }
//                callJavascriptFunction("javascript:toggleStream(\"" + s+ "\")");

            }
        });


        responeCallViewModel.isAudio.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
//                callJavascriptFunction("javascript:toggleAudio(\"" + s + "\")");

                if(s){
                    imgBtnOpenAudioCallLp.setImageResource(R.drawable.ic_baseline_mic_off_24);
                }
                else{
                    imgBtnOpenAudioCallLp.setImageResource(R.drawable.ic_baseline_mic_24);

                    }
                localAudioTrack.setEnabled(s);



                }


        });
        responeCallViewModel.isSpeaker.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {

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
                switchCamera();

            }
        });
        //////////////////////////////


    }


    @Override
    protected void onDestroy() {
//        webView.loadUrl("about:blank");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveStopCalling);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveSettingsCalling);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(Integer.parseInt("0"));

        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveclosecallfromnotification);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveAskForCall);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveMessageCall);
        if (surfaceTextureHelper != null) {
            surfaceTextureHelper.dispose();
            surfaceTextureHelper = null;
        }
        peerConnection.dispose();
        videoCapturer.dispose();
        factory.dispose();;
        rootEglBase.release();




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

//            connectToSignallingServer();

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
//        System.out.println("doAnswermajdddd");

        peerConnection.createAnswer(new SimpleSdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                System.out.println("onCreateSuccessdoAnswer");
                peerConnection.setLocalDescription(new SimpleSdpObserver(), sessionDescription);

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
            isStarted = true;}
//            if (isInitiator) {
        doCall();
//            }

    }

    private void doCall() {
        Log.d(TAG, "doCall: ");

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
                    System.out.println("messageeeeeeeee"+message);
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

    private void initializePeerConnectionFactory() {
        System.out.println("initializePeerConnectionFactory");
        PeerConnectionFactory.initializeAndroidGlobals(this, true, true, true);
        factory = new PeerConnectionFactory(null);
        factory.setVideoHwAccelerationOptions(rootEglBase.getEglBaseContext(), rootEglBase.getEglBaseContext());
    }

    private void createVideoTrackFromCameraAndShowIt() {
        audioConstraints = new MediaConstraints();
        VideoCapturer videoCapt = createVideoCapturer();
        VideoSource videoSource = factory.createVideoSource(videoCapt);
        videoCapt.startCapture(VIDEO_RESOLUTION_WIDTH, VIDEO_RESOLUTION_HEIGHT, FPS);

        videoTrackFromCamera = factory.createVideoTrack(VIDEO_TRACK_ID, videoSource);
        videoTrackFromCamera.setEnabled(true);
        videoTrackFromCamera.addRenderer(new VideoRenderer(binding.surfaceView));

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
        String URL = "stun:stun.l.google.com:19302";
        iceServers.add(new PeerConnection.IceServer(URL));

        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(iceServers);
        MediaConstraints pcConstraints = new MediaConstraints();

        PeerConnection.Observer pcObserver = new PeerConnection.Observer() {
            @Override
            public void onSignalingChange(PeerConnection.SignalingState signalingState) {
                Log.d(TAG, "onSignalingChange: ");
            }

            @Override
            public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
                Log.d(TAG, "onIceConnectionChange: ");
            }

            @Override
            public void onIceConnectionReceivingChange(boolean b) {
                Log.d(TAG, "onIceConnectionReceivingChange: ");
            }

            @Override
            public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
                Log.d(TAG, "onIceGatheringChange: ");
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
                Log.d(TAG, "onIceCandidatesRemoved: ");
            }

            @Override
            public void onAddStream(MediaStream mediaStream) {
                Log.d(TAG, "onAddStream: " + mediaStream.videoTracks.size());
                VideoTrack remoteVideoTrack = mediaStream.videoTracks.get(0);
                AudioTrack remoteAudioTrack = mediaStream.audioTracks.get(0);
                remoteAudioTrack.setEnabled(true);
                remoteVideoTrack.setEnabled(true);
                remoteVideoTrack.addRenderer(new VideoRenderer(binding.surfaceView2));

            }

            @Override
            public void onRemoveStream(MediaStream mediaStream) {
                Log.d(TAG, "onRemoveStream: ");
            }

            @Override
            public void onDataChannel(DataChannel dataChannel) {
                Log.d(TAG, "onDataChannel: ");
            }

            @Override
            public void onRenegotiationNeeded() {
                Log.d(TAG, "onRenegotiationNeeded: ");
            }
        };

        return factory.createPeerConnection(rtcConfig, pcConstraints, pcObserver);
    }

    private VideoCapturer createVideoCapturer() {
//        VideoCapturer videoCapturer;
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

                .setFullScreenIntent(null, false)

                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setSound(null)
                .setOngoing(true)
                .setGroup(anthor_user_id)


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