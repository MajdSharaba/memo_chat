package com.yawar.memo.Api;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.yawar.memo.R;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.model.ContactModel;
import com.yawar.memo.model.SendContactNumberResponse;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.repositry.AuthRepo;
import com.yawar.memo.repositry.BlockUserRepo;
import com.yawar.memo.repositry.ChatRoomRepoo;
import com.yawar.memo.service.SocketIOService;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.utils.BaseApp;
import com.yawar.memo.views.DashBord;
import com.yawar.memo.views.IntroActivity;
import com.yawar.memo.views.RegisterActivity;
import com.yawar.memo.views.SplashScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerApi {
    Activity context;
    BaseApp myBase =BaseApp.getInstance() ;
    ClassSharedPreferences classSharedPreferences = myBase.getClassSharedPreferences();
    public ServerApi(Activity context) {
        this.context = context;
    }
    public void sendNotification(String message, String type,String fcmToken, String chat_id, String blockedFor, String messageId, String dateTime) {
        try {
            JSONObject data = new JSONObject();
            JSONObject android = new JSONObject();
            android.put("priority", "high");
            data.put("title", classSharedPreferences.getUser().getUserName()+" "+ classSharedPreferences.getUser().getLastName());
            data.put("body", message);
            data.put("image", classSharedPreferences.getUser().getImage());
            data.put("chat_id", chat_id);
            data.put("sender_id", classSharedPreferences.getUser().getUserId());
            data.put("fcm_token", classSharedPreferences.getFcmToken());
            data.put("special", classSharedPreferences.getUser().getSecretNumber());
            data.put("message_id", messageId);
            data.put("dateTime", dateTime);
            data.put("blockedFor", blockedFor);
            data.put("type", type);
            JSONObject notification_data = new JSONObject();
            notification_data.put("data", data);
            notification_data.put("to", fcmToken);
            notification_data.put("content_available", true);
            notification_data.put("android", android);
            JsonObjectRequest request = new JsonObjectRequest(AllConstants.fcm_send_notification_url, notification_data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println("responeeeeeeeeeeeeeeeeeeeeeeeeee" + message + fcmToken);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", AllConstants.api_key_fcm_token_header_value);
                    return headers;
                }
            };
            myBase.addToRequestQueue(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
