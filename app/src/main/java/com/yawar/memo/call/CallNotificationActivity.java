package com.yawar.memo.call;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yawar.memo.R;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.service.SocketIOService;
import com.yawar.memo.utils.BaseApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CallNotificationActivity extends AppCompatActivity {
    public static final String ON_CLOSE_CALL_FROM_NOTIFICATION = "CallNotificationActivity.ON_RINING_REQUEST";

    ImageView acceptBtn;
    ImageView rejectBtn;
    String callString;
    String id;
    JSONObject message = null;
    JSONObject data = new JSONObject();
    JSONObject type = new JSONObject();
    JSONObject userObject = new JSONObject();
    private final BroadcastReceiver reciveCloseCallFromNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("CallNotificatio", "run: close ");
                 finish();
                    ///////////


                }
            });
        }
    };
    private void sendPeerId(String object,String peer_id) {
        System.out.println(object + "this is object ");
        JSONObject message = null;
        /////////


        try {
            message = new JSONObject(object);
            message.put("peerId", peer_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(message.toString() + "peeeeeeeeeeeId object");


        Intent service = new Intent(this, SocketIOService.class);

        service.putExtra(SocketIOService.EXTRA_SEND_PEER_ID_PARAMTERS, message.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_SEND_PEER_ID);
        startService(service);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showWhenLockedAndTurnScreenOn();
        setContentView(R.layout.activity_call_notification);
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveCloseCallFromNotification, new IntentFilter(ON_CLOSE_CALL_FROM_NOTIFICATION));

        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id","0");
        callString = bundle.getString("callRequest", "code");
        acceptBtn = findViewById(R.id.acceptBtn);
        rejectBtn = findViewById(R.id.rejectBtn);
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                System.out.println("acceptBtn" + callString)
                Intent intent = new Intent(CallNotificationActivity.this, ResponeCallActivity.class);
                intent.putExtra("callRequest",callString);
                intent.putExtra("id",id);

                startActivity(intent);
                finish();


            }
        });


        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                sendPeerId(callString,"null");
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(CallNotificationActivity.this);
//                notificationManager.cancel(Integer.parseInt(id)+10000);
                notificationManager.cancel(-1);

                reject(callString);

                finish();

            }
        });
    }
    private void showWhenLockedAndTurnScreenOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//            keyguardManager.requestDismissKeyguard(this, null);
        } else {
//            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
//            window.addFlags(
//                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
//            )
        }
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveCloseCallFromNotification);

        super.onDestroy();
    }
    public void reject(String callParamters) {
        BaseApp myBase;
        myBase = BaseApp.getInstance();

        /////////


        try {
            message = new JSONObject(callParamters);
//            data = message.getJSONObject("data");
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