package com.yawar.memo.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.fragment.CallHistoryFragment;
import com.yawar.memo.language.helper.LocaleHelper;
import com.yawar.memo.permissions.Permissions;
import com.yawar.memo.repositry.AuthRepo;
import com.yawar.memo.repositry.ChatRoomRepoo;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.R;
import com.yawar.memo.fragment.ChatRoomFragment;
import com.yawar.memo.fragment.SearchFragment;
import com.yawar.memo.fragment.SettingsFragment;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.service.SocketIOService;
import com.yawar.memo.utils.BaseApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
//import com.yawar.memo.fragment.StoriesFragment;

public class DashBord extends AppCompatActivity implements Observer {

// test //

//    private ChipNavigationBar navigationBar;
    BottomNavigationView bottomNavigation;
    private Fragment fragment = null;
    private Permissions permissions;
    BaseApp myBase;
//    ChatRoomRepo chatRoomRepo;
    ChatRoomRepoo chatRoomRepoo;

    ClassSharedPreferences classSharedPreferences;
    String myId;
    AuthRepo authRepo;
    BottomNavigationView bottomNavigationView;

    public static final String NEW_MESSAGE ="new Message" ;
    public static final String ON_MESSAGE_RECEIVED = "ConversationActivity.ON_MESSAGE_RECEIVED";
    public static final String TYPING = "ConversationActivity.ON_TYPING";
    public static final String ON_BLOCK_USER = "ConversationActivity.ON_BLOCK_USER";
    public static final String ON_UN_BLOCK_USER = "ConversationActivity.ON_UN_BLOCK_USER";
    private static final int STORAGE_PERMISSION_CODE = 2000;
    private static final int Contact_PERMISSION_CODE = 1000;



    private void connectSocket() {
        Intent service = new Intent(this, SocketIOService.class);
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_JOIN);
        this.startService(service);
    }
    private final BroadcastReceiver reciveNewChat = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String objectString = intent.getExtras().getString("new chat");
            System.out.println(objectString+"new chattttttttttttttttttttttttttttttt");

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(objectString);
            } catch (JSONException e) {
                e.printStackTrace();
            }



            JSONObject message = null;
            JSONObject user = null;
            String text = "";
            String chatId= null;
