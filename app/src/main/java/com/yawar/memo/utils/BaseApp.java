package com.yawar.memo.utils;


import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.PhoneAuthProvider;
import com.yawar.memo.R;
import com.yawar.memo.observe.ContactNumberObserve;
import com.yawar.memo.observe.FireBaseTokenObserve;
import com.yawar.memo.observe.StoriesObserve;
import com.yawar.memo.repositry.AuthRepo;
import com.yawar.memo.repositry.BlockUserRepo;
import com.yawar.memo.repositry.ChatMessageRepo;
import com.yawar.memo.repositry.ChatRoomRepo;
import com.yawar.memo.repositry.RequestCallRepo;
import com.yawar.memo.repositry.UserInformationRepo;
import com.yawar.memo.service.SocketIOService;
import com.yawar.memo.sessionManager.ClassSharedPreferences;

public class BaseApp extends Application implements LifecycleObserver {

//    ChatRoomObserve observeClass;
    FireBaseTokenObserve fireBaseTokenObserve;
    StoriesObserve storiesObserve;
    ContactNumberObserve contactNumberObserve;
    public static final String TAG = "VolleyPatterns";
    private RequestQueue mRequestQueue;
    private static BaseApp sInstance;
    ChatRoomRepo chatRoomRepo;
    RequestCallRepo requestCallRepo;
    BlockUserRepo blockUserRepo;
    AuthRepo authRepo;
    ChatMessageRepo chatMessageRepo;
    UserInformationRepo userInformationRepo;
     String[] darkModeValues ;
     ClassSharedPreferences classSharedPreferences;
     Handler handler = new Handler();
    private Runnable myRunnable ;



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
            Log.d(TAG, "getChatRoomRepo().callAPI(classSharedPreferences.getUser()");
            getChatRoomRepo().callAPI(classSharedPreferences.getUser().getUserId());

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

//        observeClass = new ChatRoomObserve();
//        fireBaseTokenObserve = new FireBaseTokenObserve();
//        storiesObserve = new StoriesObserve();
//        contactNumberObserve = new ContactNumberObserve();



//    public ChatRoomObserve getObserver() {
//       if(observeClass== null){
//           observeClass = new ChatRoomObserve();
//       }
//
//        return observeClass;
//    }
    public FireBaseTokenObserve getForceResendingToken() {
        if(fireBaseTokenObserve== null){
            fireBaseTokenObserve = new FireBaseTokenObserve();
        }
        return fireBaseTokenObserve;
    }
    public StoriesObserve getStoriesObserve() {
        if(storiesObserve== null){
            storiesObserve = new StoriesObserve();
        }
        return storiesObserve;
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
    public ChatRoomRepo getChatRoomRepo() {
        if(chatRoomRepo== null){
            chatRoomRepo = new ChatRoomRepo(this);
//            if(getClassSharedPreferences().getUser()!=null)
//            chatRoomRepo.callAPI(classSharedPreferences.getUser().getUserId());

        }
        return chatRoomRepo;
    }
    public RequestCallRepo getRequestCallRepo() {
        if(requestCallRepo== null){
            requestCallRepo = new RequestCallRepo(this);
        }
        return requestCallRepo;
    }
    public UserInformationRepo getUserInformationRepo() {
        if(userInformationRepo== null){
            userInformationRepo = new UserInformationRepo(this);
        }
        return userInformationRepo;
    }
    public BlockUserRepo getBlockUserRepo() {
        if(blockUserRepo== null){
            blockUserRepo = new BlockUserRepo(this);
        }
        return blockUserRepo;
    }
    public AuthRepo getAuthRepo() {
        if(authRepo== null){
            authRepo = new AuthRepo(this);
        }
        return authRepo;
    }
    public ChatMessageRepo getChatMessageRepo() {
        if(chatMessageRepo== null){
            chatMessageRepo = new ChatMessageRepo(this);
        }
        return chatMessageRepo;
    }

    public ClassSharedPreferences getClassSharedPreferences() {
        if(classSharedPreferences== null){
            classSharedPreferences = new ClassSharedPreferences(this);
        }
        return classSharedPreferences;
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
