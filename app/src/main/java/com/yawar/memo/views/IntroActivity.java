package com.yawar.memo.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.Settings;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import com.yawar.memo.utils.Globale;
import com.yawar.memo.permissions.Permissions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import com.yawar.memo.utils.BaseApp;
import com.yawar.memo.videocalltest.ConnService;

public class IntroActivity extends AppCompatActivity implements Observer {
    ClassSharedPreferences classSharedPreferences;
    Globale globale;
    ProgressDialog progressDialog;
    BaseApp myBase;
    String myId;
    IntroActModelView introActModelView;
    ChatRoomRepo chatRoomRepo;
    BlockUserRepo blockUserRepo;


    boolean isResponeSucces = false;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private Permissions permissions;
//    ServerApi serverApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        System.out.println( android.os.Build.MANUFACTURER+"String deviceMan = android.os.Build.MANUFACTURER;\n");
//        if(android.os.Build.MANUFACTURER.equals("xhaomi")){
//        openAppPermission();}
//        askCallPermission();

//        goToNotificationSettings(this);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("notification is not granted");
            goToNotificationSettings(this);
        }
        else {
            System.out.println("notification is  granted");

        }
//        goToNotificationSettings(this);
//        askCallPermission();

        classSharedPreferences = new ClassSharedPreferences(this);
        myId = classSharedPreferences.getUser().getUserId();
//        serverApi = new ServerApi(this);
       myBase = BaseApp.getInstance();
//        myBase.getObserver().addObserver(this);
        chatRoomRepo=myBase.getChatRoomRepo();
        blockUserRepo = myBase.getBlockUserRepo();
        introActModelView = new ViewModelProvider(this).get(IntroActModelView.class);
        permissions = new Permissions();
        System.out.println(classSharedPreferences.getFcmToken() + "classSharedPreferences.getFcmToken()");

//        if (classSharedPreferences.getFcmToken().equals("empty")) {
            System.out.println(classSharedPreferences.getFcmToken() + "classSharedPreferences.getFcmToken()");
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w("kk", "Fetching FCM registration token failed", task.getException());
                                return;
                            }

                            // Get new FCM registration token
                            String token = task.getResult();
                            sendToken(token);

                            // Log and toast
                            Log.d("jjj", token);
//                            Toast.makeText(IntroActivity.this, token, Toast.LENGTH_SHORT).show();
                        }
                    });
//        }

        introActModelView.loadData().observe(this, new androidx.lifecycle.Observer<ArrayList<ChatRoomModel>>() {
            @Override
            public void onChanged(ArrayList<ChatRoomModel> chatRoomModels) {
                if(chatRoomModels!=null){
                    introActModelView.loadData().removeObserver(this);
                    blockUserRepo.getUserBlock(myId);

                }

            }
        });
        introActModelView.getUserBlock().observe(this, new androidx.lifecycle.Observer<ArrayList<UserModel>>() {
            @Override
            public void onChanged(ArrayList<UserModel> userModels) {
                if(userModels!=null){
                    introActModelView.getUserBlock().removeObserver(this);
               Intent intent = new Intent(IntroActivity.this, DashBord.class);

                    startActivity(intent);
                    finish();

                }

            }
        });



        checkPermission();



    }