//            String senderId = "";
//            String reciverId = "";
//            String id = "";
//            String fileName = "";
//            String chatId = "";

            try {

                /// JSONObject jsonObject= (JSONObject) messageJson.get("data");
                message = jsonObject.getJSONObject("message");
                text = message.getString("message");
                user = jsonObject.getJSONObject("user");
                chatId = jsonObject.getString("chat_id");


                if(!user.getString("id").equals(myId)) {
                    chatRoomRepoo.addChatRoom(new ChatRoomModel(
                            user.getString("first_name"),
                            user.getString("id"),
                            text,


                            user.getString("profile_image")
                            ,
                            false,
                            "0",
                            chatId,
                            "null",
                            "1",
                            false,
                            user.getString("user_token"),
                            user.getString("sn"),

                            message.getString("message_type"),
                            message.getString("state"),
                            message.getString("created_at"),
                             false,
                               "null",
                            user.getString("id")
                            ,""





                    ));
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

//
//
//
//
//
    };
    private final BroadcastReceiver reciveNwMessage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String objectString = intent.getExtras().getString("message");
            JSONObject message = null;
            try {
                message = new JSONObject(objectString);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            String text = "";
            String type = "";
            String state = "";
            String senderId = "";
            String reciverId = "";
            String id = "";
            String fileName = "";
            String chatId = "";
            String dateTime = "";
            String id_user = "";

            try {

                /// JSONObject jsonObject= (JSONObject) messageJson.get("data");
                id = message.getString("message_id");
                id_user = message.getString("id");
                text = message.getString("message");
                type = message.getString("message_type");
                state = message.getString("state");
                senderId = message.getString("sender_id");
//                        id = message.getString("message_id");
                reciverId = message.getString("reciver_id");
                chatId =  message.getString("chat_id");
                dateTime = message.getString("dateTime");
//                        fileName = message.getString("orginalName");


            } catch (JSONException e) {
                e.printStackTrace();
            }
            String anthor_id="";
            if(senderId.equals(myId)){
                anthor_id= reciverId;
            }
            else {
                anthor_id = senderId;
            }
            System.out.println("set last message"+message.toString());


                if (!id.equals("0000")){
                    chatRoomRepoo.setLastMessage(text, chatId, myId, anthor_id, type, state, dateTime, senderId);
            }
                else{
                    chatRoomRepoo.updateLastMessageState(state,chatId);
                }

        }





    };
    private final BroadcastReceiver reciveTyping = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String typingString = intent.getExtras().getString("typing");
            System.out.println("typinnnnnnnnnnnnnnnng");
            JSONObject message = null;
            String isTyping = "false";
            String anthor_id = "";
            String chat_id = "";

            try {
                message = new JSONObject(typingString);
                isTyping = message.getString("typing");
                anthor_id = message.getString("my_id");
                chat_id = message.getString("chat_id");


            } catch (JSONException e) {
                e.printStackTrace();
            }
            chatRoomRepoo.setTyping(chat_id, isTyping.equals("true"));
        }
    };
    private final BroadcastReceiver reciveBlockUser = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("BroadcastReceiver");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String blockString = intent.getExtras().getString("block");

                    String userDoBlock = "";
                    String userBlock = "";
                    String blockedFor = "";
                    String name = "";
                    String image = "";
                    String special_number = "";
                    try {
                        JSONObject jsonObject = new JSONObject(blockString);
                        userDoBlock = jsonObject.getString("my_id");
                        userBlock = jsonObject.getString("user_id");
                        blockedFor = jsonObject.getString("blocked_for");
                        name = jsonObject.getString("userDoBlockName");
                        special_number = jsonObject.getString("userDoBlockSpecialNumber");
                        image = jsonObject.getString("userDoBlockImage");

                         chatRoomRepoo.setBlockedState(userDoBlock,blockedFor);



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private final BroadcastReceiver reciveUnBlockUser = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String unBlockString = intent.getExtras().getString("unBlock");

                    String userDoUnBlock = "";
                    String userUnBlock = "";
                    String unBlockedFor = "";

                    try {
                        JSONObject jsonObject = new JSONObject(unBlockString);
                        userDoUnBlock = jsonObject.getString("my_id");
                        userUnBlock = jsonObject.getString("user_id");
                        unBlockedFor = jsonObject.getString("blocked_for");

                        chatRoomRepoo.setBlockedState(userDoUnBlock,unBlockedFor);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        getWindow().setFlags(WindowManager.LayoutParams.,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        Context context = LocaleHelper.setLocale(this,LocaleHelper.getLanguage(this));
        Locale myLocale = new Locale(LocaleHelper.getLanguage(this));
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        getBaseContext().getResources().updateConfiguration(conf,
                getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_dash_bord);

        Log.i("TAG", "onCreate:  " + context.getResources().getString(R.string.chat));
        Log.i("TAG", "onCreate:  " + getResources().getString(R.string.chat));
        connectSocket();
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveNwMessage, new IntentFilter(ON_MESSAGE_RECEIVED));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveTyping, new IntentFilter(TYPING));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveBlockUser, new IntentFilter(ON_BLOCK_USER));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveUnBlockUser, new IntentFilter(ON_UN_BLOCK_USER));

////// send Fcm Token
        myBase = BaseApp.getInstance();
        authRepo = myBase.getAuthRepo();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("kk", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        Log.w("kk", "Fetching FCM registration token sucess", task.getException());

                        String token = task.getResult();
                        authRepo.sendFcmToken(myId,token);
                        Log.d("jjj", token);
                    }
                });

////////////////////////////////////


        ///////////////////
        System.out.println("android.os.Build.MANUFACTURER"+android.os.Build.MANUFACTURER);

/////////////////////
        classSharedPreferences = BaseApp.getInstance().getClassSharedPreferences();
        permissions = new Permissions();
        checkPermission();
        myId = classSharedPreferences.getUser().getUserId();
//        chatRoomRepo= myBase.getChatRoomRepo();
        chatRoomRepoo= myBase.getChatRoomRepoo();

//        chatRoomRepo.callAPI(myId);

        LocalBroadcastManager.getInstance(this).registerReceiver(reciveNewChat, new IntentFilter(NEW_MESSAGE));

         bottomNavigationView = findViewById(R.id.navigationChip);
                if (savedInstanceState == null) {
                    bottomNavigationView.setSelectedItemId(R.id.chat);
            getSupportFragmentManager().beginTransaction().replace(R.id.dashboardContainer, new ChatRoomFragment()).commit();
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {


            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case  R.id.chat:
                        fragment = new ChatRoomFragment();
                        break;

                    case R.id.searchSn:
                        fragment = new SearchFragment();
                        break;
                     case R.id.block:
                        fragment = new SettingsFragment();
                        break;
                    case  R.id .calls:
                        fragment = new CallHistoryFragment();
                }
                if (fragment != null)
                    getSupportFragmentManager().beginTransaction().replace(R.id.dashboardContainer, fragment).commit();
//            }
                return true;
            }
        });
    }




