package com.yawar.memo.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yawar.memo.language.helper.LocaleHelper;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.R;
import com.yawar.memo.fragment.ChatRoomFragment;
import com.yawar.memo.fragment.SearchFragment;
import com.yawar.memo.fragment.SettingsFragment;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.repositry.ChatRoomRepo;
import com.yawar.memo.service.SocketIOService;
import com.yawar.memo.utils.BaseApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
//import com.yawar.memo.fragment.StoriesFragment;

public class DashBord extends AppCompatActivity implements Observer {

// test //

//    private ChipNavigationBar navigationBar;
    BottomNavigationView bottomNavigation;
    private Fragment fragment = null;
    BaseApp myBase;
    ChatRoomRepo chatRoomRepo;
    ClassSharedPreferences classSharedPreferences;
    String myId;
    public static final String NEW_MESSAGE ="new Message" ;
    public static final String ON_MESSAGE_RECEIVED = "ConversationActivity.ON_MESSAGE_RECEIVED";
    public static final String TYPING = "ConversationActivity.ON_TYPING";
    public static final String ON_BLOCK_USER = "ConversationActivity.ON_BLOCK_USER";
    public static final String ON_UN_BLOCK_USER = "ConversationActivity.ON_UN_BLOCK_USER";


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
                    chatRoomRepo.addChatRoom(new ChatRoomModel(
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
                    chatRoomRepo.setLastMessage(text, chatId, myId, anthor_id, type, state, dateTime, senderId);
            }
                else{
                    chatRoomRepo.updateLastMessageState(state,chatId);
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
            chatRoomRepo.setTyping(chat_id, isTyping.equals("true"));
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

                         chatRoomRepo.setBlockedState(userDoBlock,blockedFor);



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

                        chatRoomRepo.setBlockedState(userDoUnBlock,unBlockedFor);

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



        classSharedPreferences = new ClassSharedPreferences(this);
        myId = classSharedPreferences.getUser().getUserId();
        myBase = BaseApp.getInstance();
        chatRoomRepo= myBase.getChatRoomRepo();
//        chatRoomRepo.callAPI(myId);

        LocalBroadcastManager.getInstance(this).registerReceiver(reciveNewChat, new IntentFilter(NEW_MESSAGE));

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationChip);
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
               //     case  R.id.profile:
//                        fragment = new ProfileFragment();
                    //    break;
                    case R.id.searchSn:
                        fragment = new SearchFragment();
                        break;
                     case R.id.block:
                        fragment = new SettingsFragment();
                        break;
               //     case  R.id .calls:
//                        fragment = new StoriesFragment();
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
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveNewChat);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveNwMessage);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveTyping);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveBlockUser);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveUnBlockUser);
    }
}