//    public void GetData() {
//        List<ChatRoomModel> postList = new ArrayList<>();
//
//
//        UserModel userModel = classSharedPreferences.getUser();
//        String myId = userModel.getUserId();
//
//        System.out.println(userModel.getUserId());
////        progressDialog = new ProgressDialog(this);
////        progressDialog.setMessage("Loading...");
////        progressDialog.show();
////        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        StringRequest request = new StringRequest(Request.Method.GET, AllConstants.base_url + "APIS/mychat.php?user_id=" + myId, new Response.Listener<String>() {
//
//
//            @Override
//            public void onResponse(String response) {
////                progressDialog.dismiss();
//                System.out.println(response + "responeeeeeeeeeeeeeeeee");
//
//                try {
//                    JSONObject respObj = new JSONObject(response);
//                    System.out.println(respObj + "");
//                    JSONArray jsonArray = (JSONArray) respObj.get("data");
////                    JSONArray jsonArray = new JSONArray(respObj.getJSONArray("data"));
//                    System.out.println(jsonArray);
//                    postList.clear();
//
//                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
//                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//                        //System.out.println(jsonObject.getString("last_message"));
//                        String image = jsonObject.getString("image");
//                        isArchived = jsonObject.getBoolean("archive");
//                        String username = "mustafa";
//                        username = jsonObject.getString("username");
//                        String state = jsonObject.getString("state");
//                        String numberUnRMessage = jsonObject.getString("num_msg");
//
//
//                        postList.add(new ChatRoomModel(
//                                username,
//                                jsonObject.getString("other_id"),
//                                jsonObject.getString("last_message"),
//
//
//                                image,
//                                false,
//                                jsonObject.getString("num_msg"),
//                                jsonObject.getString("id"),
//                                state,
//                                numberUnRMessage,
//                                false,
//                                jsonObject.getString("user_token")
//
////                                "https://th.bing.com/th/id/OIP.2s7VxdmHEoDKji3gO_i-5QHaHa?pid=ImgDet&rs=1"
//
//                        ));
//                        System.out.println(AllConstants.base_url + "uploads/profile/" + jsonObject.getString("image"));
//                    }
//                    if (isArchived) {
//
//                        myBase.getObserver().setArchived(true);
//                    }
//                    System.out.println("postListttttttttttttttt" + postList.size());
//                    myBase.getObserver().setChatRoomModelList(postList);
////                    Intent intent = new Intent(IntroActivity.this, DashBord.class);
////
////                    startActivity(intent);
////                    IntroActivity.this.finish();
//                    System.out.println("myBase.getObserver().getChatRoomModelList().size()" + myBase.getObserver().getChatRoomModelList().size());
//
//
////                    else {
////                        linerArchived.setVisibility(View.GONE);
////
////                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    // progressDialog.dismiss();
//                }
////                if(isArchived){
////                    linerArchived.setVisibility(View.VISIBLE);
////                }
//                ///itemAdapter = new ChatRoomAdapter(postList, getApplicationContext(), listener);
////                itemAdapter = new ChatRoomAdapter(postList, BasicActivity.this);
////////                itemAdapter=new ChatRoomAdapter(getApplicationContext(),postList);
////                recyclerView.setAdapter(itemAdapter);
////                itemAdapter.notifyDataSetChanged();
////                Toast.makeText(BasicActivity.this, "Success", Toast.LENGTH_SHORT).show();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                // progressDialog.dismiss();
//                Toast.makeText(IntroActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }) {
//
//        };
//        myBase.addToRequestQueue(request);
//        System.out.println("postList" + postList.size());
//    }

    @Override
    public void update(Observable observable, Object o) {

    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                    startActivityForResult(intent, 2296);
                } catch (Exception e) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivityForResult(intent, 2296);
                }
            } else {
                createDirectory("memo");
                createDirectory("memo/send");
                createDirectory("memo/recive");
                createDirectory("memo/send/voiceRecord");
                createDirectory("memo/recive/voiceRecord");
                createDirectory("memo/send/video");
                createDirectory("memo/recive/video");
                chatRoomRepo.callAPI(myId);
            }
        } else {
            checkPermission();
        }
    }

    public void checkPermission() {


        if (permissions.isStorageWriteOk(IntroActivity.this) ) {
            createDirectory("memo");
            createDirectory("memo/send");
            createDirectory("memo/recive");
            createDirectory("memo/send/voiceRecord");
            createDirectory("memo/recive/voiceRecord");
            createDirectory("memo/send/video");
            createDirectory("memo/recive/video");
            chatRoomRepo.callAPI(myId);

        } else permissions.requestStorage(IntroActivity.this);
//        if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
//
//            // Requesting the permission
//            ActivityCompat.requestPermissions(IntroActivity.this, new String[] { permission }, requestCode);
//        }
//        else {
//            Toast.makeText(IntroActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
//            System.out.println("Permission already granted");
//        }
    }

    private void checkContactpermission() {

        if (permissions.isContactOk(this)) {
//            getContactList();
        checkPermission();

        } else {
            permissions.requestContact(this);
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {


//        if (requestCode == STORAGE_PERMISSION_CODE) {
//            System.out.println("Permission Granted");
//
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(IntroActivity.this, "Permission Granted", Toast.LENGTH_SHORT) .show();
//            }
//            else {
//                Toast.makeText(IntroActivity.this, "Permission Denied", Toast.LENGTH_SHORT) .show();
//            }
//        }
        switch (requestCode) {
            case AllConstants.STORAGE_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    createDirectory("memo");
                    createDirectory("memo/send");
                    createDirectory("memo/recive");
                    createDirectory("memo/send/voiceRecord");
                    createDirectory("memo/recive/voiceRecord");
                    createDirectory("memo/send/video");
                    createDirectory("memo/recive/video");
                    chatRoomRepo.callAPI(myId);
                } else
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                showPermissionDialog(getResources().getString(R.string.write_premission),2000);

                break;
            case AllConstants.CONTACTS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    serverApi.getContactList();
                            checkPermission();

                } else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                        showPermissionDialog(getResources().getString(R.string.contact_permission),1000);

                        // Comment 2.
//                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
//                        alertBuilder.setCancelable(true);
//                        alertBuilder.setTitle(getResources().getString(R.string.permission_necessary));
//                        alertBuilder.setMessage(getResources().getString(R.string.contact_permission));
//                        alertBuilder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
//
//                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//                            public void onClick(DialogInterface dialog, int which) {
//                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                Uri uri = Uri.fromParts("package", getPackageName(), null);
//                                intent.setData(uri);
//                                startActivityForResult(intent, 1000);                                }
//                        });
//
//                        AlertDialog alert = alertBuilder.create();
//                        alert.show();
                    }


                   }
                break;


        }

        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);
    }

    void createDirectory(String dName) {
//        File yourAppDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + dName);
        File yourAppDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + dName);

        if (!yourAppDir.exists() && !yourAppDir.isDirectory()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    Files.createDirectory(Paths.get(yourAppDir.getAbsolutePath()));
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "problem", Toast.LENGTH_LONG).show();
                }
            } else {
                yourAppDir.mkdir();
            }

        } else {
            Log.i("CreateDir", "App dir already exists");
        }


    }

    void sendToken(String token) {
        final ProgressDialog progressDialo = new ProgressDialog(IntroActivity.this);
        // url to post our data
//        progressDialo.setMessage("Uploading, please wait...");
//        progressDialo.show();
        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(IntroActivity.this);
        // on below line we are calling a string
        // request method to post the data to our API
        // in this we are calling a post method.
        StringRequest request = new StringRequest(Request.Method.POST, AllConstants.add_token, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                progressDialo.dismiss();
                System.out.println("Data added to API+ for token" + response);
                classSharedPreferences.setFcmToken(token);

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                Toast.makeText(IntroActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // below line we are creating a map for
                // storing our values in key and value pair.
                Map<String, String> params = new HashMap<String, String>();

                // on below line we are passing our key
                // and value pair to our parameters.
                params.put("users_id", myId);
                params.put("token", token);

                // at last we are
                // returning our params.
                return params;
            }
        };
        // below line is to make
        // a json object request.
        myBase.addToRequestQueue(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == 1000 ) {
        switch (requestCode){
            case 1000:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
                            showPermissionDialog(getResources().getString(R.string.contact_permission),1000);
//                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
//                            alertBuilder.setCancelable(true);
//                            alertBuilder.setTitle(getResources().getString(R.string.permission_necessary));
//                            alertBuilder.setMessage(getResources().getString(R.string.contact_permission));
//                            alertBuilder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
//
//                                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
//                                    intent.setData(uri);
//                                    startActivityForResult(intent, 1000);                                     }
//                            });
//
//                            AlertDialog alert = alertBuilder.create();
//                            alert.show();
                        }
                        else{
//                            serverApi.getContactList();
                            checkPermission();
                        }
                        break;
            case 2000:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    showPermissionDialog(getResources().getString(R.string.write_premission),2000);
//                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
//                            alertBuilder.setCancelable(true);
//                            alertBuilder.setTitle(getResources().getString(R.string.permission_necessary));
//                            alertBuilder.setMessage(getResources().getString(R.string.contact_permission));
//                            alertBuilder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
//
//                                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
//                                    intent.setData(uri);
//                                    startActivityForResult(intent, 1000);                                     }
//                            });
//
//                            AlertDialog alert = alertBuilder.create();
//                            alert.show();
                }
                else{
                    createDirectory("memo");
                    createDirectory("memo/send");
                    createDirectory("memo/recive");
                    createDirectory("memo/send/voiceRecord");
                    createDirectory("memo/recive/voiceRecord");
                    createDirectory("memo/send/video");
                    createDirectory("memo/recive/video");
                    chatRoomRepo.callAPI(myId);
                }
                break;



//        if (requestCode == 2296) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                if (Environment.isExternalStorageManager()) {
//                    createDirectory("memo");
//                    createDirectory("memo/send");
//                    createDirectory("memo/recive");
//                    createDirectory("memo/send/voiceRecord");
//                    createDirectory("memo/recive/voiceRecord");
//                    createDirectory("memo/send/video");
//                    createDirectory("memo/recive/video");
//                    serverApi.getChatRoom();
//                } else {
//                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
//                }
            }
        }
        public void showPermissionDialog(String message,int RequestCode){
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle(getResources().getString(R.string.permission_necessary));
            alertBuilder.setMessage(getResources().getString(R.string.contact_permission));
            alertBuilder.setMessage(message);

            alertBuilder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {

                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, RequestCode);                                     }
            });

            AlertDialog alert = alertBuilder.create();
            alert.show();


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
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void askCallPermission() {


        TelecomManager telecomManager;
        TelephonyManager telephonyManager;
        PhoneAccountHandle phoneAccountHandle;

        telecomManager = (TelecomManager) this.getSystemService(Context.TELECOM_SERVICE);
        telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        ComponentName componentName = new ComponentName(this, ConnService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            phoneAccountHandle = new PhoneAccountHandle(componentName, "com.darkhorse.videocalltest");

            PhoneAccount phoneAccount = PhoneAccount.builder(phoneAccountHandle, "com.darkhorse.videocalltest").setCapabilities(
                    PhoneAccount.CAPABILITY_SELF_MANAGED

            ).setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER).build();
            try {
                telecomManager.registerPhoneAccount(phoneAccount);
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(
                        "com.android.server.telecom",
                        "com.android.server.telecom.settings.EnableAccountPreferenceActivity"
                ));
                startActivity(intent);

            } catch (Exception e) {
                Log.e("main activity register", e.toString());
            }
        }
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
