package com.yawar.memo.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.yawar.memo.Api.ClassSharedPreferences;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.fragment.ChatRoomFragment;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.repositry.BlockUserRepo;
import com.yawar.memo.views.ConversationActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import io.socket.client.IO;
import io.socket.client.Socket;
import com.yawar.memo.utils.BaseApp;
import com.yawar.memo.views.DashBord;
import com.yawar.memo.views.DevicesLinkActivity;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

public class SocketIOService extends Service implements SocketEventListener.Listener, HeartBeat.HeartBeatListener, Observer {
    public static final String KEY_BROADCAST_MESSAGE = "b_message";
    public static final int EVENT_TYPE_JOIN = 1, EVENT_TYPE_MESSAGE = 2,
            EVENT_TYPE_TYPING = 3,EVENT_TYPE_ENTER = 4,EVENT_TYPE_CHECK_CONNECT=5,
            EVENT_TYPE_ON_SEEN=6,EVENT_TYPE_Forward=7,EVENT_TYPE_ON_DELETE =8,
            EVENT_TYPE_CHECK_QR =9,EVENT_TYPE_GET_QR =10,EVENT_TYPE_DISCONNECT=11, EVENT_TYPE_BLOCK = 12, EVENT_TYPE_UN_BLOCK = 13;
    public static final String EVENT_DELETE = "delete message";
    private static final String EVENT_MESSAGE = "new message";
    private static final String EVENT_CHANGE = "change";
    private static final String CHECK_CONNECT= "check connect";
    private static final String FORWARD= "forward message";
    private static final String CHECK_QR= "checkQrKey";
    private static final String GET_QR= "getIdForUser";
    private static final String BLOCK_USER= "block";
    private static final String UNBLOCK_USER= "unblock";






    private static final String EVENT_JOIN = "join";
    private static final String EVENT_RECEIVED = "received";
    private static final String EVENT_TYPING = "on typing";
    private static final String NEW_CHAT = "new chat";
    Bitmap bitmap;
    String imageString;


    public static final String EXTRA_DATA = "extra_data_message";
    public static final String EXTRA_ROOM_ID = "extra_room_id";
//    public static final String EXTRA_USER_ENTER = "extra_user_enter";
    public static final String EXTRA_ENTER_PARAMTERS = "extra_enter_paramters";
    public static final String EXTRA_ON_DELETE_PARAMTERS = "extra_on_delete_paramters";
    public static final String EXTRA_BLOCK_PARAMTERS = "extra_block_paramters";
    public static final String EXTRA_UN_BLOCK_PARAMTERS = "extra_un_block_paramters";




    public static final String EXTRA_CHECK_CONNECT_PARAMTERS = "extra_check_connect_paramters";
    public static final String EXTRA_TYPING_PARAMTERS = "extra_typing_paramters";
    public static final String EXTRA_NEW_MESSAGE_PARAMTERS = "extra_new_message_paramters";
    public static final String EXTRA_FORWARD_MESSAGE_PARAMTERS = "extra_fowrward_message_paramters";
    public static final String EXTRA_CHECK_QR_PARAMTERS = "extra_check_qr_paramters";
    public static final String EXTRA_GET_QR_PARAMTERS = "extra_get_qr_paramters";




    public static final String EXTRA_ON_SEEN_PARAMTERS = "extra_on_seen_paramters";
    public static final String EXTRA_EVENT_TYPE = "extra_event_type";

    private static final String TAG = SocketIOService.class.getSimpleName();
    private Socket mSocket;
    private Boolean isConnected = true;
    private boolean mTyping;
    private Queue<Message> chatQueue;
    String my_id;
    BlockUserRepo blockUserRepo;
     ClassSharedPreferences classSharedPreferences;
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private HeartBeat heartBeat;
    private String room_id;
    BaseApp myBase;

    private ConcurrentHashMap<String, SocketEventListener> listenersMap;

    //-------------------------------------------------------------------------------------------
    private IO.Options IOOption;
    public static final String EXTRA_EVENT_SEND_MESSAGE = "message_detection";

