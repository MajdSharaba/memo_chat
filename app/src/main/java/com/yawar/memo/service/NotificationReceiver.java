package com.yawar.memo.service;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Bundle bundle = intent.getExtras();
        String callString = bundle.getString("callRequest", "code");
        Log.d("Here", "I am here+"+callString);

        JSONObject message = null;
            /////////


            try {
                message = new JSONObject(callString);
                message.put("peerId", "null");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println(message.toString() + "peeeeeeeeeeeId object");


            Intent service = new Intent(context, SocketIOService.class);

            service.putExtra(SocketIOService.EXTRA_SEND_PEER_ID_PARAMTERS, message.toString());
            service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_SEND_PEER_ID);
        context.startService(service);




        notificationManager.cancel(0);


    }
}
