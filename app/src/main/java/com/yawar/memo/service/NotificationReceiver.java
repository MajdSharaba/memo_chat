package com.yawar.memo.service;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.yawar.memo.call.CallMainActivity;
import com.yawar.memo.call.CallNotificationActivity;
import com.yawar.memo.call.RequestCallActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Bundle bundle = intent.getExtras();
        String callString = bundle.getString("callRequest", "code");
        String id = bundle.getString("id","0");

        JSONObject message = null;
            /////////


            try {
                message = new JSONObject(callString);
                message.put("peerId", "null");
            } catch (JSONException e) {
                e.printStackTrace();
            }

/////////////// for send peer Id
            Intent service = new Intent(context, SocketIOService.class);

            service.putExtra(SocketIOService.EXTRA_SEND_PEER_ID_PARAMTERS, message.toString());
            service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_SEND_PEER_ID);
            context.startService(service);

//////////////for cancel full Screen intent
        Intent closeIntent = new Intent(CallNotificationActivity.ON_CLOSE_CALL_FROM_NOTIFICATION);

        LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(closeIntent);



        notificationManager.cancel(Integer.parseInt(id));


    }
}