    @Override
    public void update(Observable observable, Object o) {

    }

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.arg1) {
                case 1:
                    Log.w(TAG, "Connected");

                    break;
                case 2:
                    Log.w(TAG, "Disconnected");
                    stopSelfResult(msg.arg1);



                    break;
                case 3:
                    Log.w(TAG, "Error in Connection");

                    break;
            }
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        IOOption = new IO.Options();
//        IOOption.query = "public_key=" + new SessionManager(getApplicationContext()).getPublicKey();
        chatQueue = new LinkedList<>();
        classSharedPreferences = new ClassSharedPreferences(this);
        my_id = classSharedPreferences.getUser().getUserId();
        listenersMap = new ConcurrentHashMap<>();
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread(TAG + "Args",
                THREAD_PRIORITY_BACKGROUND);
        thread.start();
        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        myBase = (BaseApp) getApplication();
        blockUserRepo = myBase.getBlockUserRepo();
        myBase.getObserver().addObserver(this);



        try {
            mSocket = IO.socket(AllConstants.socket_url);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        getSocketListener();

        for (Map.Entry<String, SocketEventListener> entry : listenersMap.entrySet()) {
            mSocket.on(entry.getKey(), entry.getValue());
        }
        /*mSocket.on("user joined", new SocketEventListener("user joined", this));
        mSocket.on("user left", new SocketEventListener("user left", this));
        mSocket.on("typing", new SocketEventListener("typing", this));
        mSocket.on("stop typing", new SocketEventListener("stop typing", this));*/
        if (!isConnected && !mSocket.connected()) {
            mSocket.connect();
            joinSocket();
            System.out.println("Sockettttttttttt is connecttttttttttttttt");
        }
        heartBeat = new HeartBeat(this);
//        heartBeat.start();
    }

    private void getSocketListener() {
//        socket.connect();
//        socket.on("connect user", onNewUser);
//        socket.on("check connect", check);
//        socket.on("on typing", onTyping);
//        socket.on("new message", onNewMessage);
        listenersMap.put(Socket.EVENT_CONNECT, new SocketEventListener(Socket.EVENT_CONNECT, this));
//        listenersMap.put(Socket.EVENT_DISCONNECT, new SocketEventListener(Socket.EVENT_DISCONNECT, this));
//        listenersMap.put(Socket.EVENT_CONNECT_ERROR, new SocketEventListener(Socket.EVENT_CONNECT_ERROR, this));
//        listenersMap.put(Socket.EVENT_CONNECT_TIMEOUT, new SocketEventListener(Socket.EVENT_CONNECT_TIMEOUT, this));

        listenersMap.put("connect user", new SocketEventListener("connect user", this));
        listenersMap.put("enter", new SocketEventListener("enter", this));
        listenersMap.put("seen", new SocketEventListener("seen", this));
        listenersMap.put("new chat", new SocketEventListener("new chat", this));
        listenersMap.put("forward message", new SocketEventListener("forward message", this));
        listenersMap.put("delete message", new SocketEventListener("delete message", this));
        listenersMap.put("checkQrKey", new SocketEventListener("checkQrKey", this));
        listenersMap.put("getIdForUser", new SocketEventListener("getIdForUser", this));
        listenersMap.put("block", new SocketEventListener("block", this));
        listenersMap.put("unblock", new SocketEventListener("unblock", this));











        listenersMap.put("check connect", new SocketEventListener("check connect", this));
        listenersMap.put("on typing", new SocketEventListener("on typing", this));
        listenersMap.put("new message", new SocketEventListener("new message", this));
    }

    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("onBinddddddd");


        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        (TAG, "onStartCommand");

        System.out.println("onStartCommand");
        if (intent != null) {
            int eventType = intent.getIntExtra(EXTRA_EVENT_TYPE, EVENT_TYPE_JOIN);

            switch (eventType) {

                case EVENT_TYPE_JOIN:
                    System.out.println("EVENT_TYPE_JOIN");

//                    room_id = intent.getStringExtra(EXTRA_ROOM_ID);
                    if (!mSocket.connected()) {
                        System.out.println("connectttttttttttttted");
                        mSocket.connect();
                    }
                    joinSocket();
                    break;
                case EVENT_TYPE_MESSAGE:
                    System.out.println("EVENT_TYPE_MESSAGEEEEEEE");

                    String chat = intent.getExtras().getString(EXTRA_NEW_MESSAGE_PARAMTERS);
                    if (isSocketConnected()) {
                        sendMessage(chat);
                    }
                    break;
                case EVENT_TYPE_DISCONNECT:
                    System.out.println("EVENT_TYPE_MESSAGEEEEEEE");

                    if (isSocketConnected()) {
                        System.out.println("Lowwwwwwwwwwww");
                        mSocket.disconnect();
//                        heartBeat.stop();
//                        for (Map.Entry<String, SocketEventListener> entry : listenersMap.entrySet()) {
//                            mSocket.off(entry.getKey(), entry.getValue());
//                        }
                    }
                    break;
                case EVENT_TYPE_TYPING:
                    System.out.println("EVENT_TYPE_TYPING");


                    if (isSocketConnected()) {
//                        sendMessage(chat, eventType);
                        String typingString = intent.getExtras().getString(EXTRA_TYPING_PARAMTERS);

                        onTyping(typingString);
                    }
                    break;

                case EVENT_TYPE_ENTER:
                    System.out.println("EVENT_TYPE_ENTER");

                    String paramter = intent.getExtras().getString(EXTRA_ENTER_PARAMTERS);

                    if (isSocketConnected()) {
                       enter(paramter );
                    }
                    break;
                case EVENT_TYPE_CHECK_CONNECT:
                    System.out.println("EVENT_TYPE_CHECK_CONNECT");

                    String checkParamter = intent.getExtras().getString(EXTRA_CHECK_CONNECT_PARAMTERS);

                    if (isSocketConnected()) {
                        System.out.println("isSocketConnected");

                        checkConnect(checkParamter);
                    }
                    break;
                case EVENT_TYPE_ON_SEEN:
                    System.out.println("EVENT_TYPE_ON_SEEN");

                    String seenParamter = intent.getExtras().getString(EXTRA_ON_SEEN_PARAMTERS);

                    if (isSocketConnected()) {
                        System.out.println("isSocketConnected");

                        onSeen(seenParamter);
                    }
                    break;

                case EVENT_TYPE_Forward:
                    System.out.println("EVENT_TYPE_Forward");

                    String forward_paramter = intent.getExtras().getString(EXTRA_FORWARD_MESSAGE_PARAMTERS);

                    if (isSocketConnected()) {
                        forwardMessage(forward_paramter);
                    }
                    break;
                case EVENT_TYPE_ON_DELETE:
                    System.out.println("EVENT_TYPE_ON_DELETE");

                    String delete_paramter = intent.getExtras().getString(EXTRA_ON_DELETE_PARAMTERS);

                    if (isSocketConnected()) {
                        deleteMessages(delete_paramter);
                    }
                    break;
                case EVENT_TYPE_CHECK_QR:
                    System.out.println("EVENT_TYPE_Check_QR");

                    String qr_paramter = intent.getExtras().getString(EXTRA_CHECK_QR_PARAMTERS);

                    if (isSocketConnected()) {
                        System.out.println("qr is connect");
                        checkQr(qr_paramter);
                    }
                    break;
                case EVENT_TYPE_GET_QR:
                    System.out.println("EVENT_TYPE_get_QR");

                    String qr_get_paramter = intent.getExtras().getString(EXTRA_GET_QR_PARAMTERS);

                    if (isSocketConnected()) {
                        System.out.println("qr is connect");
                        getQr(qr_get_paramter);
                    }
                    break;
                case EVENT_TYPE_BLOCK:
                    System.out.println("EVENT_TYPE_block");

                    String block_paramter = intent.getExtras().getString(EXTRA_BLOCK_PARAMTERS);

                    if (isSocketConnected()) {
                        System.out.println("blocked is connect");
                        blockUser(block_paramter);
                    }
                    break;
                case EVENT_TYPE_UN_BLOCK:
                    System.out.println("EVENT_TYPE_block");

                    String unBlock_paramter = intent.getExtras().getString(EXTRA_UN_BLOCK_PARAMTERS);

                    if (isSocketConnected()) {
                        System.out.println("unblocked is connect");
                        unBlockUser(unBlock_paramter);
                    }
                    break;


            }
        }
        return START_STICKY;
    }

    private boolean isSocketConnected() {
        if (null == mSocket) {
            return false;
        }
        if (!mSocket.connected()) {
            mSocket.connect();
            joinSocket();
            Log.i(TAG, "reconnecting socket...");
            return false;
        }
        return true;
    }

    @Override
    public void onHeartBeat() {
        Log.e(TAG, "onHeartBeat: " );
        if (mSocket != null && !mSocket.connected()) {
            mSocket.connect();
            joinSocket();

        }
    }

    private void joinSocket() {
//        if (TextUtils.isEmpty(room_id)) {
//            //null can not join chat
//            return;
//        }
        JSONObject userId = new JSONObject();
        try {
            userId.put("user_id",  my_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("connect user", userId);
    }

    private void sendMessage(String messageObject) {
        JSONObject chat = null;
        JSONObject jsonObjec = new JSONObject();
        String type= "";
        try {
            chat = new JSONObject(messageObject);
        } catch (JSONException e) {
            e.printStackTrace();}

                mSocket.emit("new message", chat);




    }
    private void enter(String messageObject) {
        JSONObject chat = null;
        try {

            chat = new JSONObject(messageObject);

            mSocket.emit("enter", chat);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void checkConnect(String messageObject) {
        JSONObject chat = null;
        joinSocket();
        try {

            chat = new JSONObject(messageObject);
            System.out.println(chat.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("check connect", chat);

    }
    private void onTyping(String messageObject) {
        JSONObject typingObject = null;
        try {

            typingObject = new JSONObject(messageObject);
            System.out.println(typingObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("on typing", typingObject);

    }
    private void onSeen(String seenObject) {
        JSONObject chat = null;
        try {
            chat = new JSONObject(seenObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(seenObject);
        mSocket.emit("seen", chat);
    }
    private void forwardMessage(String messageObject) {
        JSONObject chat = null;
        JSONObject object1 = new JSONObject();
        JSONObject object2 = new JSONObject();

        try {
            chat = new JSONObject(messageObject);
//            System.out.println(chat.getString("id"));
//            System.out.println(chat.getString("message_id"));
//            object1.put("id",chat.getString("id"));
//            object1.put("message_id",chat.getString("message_id"));
       } catch (JSONException e) {
            e.printStackTrace();}
        mSocket.emit("forward message",chat);




    }
    private void deleteMessages(String messageObject) {
        JSONObject chat = null;


        try {
            chat = new JSONObject(messageObject);

        } catch (JSONException e) {
            e.printStackTrace();}
        mSocket.emit("delete message",chat);




    }
    private void checkQr(String messageObject) {
        JSONObject chat = null;
        joinSocket();
        try {

            chat = new JSONObject(messageObject);
            System.out.println(chat.toString()+"qr paramterssssssssss");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(" checkQrKey Emittttttttttttt");

        mSocket.emit("checkQrKey", chat);

    }
    private void getQr(String messageObject) {
        JSONObject chat = null;
        joinSocket();
        try {

            chat = new JSONObject(messageObject);
            System.out.println(chat.toString()+"getIdForUser paramterssssssssss");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(" getIdForUser Emittttttttttttt");

        mSocket.emit("getIdForUser", chat);

    }
    private void blockUser(String messageObject) {
        JSONObject chat = null;
        joinSocket();
        try {

            chat = new JSONObject(messageObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(chat.toString()+"blocked paramters");
        mSocket.emit("block", chat);

    }
    private void unBlockUser(String messageObject) {
        JSONObject chat = null;
        joinSocket();
        try {

            chat = new JSONObject(messageObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(chat.toString()+"blocked paramters");
        mSocket.emit("unblock", chat);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        System.out.println("destroyyyyyyyyyyyyyyyy");
        mSocket.disconnect();
        mSocket.close();
        heartBeat.stop();
        for (Map.Entry<String, SocketEventListener> entry : listenersMap.entrySet()) {
            mSocket.off(entry.getKey(), entry.getValue());
        }
//        stopService(getSystemService("name"));


    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.out.println("Lowwwwwwwwwwww");
        mSocket.disconnect();
        heartBeat.stop();
        for (Map.Entry<String, SocketEventListener> entry : listenersMap.entrySet()) {
            mSocket.off(entry.getKey(), entry.getValue());
        }

    }
//    @Override
//    public void onResume() {
//        super.onR();
//        System.out.println("destroyyyyyyyyyyyyyyyy");
//        mSocket.disconnect();
//        heartBeat.stop();
//        for (Map.Entry<String, SocketEventListener> entry : listenersMap.entrySet()) {
//            mSocket.off(entry.getKey(), entry.getValue());
//        }
//    }
@Override
public void onTaskRemoved(Intent rootIntent) {
//    Intent restartServiceIntent = new Intent(getApplicationContext(),this.getClass());
//    restartServiceIntent.setPackage(getPackageName());
//    startService(restartServiceIntent);
    System.out.println("on task removeeeeed");
    super.onTaskRemoved(rootIntent);
}

    @Override
    public void onEventCall(String event, Object... args) {
        JSONObject data;
        System.out.println("deletttttttttttttttt");
        Intent intent;
        switch (event) {
            case Socket.EVENT_CONNECT:
                android.os.Message msg = mServiceHandler.obtainMessage();
                msg.arg1 = 1;
                mServiceHandler.sendMessage(msg);
                isConnected = true;
                intent = new Intent(ChatRoomFragment.ON_SOCKET_CONNECTION);
                intent.putExtra("status", true);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                break;
            case Socket.EVENT_DISCONNECT:
                Log.w(TAG, "socket disconnected");
                isConnected = false;
                msg = mServiceHandler.obtainMessage();
                msg.arg1 = 2;
                mServiceHandler.sendMessage(msg);
                intent = new Intent(ChatRoomFragment.ON_SOCKET_CONNECTION);
                intent.putExtra("status", false);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                break;
            case Socket.EVENT_CONNECT_ERROR:
                isConnected = false;
                msg = mServiceHandler.obtainMessage();
                msg.arg1 = 3;
                mServiceHandler.sendMessage(msg);
                // reconnect
                mSocket.connect();
                break;
//            case Socket.EVENT_CONNECT_TIMEOUT:
//                if (!mTyping) return;
//                mTyping = false;
//                mSocket.emit("stop typing");
//                break;
            case EVENT_MESSAGE:
                data = (JSONObject) args[0];
                String text="";
                String chatId="";
                try {
                    text = data.getString("message");
                   chatId = data.getString("chat_id");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                intent = new Intent(ConversationActivity.ON_MESSAGE_RECEIVED);
                intent.putExtra("message", data.toString());
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                break;
            case CHECK_CONNECT:
//                data = (JSONObject) args[0];

                intent = new Intent(ConversationActivity.CHEK);
                intent.putExtra("check", args[0].toString());
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                break;
            case EVENT_TYPING:
//                data = (JSONObject) args[0];
                System.out.println(args[0].toString());
                intent = new Intent(ConversationActivity.TYPING);
                intent.putExtra("typing", args[0].toString());
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                break;
            case NEW_CHAT:
//                data = (JSONObject) args[0];
                System.out.println(args[0].toString()+"NEW_CHAT");
                intent = new Intent(DashBord.NEW_MESSAGE);
                intent.putExtra("new chat", args[0].toString());
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                break;
            case EVENT_DELETE:
//                data = (JSONObject) args[0];
                System.out.println(args[0].toString()+"NEW_DELETW");
                intent = new Intent(ConversationActivity.ON_MESSAGE_DELETED);
                intent.putExtra("delete message", args[0].toString());
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                break;
            case CHECK_QR:
//                data = (JSONObject) args[0];
                System.out.println(args[0].toString()+"NEW_DELETW");
                intent = new Intent(DevicesLinkActivity.SCAN_QR);
                intent.putExtra("scan qr", args[0].toString());
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                break;
            case GET_QR:
                System.out.println(args[0].toString()+"NEW_GET_GR");
                intent = new Intent(DevicesLinkActivity.GET_QR);
                intent.putExtra("get qr", args[0].toString());
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                break;

            case BLOCK_USER:
                System.out.println(args[0].toString()+"NEW_Block");
                String userDoBlock = "";
                String userBlock = "";
                String blockedFor = "";
                String name = "";
                String image = "";
                String special_number = "";
                try {
                    JSONObject jsonObject = new JSONObject(args[0].toString());
                    userDoBlock = jsonObject.getString("my_id");
                    userBlock = jsonObject.getString("user_id");
                    blockedFor = jsonObject.getString("blocked_for");
                    name = jsonObject.getString("userDoBlockName");
                    special_number = jsonObject.getString("userDoBlockSpecialNumber");
                    image = jsonObject.getString("userDoBlockImage");




                    if(userBlock.equals(my_id)){
                        System.out.println("doooo it");
                        UserModel userModel = new UserModel(userDoBlock,name,"","","",special_number,image,blockedFor);
                        blockUserRepo.addBlockUser(userModel);
                    }





                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case UNBLOCK_USER:
                System.out.println(args[0].toString()+"NEW_UNBlock");
                String userDoUnBlock = "";
                String userUnBlock = "";
                String unBlockedFor = "";

                try {
                    JSONObject jsonObject = new JSONObject(args[0].toString());
                    userDoUnBlock = jsonObject.getString("my_id");
                    userUnBlock = jsonObject.getString("user_id");
                    unBlockedFor = jsonObject.getString("blocked_for");





                    if(userUnBlock.equals(my_id)){
                        System.out.println("doooo it");
                        blockUserRepo.deleteBlockUser(userDoUnBlock,unBlockedFor);
                    }





                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}