//        navigationBar = findViewById(R.id.navigationChip);
//                LocalBroadcastManager.getInstance(this).registerReceiver(reciveNewChat, new IntentFilter(NEW_MESSAGE));
//
//
//        if (savedInstanceState == null) {
//            navigationBar.setItemSelected(R.id.chat, true);
//            getSupportFragmentManager().beginTransaction().replace(R.id.dashboardContainer, new ChatRoomFragment()).commit();
//        }
//
//        navigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(int i) {
//                switch (i) {
//
//                    case R.id.chat:
//                        fragment = new ChatRoomFragment();
//                        break;
//                    case  R.id.profile:
////                        fragment = new ProfileFragment();
//                        break;
//                    case R.id.searchSn:
//                        fragment = new SearchFragment();
//                        break;
//                     case R.id.block:
//                        fragment = new SettingsFragment();
//                        break;
//                    case  R.id .calls:
////                        fragment = new StoriesFragment();
//                }
//
//                if (fragment != null)
//                    getSupportFragmentManager().beginTransaction().replace(R.id.dashboardContainer, fragment).commit();
//            }
//        });
//
//


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void update(Observable observable, Object o) {

    }

    @Override
    public void onBackPressed() {
        System.out.println("onBackPressed");
        if( bottomNavigationView.getSelectedItemId() != R.id.chat){
        getSupportFragmentManager().beginTransaction().replace(R.id.dashboardContainer, new ChatRoomFragment()).commit();
                            bottomNavigationView.setSelectedItemId(R.id.chat);

        }
        else{
            finish();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveNewChat);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveNwMessage);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveTyping);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveBlockUser);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveUnBlockUser);
    }

    public void checkPermission() {


        if (permissions.isStorageWriteOk(DashBord.this) ) {
            createDirectory("memo");
            createDirectory("memo/send");
            createDirectory("memo/recive");
            createDirectory("memo/send/voiceRecord");
            createDirectory("memo/recive/voiceRecord");
            createDirectory("memo/send/video");
            createDirectory("memo/recive/video");
            System.out.println("permission granted call");
//            chatRoomRepo.callAPI(myId);

        }
        else permissions.requestStorage(DashBord.this);

    }

    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case AllConstants.STORAGE_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    createDirectory("memo");
                    createDirectory("memo/send");
                    createDirectory("memo/recive");
                    createDirectory("memo/send/voiceRecord");
                    createDirectory("memo/recive/voiceRecord");
                    createDirectory("memo/send/video");
                    createDirectory("memo/recive/video");
//                    chatRoomRepo.callAPI(myId);
                }
                else
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    showPermissionDialog(getResources().getString(R.string.write_premission),STORAGE_PERMISSION_CODE);}

                break;
            case AllConstants.CONTACTS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    serverApi.getContactList();
                    checkPermission();

                } else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                        showPermissionDialog(getResources().getString(R.string.contact_permission),Contact_PERMISSION_CODE);

                    }


                }
                break;


        }

        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);
    }

    void createDirectory(String dName) {
//        File yourAppDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + dName);
        File yourAppDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + dName);

        if (!yourAppDir.exists() && !yourAppDir.isDirectory()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    Files.createDirectory(Paths.get(yourAppDir.getAbsolutePath()));
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "problem", Toast.LENGTH_LONG).show();
                }
            } else {
                yourAppDir.mkdir();
            }

        } else {
            Log.i("CreateDir", "App dir already exists");
        }


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case Contact_PERMISSION_CODE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
                    showPermissionDialog(getResources().getString(R.string.contact_permission),Contact_PERMISSION_CODE);

                }
                else{
//
                    checkPermission();
                }
                break;
            case STORAGE_PERMISSION_CODE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

//                    showPermissionDialog(getResources().getString(R.string.write_premission),STORAGE_PERMISSION_CODE);

                }
                else{
                    createDirectory("memo");
                    createDirectory("memo/send");
                    createDirectory("memo/recive");
                    createDirectory("memo/send/voiceRecord");
                    createDirectory("memo/recive/voiceRecord");
                    createDirectory("memo/send/video");
                    createDirectory("memo/recive/video");
//                    chatRoomRepo.callAPI(myId);
                }
                break;

        }
    }
    public void showPermissionDialog(String message,int RequestCode){
        System.out.println(message+"message");
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle(getResources().getString(R.string.permission_necessary));
        alertBuilder.setMessage(getResources().getString(R.string.contact_permission));
        alertBuilder.setMessage(message);

        alertBuilder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, RequestCode);                                     }
        });

        AlertDialog alert = alertBuilder.create();
        alert.show();


    }

}