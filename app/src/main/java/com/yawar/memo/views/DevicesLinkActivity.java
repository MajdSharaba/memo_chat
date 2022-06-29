package com.yawar.memo.views;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.R;
import com.yawar.memo.adapter.DeviceLinkAdapter;
import com.yawar.memo.model.DeviceLinkModel;
import com.yawar.memo.service.SocketIOService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DevicesLinkActivity extends AppCompatActivity {
    Button btnScan;
    ArrayList<DeviceLinkModel> deviceLinkModels = new ArrayList<DeviceLinkModel>();
    DeviceLinkAdapter mainAdapter;
    RecyclerView recyclerView;
    String resultQr;
    ClassSharedPreferences classSharedPreferences;
    String myId;
    public static final String SCAN_QR ="scan qr" ;
    public static final String GET_QR ="get qr" ;


    private void checkQr() {
        Intent service = new Intent(this, SocketIOService.class);
        JSONObject object = new JSONObject();
        try {
            object.put("key",resultQr);
            object.put("id",myId);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        service.putExtra(SocketIOService.EXTRA_CHECK_QR_PARAMTERS, object.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_CHECK_QR);
        startService(service);
    }
    private void getIDQr() {
        Intent service = new Intent(this, SocketIOService.class);
        JSONObject object = new JSONObject();
        try {
            object.put("key",resultQr);
            object.put("id",myId);
            object.put("checkQr",true);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        service.putExtra(SocketIOService.EXTRA_GET_QR_PARAMTERS, object.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_GET_QR);
        startService(service);
    }
    private final BroadcastReceiver recivecheckQr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String objectString = intent.getExtras().getString("scan qr");
            System.out.println(objectString+"new chattttttttttttttttttttttttttttttt");

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(objectString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
              boolean check=false;
            try {
                 check = jsonObject.getBoolean("checkQr");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println("checkkkkkkkk");
            if(check){
                System.out.println("check true");
                getIDQr();
            }
        }

//
//
//
//
//
    };
    private final BroadcastReceiver reciveGetQr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String objectString = intent.getExtras().getString("get qr");
            System.out.println(objectString+"new chattttttttttttttttttttttttttttttt");

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(objectString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String device = "undefine";


            try {
                device = jsonObject.getString("device");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            deviceLinkModels.add(new DeviceLinkModel(device,"", DateFormat.getDateTimeInstance().format(new Date())));
            mainAdapter.notifyDataSetChanged();



        }

//
//
//
//
//
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_devices_link);
        LocalBroadcastManager.getInstance(this).registerReceiver(recivecheckQr, new IntentFilter(SCAN_QR));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveGetQr, new IntentFilter(GET_QR));

        classSharedPreferences = new ClassSharedPreferences(this);
        myId=classSharedPreferences.getUser().getUserId();
        recyclerView = findViewById(R.id.recycler_view);
//        deviceLinkModels.add(new DeviceLinkModel("chrome",""));
        recyclerView.setLayoutManager(new LinearLayoutManager(DevicesLinkActivity.this));
        mainAdapter = new DeviceLinkAdapter(DevicesLinkActivity.this,deviceLinkModels);
        recyclerView.setAdapter(mainAdapter);
        btnScan = findViewById(R.id.btn_link);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barcodeLauncher.launch(new ScanOptions());

            }
        });
    }
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
//                    Toast.makeText(DevicesLinkActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
                } else {
////                    deviceLinkModels.add(new DeviceLinkModel(result.getContents(),""));
//                    mainAdapter.notifyDataSetChanged();
                    resultQr= result.getContents();
                    checkQr();
//                    Toast.makeText(DevicesLinkActivity.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(recivecheckQr);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveGetQr);


    }
}
