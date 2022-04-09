package com.yawar.memo.utils;


import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.text.TextUtils;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.PhoneAuthProvider;
import com.yawar.memo.observe.ChatRoomObserve;
import com.yawar.memo.observe.ContactNumberObserve;
import com.yawar.memo.observe.FireBaseTokenObserve;
import com.yawar.memo.observe.StoriesObserve;
import com.yawar.memo.service.SocketIOService;

public class BaseApp extends Application implements LifecycleObserver {

    ChatRoomObserve observeClass;
    FireBaseTokenObserve fireBaseTokenObserve;
    StoriesObserve storiesObserve;
    ContactNumberObserve contactNumberObserve;
    public static final String TAG = "VolleyPatterns";
    private RequestQueue mRequestQueue;
    private static BaseApp sInstance;


    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

     public  String isActivityVisible(){
         return ProcessLifecycleOwner.get().getLifecycle().getCurrentState().name();
     }
    @Override
    public void onTerminate() {
        System.out.println("on Tirminal");
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        System.out.println("on low memoryyyyyyyyyyyyyy");
        super.onLowMemory();
    }

    public static synchronized BaseApp getInstance() {
        if(sInstance==null){
            sInstance = new BaseApp();
        }

        return sInstance;
        }

//        observeClass = new ChatRoomObserve();
//        fireBaseTokenObserve = new FireBaseTokenObserve();
//        storiesObserve = new StoriesObserve();
//        contactNumberObserve = new ContactNumberObserve();



    public ChatRoomObserve getObserver() {
       if(observeClass== null){
           observeClass = new ChatRoomObserve();
       }

        return observeClass;
    }
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

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
    @SuppressLint("CheckResult")
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onMoveToForeground() {
//        System.out.println("on move to Forgroundgroundddddddddd");
//        Intent service = new Intent(this, SocketIOService.class);
//        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_JOIN);
//        this.startService(service);
//            Intent service = new Intent(this, SocketIOService.class);
//            service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_JOIN);
//            this.startService(service);

//        notifyAll();


    }

    @SuppressLint("CheckResult")
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onMoveToBackground() {
        System.out.println("on move to Backgroundddddddddd");
//            Intent service = new Intent(this, SocketIOService.class);
//            service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_DISCONNECT);
//            this.startService(service);



    }


}
