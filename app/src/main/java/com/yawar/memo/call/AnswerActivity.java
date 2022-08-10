package com.yawar.memo.call;

import static org.webrtc.SessionDescription.Type.ANSWER;
import static org.webrtc.SessionDescription.Type.OFFER;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.yawar.memo.R;
import com.yawar.memo.databinding.ActivityAnswerBinding;
import com.yawar.memo.databinding.ActivityCompleteBinding;
import com.yawar.memo.service.SocketIOService;
import com.yawar.memo.sessionManager.ClassSharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
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

public class AnswerActivity extends AppCompatActivity {

    private static final String TAG = "CompleteActivity";
    private static final int RC_CALL = 111;
    public static final String VIDEO_TRACK_ID = "ARDAMSv0";
    public static final int VIDEO_RESOLUTION_WIDTH = 1280;
    public static final int VIDEO_RESOLUTION_HEIGHT = 720;
    public static final int FPS = 30;

    private Socket socket;
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
    ClassSharedPreferences classSharedPreferences;
//    public static final String ON_RECIVE_MESSAGE = "ON_RECIVE_MESSAGE";


    private ActivityAnswerBinding binding;
    private PeerConnection peerConnection;
    private EglBase rootEglBase;
    private PeerConnectionFactory factory;
    private VideoTrack videoTrackFromCamera;

    //Firestore
//    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private void sendMessage( Object object) {
        System.out.println("sendMessage"+object.toString());
        Intent service = new Intent(this, SocketIOService.class);
//        JSONObject object = new JSONObject();

        service.putExtra(SocketIOService.EXTRA_SEND_MESSAGE_FOR_CALL_PARAMTES, object.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_SEND_MESSAGE_FOR_CALL);
        this.startService(service);
    }

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
                                maybeStart();}

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_answer);
//        setSupportActionBar(binding.toolbar);
        classSharedPreferences = new ClassSharedPreferences(this);
        start();

        Bundle bundle = getIntent().getExtras();
        System.out.println(bundle.getString("Call Sdp","")+"bundle.getString");
        String id = "";
        String type = "";
        String sdp = "";

        try {
            JSONObject jsonObject = new JSONObject(bundle.getString("Call Sdp",""));
            id = jsonObject.getString("your_id");
            type = jsonObject.getString("type");
            String anthor_id = jsonObject.getString("my_id");

            if (type.equals("got user media")) {
                if(!anthor_id.equals(classSharedPreferences.getUser().getUserId())){
                    System.out.println("got user media");
                    anthor_user_id = anthor_id;
                    maybeStart();}

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



//        LocalBroadcastManager.getInstance(this).registerReceiver(reciveMessageCall, new IntentFilter(ON_RECIVE_MESSAGE));


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onDestroy() {
        if (socket != null) {
            sendMessage("bye");
            socket.disconnect();
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveMessageCall);

        super.onDestroy();
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

//        doCall();

//        try {
        // For me this was "http://192.168.1.220:3000";
        // $ hostname -I
//            String URL = "https://calm-badlands-59575.herokuapp.com/";
////            String URL = "http://192.168.0.109:3000/";
////            http://192.168.0.109:3000/
//            Log.e(TAG, "REPLACE ME: IO Socket:" + URL);
//            socket = IO.socket(URL);
//
//
//            socket.on(EVENT_CONNECT, args -> {
//                Log.d(TAG, "connectToSignallingServer: connect");
//                socket.emit("create or join", "foo");
//            }).on("ipaddr", args -> {
//                Log.d(TAG, "connectToSignallingServer: ipaddr");
//            }).on("created", args -> {
//                Log.d(TAG, "connectToSignallingServer: created");
        isInitiator = true;
//            }).on("full", args -> {
//                Log.d(TAG, "connectToSignallingServer: full");
//            }).on("join", args -> {
//                Log.d(TAG, "connectToSignallingServer: join");
//                Log.d(TAG, "connectToSignallingServer: Another peer made a request to join room");
//                Log.d(TAG, "connectToSignallingServer: This peer is the initiator of room");
//                isChannelReady = true;
//            }).on("joined", args -> {
//                Log.d(TAG, "connectToSignallingServer: joined");
        isChannelReady = true;
//            }).on("log", args -> {
//                for (Object arg : args) {
//                    Log.d(TAG, "connectToSignallingServer: " + String.valueOf(arg));
//                }
//            }).on("message", args -> {
//                Log.d(TAG, "connectToSignallingServer: got a message");
//            }).on("message", args -> {
//                try {
//                    if (args[0] instanceof String) {
//                        String message = (String) args[0];
//                        if (message.equals("got user media")) {
//                            maybeStart();
//                        }
//                    } else {
//                        JSONObject message = (JSONObject) args[0];
//                        Log.d(TAG, "connectToSignallingServer: got message " + message);
//                        if (message.getString("type").equals("offer")) {
//                            Log.d(TAG, "connectToSignallingServer: received an offer " + isInitiator + " " + isStarted);
//                            if (!isInitiator && !isStarted) {
//                                maybeStart();
//                            }
//                            peerConnection.setRemoteDescription(new SimpleSdpObserver(), new SessionDescription(OFFER, message.getString("sdp")));
//                            doAnswer();
//                        } else if (message.getString("type").equals("answer") && isStarted) {
//                            peerConnection.setRemoteDescription(new SimpleSdpObserver(), new SessionDescription(ANSWER, message.getString("sdp")));
//                        } else if (message.getString("type").equals("candidate") && isStarted) {
//                            Log.d(TAG, "connectToSignallingServer: receiving candidates");
//                            IceCandidate candidate = new IceCandidate(message.getString("id"), message.getInt("label"), message.getString("candidate"));
//                            peerConnection.addIceCandidate(candidate);
//                        }
//                        /*else if (message === 'bye' && isStarted) {
//                        handleRemoteHangup();
//                    }*/
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }).on(EVENT_DISCONNECT, args -> {
//                Log.d(TAG, "connectToSignallingServer: disconnect");
//            });
//            socket.connect();
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
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
        VideoCapturer videoCapturer = createVideoCapturer();
        VideoSource videoSource = factory.createVideoSource(videoCapturer);
        videoCapturer.startCapture(VIDEO_RESOLUTION_WIDTH, VIDEO_RESOLUTION_HEIGHT, FPS);

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
//        JSONObject message = new JSONObject();
//        try {
//            message.put("type", "got user media");
//            message.put("your_id", anthor_user_id);
//            message.put("my_id", classSharedPreferences.getUser().getUserId());
//
//            if(!classSharedPreferences.getUser().getUserId().equals("171")){
//                sendMessage(message);
//                System.out.println("messageeeeeeeee"+message);}
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

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
        VideoCapturer videoCapturer;
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
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

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

}
