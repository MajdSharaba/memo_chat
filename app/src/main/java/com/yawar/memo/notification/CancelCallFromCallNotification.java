package com.yawar.memo.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yawar.memo.call.CallNotificationActivity;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.utils.BaseApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CancelCallFromCallNotification extends BroadcastReceiver {
    JSONObject message = null;
    String callId;
    JSONObject data = new JSONObject();
    JSONObject type = new JSONObject();
    JSONObject userObject = new JSONObject();

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Bundle bundle = intent.getExtras();
        String callString = bundle.getString("callRequest", "code");
        String id = bundle.getString("id","0");
        reject(callString);


//////////////for cancel full Screen intent
        Intent closeIntent = new Intent(CallNotificationActivity.ON_CLOSE_CALL_FROM_NOTIFICATION);

        LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(closeIntent);



        notificationManager.cancel(-1);


    }
    public void reject(String callParamters) {
        BaseApp myBase;
        myBase = BaseApp.getInstance();

        /////////


        try {
            message = new JSONObject(callParamters);
//            data = message.getJSONObject("data");
            callId = message.getString("call_id");
            type = message.getJSONObject("type");
            userObject = message.getJSONObject("user");

            message.put("peerId", "null");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // creating a new variable for our request queue
        // on below line we are calling a string
        // request method to post the data to our API
        // in this we are calling a post method.
//        StringRequest request = new StringRequest(Request.Method.POST, AllConstants.base_url_final+"reject", new com.android.volley.Response.Listener<String>() {
        StringRequest request = new StringRequest(Request.Method.POST, AllConstants.base_node_url+"reject", new com.android.volley.Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // below line we are creating a map for
                // storing our values in key and value pair.
                Map<String, String> params = new HashMap<String, String>();

                // on below line we are passing our key
                // and value pair to our parameters.
                try {


                    params.put("rcv_id", message.getString("rcv_id"));

                    params.put("typeCall", "call");

                    params.put("user", userObject.toString());
                    params.put("type", type.toString());
                    params.put("message", "");
                    params.put("call_id", callId);


                    params.put("peerId", "null");
                    params.put("snd_id",message.getString("snd_id") );
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // at last we are
                // returning our params.
                return params;
            }
        };
        // below line is to make
        // a json object request.
        myBase.addToRequestQueue(request);
    }
}
