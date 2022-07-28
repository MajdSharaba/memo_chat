package com.yawar.memo.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.yawar.memo.call.ResponeCallActivity;
import com.yawar.memo.call.CallNotificationActivity;
import com.yawar.memo.service.SocketIOService;

import org.json.JSONException;
import org.json.JSONObject;

public class CancelCallFromCallOngoingNotification  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("clickeeesddddddddd");
        Bundle bundle = intent.getExtras();
        String id = bundle.getString("id","0");
        String callRequest = bundle.getString("callRequest");
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(Integer.parseInt("0"));
        Intent closeCallActivity = new Intent(ResponeCallActivity.ON_CLOSE_CALL_FROM_NOTIFICATION_CALL_ACTIVITY);

        LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(closeCallActivity);
        Intent closeRequestCallActivity = new Intent(CallNotificationActivity.ON_CLOSE_CALL_FROM_NOTIFICATION);

        LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(closeRequestCallActivity);
        closeCall(context,id);
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
