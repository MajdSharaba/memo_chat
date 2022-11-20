package com.yawar.memo.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationManagerCompat;

import com.yawar.memo.ui.responeCallPage.ResponeCallActivity;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.service.SocketIOService;
import com.yawar.memo.utils.BaseApp;

import org.json.JSONException;
import org.json.JSONObject;

public class CancelCallAndStartNewCall extends BroadcastReceiver {
//    Context context;
//    public CancelCallAndStartNewCall(Context context) {
//        this.context=context;
//    }

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("cancel Call and start new call");
        Bundle bundle = intent.getExtras();
        String id = bundle.getString("id","0");
        ////for close ongoing notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(AllConstants.onGoingCallChannelId);
        //// for close current call
//        Intent closeCallActivity = new Intent(ResponeCallActivity.ON_CLOSE_CALL_FROM_NOTIFICATION_CALL_ACTIVITY);
//        LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(closeCallActivity);
//        /////for close current call in anthor user
//        closeCall(context,id);
        ////////////
        Intent intent1 = new Intent(BaseApp.getInstance(), ResponeCallActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);

        BaseApp.getInstance().startActivity(intent1);
    }
    private void closeCall(Context context,String id) {
        Intent service = new Intent(context, SocketIOService.class);
        JSONObject data = new JSONObject();
        try {
//            data.put("close_call", true);
            data.put("id",id );//            data.put("snd_id", classSharedPreferences.getUser().getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("close Call");
        service.putExtra(SocketIOService.EXTRA_STOP_CALL_PARAMTERS, data.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_STOP_CALLING);
        context.startService(service);

    }

    }

