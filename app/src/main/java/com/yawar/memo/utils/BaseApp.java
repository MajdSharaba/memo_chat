package com.yawar.memo.utils;


import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.rxjava2.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava2.RxDataStore;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.yawar.memo.R;
import com.yawar.memo.observe.ContactNumberObserve;
import com.yawar.memo.observe.FireBaseTokenObserve;
import com.yawar.memo.repositry.AuthRepo;
import com.yawar.memo.repositry.BlockUserRepo;
import com.yawar.memo.repositry.ChatMessageRepoo;
import com.yawar.memo.repositry.ChatRoomRepoo;
//import com.yawar.memo.repositry.RequestCallRepo;
import com.yawar.memo.repositry.UserInformationRepo;
import com.yawar.memo.service.SocketIOService;
import com.yawar.memo.sessionManager.ClassSharedPreferences;

public class BaseApp extends Application implements LifecycleObserver {

    FireBaseTokenObserve fireBaseTokenObserve;
    ContactNumberObserve contactNumberObserve;
    public static final String TAG = "VolleyPatterns";
    private RequestQueue mRequestQueue;
    private static BaseApp sInstance;
//    ChatRoomRepo chatRoomRepo;
//    RequestCallRepo requestCallRepo;
    BlockUserRepo blockUserRepo;
    AuthRepo authRepo;
    ChatMessageRepoo chatMessageRepoo;

    UserInformationRepo userInformationRepo;
     String[] darkModeValues ;
     ClassSharedPreferences classSharedPreferences;
     Handler handler = new Handler();
    private Runnable myRunnable ;
    RxDataStore<Preferences> dataStore;
    ChatRoomRepoo chatRoomRepoo;



    String peerId = null;




    @Override
    public void onCreate() {
        super.onCreate();
        setMode();




        sInstance = this;

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        System.out.println("getInstance()");
        if(getClassSharedPreferences().getUser()!=null) {
//            System.out.println("getChatRoomRepo().callAPI(classSharedPreferences.getUser().getUserId());");
//            Log.d(TAG, "getChatRoomRepo().callAPI(classSharedPreferences.getUser()");
//            getChatRoomRepo().callAPI(classSharedPreferences.getUser().getUserId());
            getChatRoomRepoo().loadChatRoom(classSharedPreferences.getUser().getUserId());


        }




    }


     public  String isActivityVisible(){
         return ProcessLifecycleOwner.get().getLifecycle().getCurrentState().name();
     }
    @Override
    public void onTerminate() {
        super.onTerminate();
        System.out.println("on Tirminal");

    }

    @Override
    public void onLowMemory() {
        System.out.println("on low memoryyyyyyyyyyyyyy");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        System.out.println("onTrimMemory BaseApp");

        super.onTrimMemory(level);
    }

    public static synchronized BaseApp getInstance() {
        if(sInstance==null){
            Log.d(TAG, "get Instance");
            sInstance = new BaseApp();
        }

        return sInstance;
        }

    public FireBaseTokenObserve getForceResendingToken() {
        if(fireBaseTokenObserve== null){
            fireBaseTokenObserve = new FireBaseTokenObserve();
        }
        return fireBaseTokenObserve;
    }

    public ContactNumberObserve getContactNumberObserve() {
        if(contactNumberObserve== null){
            contactNumberObserve = new ContactNumberObserve();
        }
        return contactNumberObserve;
    }
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }
//    public ChatRoomRepo getChatRoomRepo() {
//        if(chatRoomRepo== null){
//            chatRoomRepo = new ChatRoomRepo(this);
//        }
//        return chatRoomRepo;
//    }
    public ChatRoomRepoo getChatRoomRepoo() {
        if(chatRoomRepoo== null){
            chatRoomRepoo = new ChatRoomRepoo();
        }
        return chatRoomRepoo;
    }
//    public RequestCallRepo getRequestCallRepo() {
//        if(requestCallRepo== null){
//            requestCallRepo = new RequestCallRepo(this);
//        }
//        return requestCallRepo;
//    }
    public UserInformationRepo getUserInformationRepo() {
        if(userInformationRepo== null){
            userInformationRepo = new UserInformationRepo();
        }
        return userInformationRepo;
    }
    public BlockUserRepo getBlockUserRepo() {
        if(blockUserRepo== null){
            blockUserRepo = new BlockUserRepo();
        }
        return blockUserRepo;
    }
    public AuthRepo getAuthRepo() {
        if(authRepo== null){
            authRepo = new AuthRepo();
        }
        return authRepo;
    }

    public ChatMessageRepoo getChatMessageRepoo() {
        if(chatMessageRepoo== null){
            chatMessageRepoo = new ChatMessageRepoo();
        }
        return chatMessageRepoo;
    }

    public ClassSharedPreferences getClassSharedPreferences() {
        if(classSharedPreferences== null){
            classSharedPreferences = new ClassSharedPreferences(this);
        }
        return classSharedPreferences;
    }
    public RxDataStore<Preferences> getDataStore() {
        if(dataStore== null){
            dataStore =
                    new RxPreferenceDataStoreBuilder(this, /*name=*/ "settings").build();
        }
        return dataStore;
    }
    public void setPeerId(String peer_id) {
        if(peerId== null){
            this.peerId = peer_id;
        }
    }
    public String getPeerId() {
    return  peerId;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }
    public  void setMode(){
        darkModeValues = getResources().getStringArray(R.array.dark_mode_values);
        String pref = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.dark_mode), getString(R.string.dark_mode_def_value));
        // Comparing to see which preference is selected and applying those theme settings
        if (pref.equals(darkModeValues[0])){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);}
        else if (pref.equals(darkModeValues[1])){

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);}

    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
    @SuppressLint("CheckResult")
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onMoveToForeground() {


        if(classSharedPreferences.getUser()!=null){
//            if(chatR) {
//                getChatRoomRepo().callAPI(classSharedPreferences.getUser().getUserId());



        Intent service = new Intent(this, SocketIOService.class);
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_JOIN);
        this.startService(service);}


//        notifyAll();


    }

    @SuppressLint("CheckResult")
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onMoveToBackground() {
        if(classSharedPreferences.getUser()!=null) {


            Intent service = new Intent(this, SocketIOService.class);
            service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_DISCONNECT);
            startService(service);
//            myRunnable = new Runnable() {
//                @Override
//                public void run() {
//                    System.out.println("on Move to bacground");
//                    service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_DISCONNECT);
//                    startService(service);
//                }
//            };
//            handler.postDelayed(myRunnable, 30000);
        }




    }
    @SuppressLint("CheckResult")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        System.out.println("on ON_DESTROY my App");
//            Intent service = new Intent(this, SocketIOService.class);
//            service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_DISCONNECT);
//            this.startService(service);



    }


}
