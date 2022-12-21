package com.yawar.memo.ui.responeCallPage;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yawar.memo.R;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.service.SocketIOService;
import com.yawar.memo.utils.BaseApp;
import com.yawar.memo.utils.CallProperty;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;
import me.mutasem.slidetoanswer.SwipeToAnswerView;
public class CallNotificationActivity extends AppCompatActivity {
    public static final String ON_CLOSE_CALL_FROM_NOTIFICATION = "CallNotificationActivity.ON_RINING_REQUEST";
    SwipeToAnswerView acceptBtn;
    SwipeToAnswerView rejectBtn;
    String callString;
    String id;
    TextView tvName,tvType;
    CircleImageView imageView;
    JSONObject message = null;
    JSONObject data = new JSONObject();
    JSONObject type = new JSONObject();
    CountDownTimer countDownTimer;
    String userName = "User Name ";
    String title = " ";
    String imageUrl;
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
        CallProperty.setStatusBarOrScreenStatus(this);
        showWhenLockedAndTurnScreenOn();
        setContentView(R.layout.activity_call_notification);
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveCloseCallFromNotification, new IntentFilter(ON_CLOSE_CALL_FROM_NOTIFICATION));
        tvName = findViewById(R.id.user_name);
        imageView = findViewById(R.id.image_user_calling);
        tvType = findViewById(R.id.type_call);
        startCounter();
        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id","0");
        callString = bundle.getString("callRequest", "code");
        title = bundle.getString("title","");
        userName = bundle.getString("name","User Name");
        imageUrl = bundle.getString("imageUrl","");
        if (!imageUrl.isEmpty()) {
            Glide.with(imageView).load(AllConstants.imageUrl+imageUrl).apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th)).into(imageView);
        }
        tvName.setText(userName);
        tvType.setText(title);
        acceptBtn = findViewById(R.id.acceptBtn);
        rejectBtn = findViewById(R.id.rejectBtn);
        acceptBtn.setSlideListner(new SwipeToAnswerView.SlideListner() {
            @Override
            public void onSlideCompleted() {
                //TODO : PERFORM WHEN ANSWER
                System.out.println("completed");
                rejectBtn.stopAnimation();
                acceptBtn.setVisibility(View.GONE);
                rejectBtn.setVisibility(View.GONE);
                Intent intent = new Intent(CallNotificationActivity.this, ResponeCallActivity.class);
                intent.putExtra("callRequest",callString);
                intent.putExtra("id",id);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

        });
        rejectBtn.setSlideListner(new SwipeToAnswerView.SlideListner() {
            @Override
            public void onSlideCompleted() {
                //TODO : PERFORM WHEN REFUSE/DECLINE
                acceptBtn.stopAnimation();
                acceptBtn.setVisibility(View.GONE);
                rejectBtn.setVisibility(View.GONE);
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
        countDownTimer.cancel();


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
            System.out.println("the type is"+ type.toString());
            userObject = message.getJSONObject("user");

            message.put("peerId", "null");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // creating a new variable for our request queue
        // on below line we are calling a string
        // request method to post the data to our API
        // in this we are calling a post method.
        StringRequest request = new StringRequest(Request.Method.POST, AllConstants.base_url_final+"reject", new com.android.volley.Response.Listener<String>() {
//        StringRequest request = new StringRequest(Request.Method.POST, AllConstants.base_node_url+"reject", new com.android.volley.Response.Listener<String>() {

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
    private void startCounter() {
//        binding.callStatue.setText(R.string.calling);

        int i=0;
        countDownTimer = new CountDownTimer(10000, 1000) { //40000 milli seconds is total time, 1000 milli seconds is time interval

            public void onTick(long millisUntilFinished) {
                System.out.println(i+1);
            }
            public void onFinish() {
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(CallNotificationActivity.this);
                notificationManager.cancel(-1);
               finish();
            }
        }.start();

    }
}