package com.yawar.memo.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.R;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.modelView.IntroActModelView;
import com.yawar.memo.repositry.BlockUserRepo;
import com.yawar.memo.repositry.ChatRoomRepo;
import com.yawar.memo.permissions.Permissions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import com.yawar.memo.utils.BaseApp;
//import com.yawar.memo.videocalltest.ConnService;

public class IntroActivity extends AppCompatActivity  {
    ClassSharedPreferences classSharedPreferences;
    ProgressDialog progressDialog;
    BaseApp myBase;
    String myId;
    IntroActModelView introActModelView;
    ChatRoomRepo chatRoomRepo;
    BlockUserRepo blockUserRepo;
    ProgressBar progressBar;


    boolean isResponeSucces = false;
    private static final int STORAGE_PERMISSION_CODE = 2000;
    private static final int Contact_PERMISSION_CODE = 1000;


    private Permissions permissions;
//    ServerApi serverApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        System.out.println( android.os.Build.MANUFACTURER+"String deviceMan = android.os.Build.MANUFACTURER;\n");
        progressBar = findViewById(R.id.progress_circular);
//        if(android.os.Build.MANUFACTURER.equals("xhaomi")){
//        if(android.os.Build.MANUFACTURER.equals("OPPO")){
//
//            showXhaomiDialog();
//        }
//        askCallPermission();

//        goToNotificationSettings(this);


//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
//            System.out.println("notification is not granted");
//            goToNotificationSettings(this);
//        }
//        else {
//            System.out.println("notification is  granted");
//
//        }
//        goToNotificationSettings(this);
//        askCallPermission();

        classSharedPreferences = BaseApp.getInstance().getClassSharedPreferences();
        myId = classSharedPreferences.getUser().getUserId();
       myBase = BaseApp.getInstance();

        introActModelView = new ViewModelProvider(this).get(IntroActModelView.class);
//            FirebaseMessaging.getInstance().getToken()
//                    .addOnCompleteListener(new OnCompleteListener<String>() {
//                        @Override
//                        public void onComplete(@NonNull Task<String> task) {
//                            if (!task.isSuccessful()) {
//                                Log.w("kk", "Fetching FCM registration token failed", task.getException());
//                                return;
//                            }
//
//                            String token = task.getResult();
//                            introActModelView.sendFcmToken(myId,token);
//                            classSharedPreferences.setFcmToken(token);
//                            Log.d("jjj", token);
//                        }
//                    });
//        }

        introActModelView.loadData().observe(this, new androidx.lifecycle.Observer<ArrayList<ChatRoomModel>>() {
            @Override
            public void onChanged(ArrayList<ChatRoomModel> chatRoomModels) {
                if(chatRoomModels!=null){
                    introActModelView.loadData().removeObserver(this);
                    Intent intent = new Intent(IntroActivity.this, DashBord.class);

                    startActivity(intent);
                    finish();

                }

            }
        });

        introActModelView.getLoading().observe(this, new androidx.lifecycle.Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean!=null) {
                    if (aBoolean) {
                        System.out.println("boleannnn");
                        progressBar.setVisibility(View.VISIBLE);

                    } else {
                        progressBar.setVisibility(View.GONE);


                    }
                }
            }
        });

        introActModelView.getErrorMessage().observe(this, new androidx.lifecycle.Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean!=null) {
                    if (aBoolean) {
//                        Toast.makeText(IntroActivity.this, R.string.internet_message, Toast.LENGTH_LONG).show();
                        introActModelView.setErrorMessage(null);
                    }
                }
            }
        });



    }


    @Override
    protected void onDestroy() {


    super.onDestroy();
    }
    public static void goToNotificationSettings(Context context) {
//        Intent intent = new Intent();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).
//            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());;
//        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
//            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
//            intent.putExtra("app_package", context.getPackageName());
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
//            intent.putExtra("app_package", context.getPackageName());
//            intent.putExtra("app_uid", context.getApplicationInfo().uid);
//        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
//            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//            intent.addCategory(Intent.CATEGORY_DEFAULT);
//            intent.setData(Uri.parse("package:" + context.getPackageName()));
//        } else {
//            return;
//        }
//        context.startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + context.getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    public void askCallPermission() {
//
//
//        TelecomManager telecomManager;
//        TelephonyManager telephonyManager;
//        PhoneAccountHandle phoneAccountHandle;
//
//        telecomManager = (TelecomManager) this.getSystemService(Context.TELECOM_SERVICE);
//        telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
//
//        ComponentName componentName = new ComponentName(this, ConnService.class);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            phoneAccountHandle = new PhoneAccountHandle(componentName, "com.darkhorse.videocalltest");
//
//            PhoneAccount phoneAccount = PhoneAccount.builder(phoneAccountHandle, "com.darkhorse.videocalltest").setCapabilities(
//                    PhoneAccount.CAPABILITY_SELF_MANAGED
//
//            ).setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER).build();
//            try {
//                telecomManager.registerPhoneAccount(phoneAccount);
//                Intent intent = new Intent();
//                intent.setComponent(new ComponentName(
//                        "com.android.server.telecom",
//                        "com.android.server.telecom.settings.EnableAccountPreferenceActivity"
//                ));
//                startActivity(intent);
//
//            } catch (Exception e) {
//                Log.e("main activity register", e.toString());
//            }
//        }
//    }

    public void showXhaomiDialog(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle(getResources().getString(R.string.alert));
        alertBuilder.setMessage(getResources().getString(R.string.xhaomi_message));

        alertBuilder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onClick(DialogInterface dialog, int which) {
                openAppPermission();

                                                  }
        });
        alertBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();


            }
        });
        AlertDialog alert = alertBuilder.create();
        alert.show();


    }
  void openAppPermission(){
      Intent intent = new Intent();
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
          intent.putExtra(Settings.EXTRA_APP_PACKAGE, this.getPackageName());
      } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
          intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
          intent.putExtra("app_package", this.getPackageName());
          intent.putExtra("app_uid", this.getApplicationInfo().uid);
      } else {
          intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//          intent.addCategory(Intent.CATEGORY_DEFAULT);
          intent.setData(Uri.parse("package:" + this.getPackageName()));
      }
      this.startActivity(intent);

    }
}
