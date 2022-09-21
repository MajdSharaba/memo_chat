package com.yawar.memo.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yawar.memo.call.CallProperty;
import com.yawar.memo.modelView.SplachActViewModel;
import com.yawar.memo.modelView.VerficationViewModel;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.Api.ServerApi;
import com.yawar.memo.R;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.repositry.AuthRepo;
import com.yawar.memo.utils.BaseApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SplashScreen extends AppCompatActivity {
    private static final int SPLASH_SCREEN_TIME_OUT=3000;
    ClassSharedPreferences classSharedPreferences;
    private static final int STORAGE_PERMISSION_CODE = 101;


    TextView text ;
    TextView powerd ;
    AuthRepo authRepo;
    BaseApp myBase;
    SplachActViewModel splachActViewModel;
    ProgressDialog progressDialog;
    ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CallProperty.setStatusBarOrScreenStatus(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        classSharedPreferences = BaseApp.getInstance().getClassSharedPreferences();


        myBase = BaseApp.getInstance();


        authRepo = myBase.getAuthRepo();

       text = findViewById(R.id.text);

        powerd = findViewById(R.id.powerd);
        progressBar = findViewById(R.id.progress_circular);

        splachActViewModel = new ViewModelProvider(this).get(SplachActViewModel.class);





        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;

                if(classSharedPreferences.getUser()!=null){
                intent = new Intent(SplashScreen.this,
                        IntroActivity.class);

                    startActivity(intent);
                    finish();
                }
                else if (classSharedPreferences.getVerficationNumber()==null){

                    //Intent is used to switch from one activity to another.
                intent  = new Intent(SplashScreen.this,LoginActivity.class);

                    startActivity(intent);
                    finish();
                        }
                else {
//                    authRepo.getspecialNumbers(classSharedPreferences.getVerficationNumber());
                    splachActViewModel.getSpecialNumber(classSharedPreferences.getVerficationNumber()).observe(SplashScreen.this ,new androidx.lifecycle.Observer<JSONObject>() {
                        @Override
                        public void onChanged(JSONObject jsonObject) {

                            if(jsonObject!=null) {
                                Log.d("getUserrr", "onChanged: "+jsonObject);
                                authRepo.jsonObjectMutableLiveData.removeObserver(this);
                                String sn="";
                                String user_id="" ;
                                String first_name="" ;
                                String last_name="" ;
                                String email="" ;
                                String profile_image="";
                                String secret_number="";
                                String number="";
                                String status="";


                                try {
                                    JSONObject userObject  = jsonObject.getJSONObject("user");
                                    sn = userObject.getString("sn");
                                    user_id = userObject.getString("id");
                                     first_name = userObject.getString("first_name");
                                     last_name = userObject.getString("last_name");
                                    email = userObject.getString("email");
                                    profile_image = userObject.getString("profile_image");
                                     secret_number = userObject.getString("sn");
                                     number = userObject.getString("phone");
                                     status= userObject.getString("status");




                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if(sn.isEmpty()){

                                Intent intent = new Intent(SplashScreen.this, RegisterActivity.class);

                                startActivity(intent);
                                finish();}
                                else{
                                    UserModel userModel = new UserModel(user_id,first_name,last_name,email,number,secret_number,profile_image,status);
                                    classSharedPreferences.setUser(userModel);
                                    Intent intent = new Intent(SplashScreen.this, IntroActivity.class);
                                    startActivity(intent);
                                    finish();

                                }
                            }
                        }
                    });


//                    splachActViewModel.getLoading().observe(SplashScreen.this, new androidx.lifecycle.Observer<Boolean>() {
//                        @Override
//                        public void onChanged(Boolean aBoolean) {
//                            if (aBoolean!=null) {
//                                if (aBoolean) {
//                                    progressBar.setVisibility(View.VISIBLE);
////                                    System.out.println("boleannnn");
////                                    progressDialog = new ProgressDialog(SplashScreen.this);
////                                    progressDialog.setMessage(getResources().getString(R.string.prograss_message));
////                                    progressDialog.show();
//                                } else {
//                                    progressBar.setVisibility(View.GONE);
//
////                                    if(progressDialog!=null){
////                                        progressDialog.dismiss();
////                                    }
//                                }
//                            }
//                        }
//                    });

                    splachActViewModel.getErrorMessage().observe(SplashScreen.this, new androidx.lifecycle.Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean aBoolean) {
                            if (aBoolean!=null) {
                                if (aBoolean) {
                                    Toast.makeText(SplashScreen.this, R.string.internet_message, Toast.LENGTH_LONG).show();
                                    splachActViewModel.setErrorMessage(null);
                                }
                            }
                        }
                    });

                }


                //invoke the SecondActivity.

//                finish();
                //the current activity will get finished.
            }
        }, SPLASH_SCREEN_TIME_OUT);

//        authRepo.jsonObjectMutableLiveData.observe(this ,new androidx.lifecycle.Observer<JSONObject>() {
//            @Override
//            public void onChanged(JSONObject jsonObject) {
//                if(jsonObject!=null) {
//                    System.out.println("SplashScreen");
//                    authRepo.jsonObjectMutableLiveData.removeObserver(this);
//
//                    Intent intent = new Intent(SplashScreen.this, RegisterActivity.class);
//
//                    startActivity(intent);
//                    finish();
//                }
//            }
//        });
    }


    public void checkPermission(String permission, int requestCode)
    {

        if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(SplashScreen.this, new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(SplashScreen.this, "Permission already granted", Toast.LENGTH_SHORT).show();
            System.out.println("Permission already granted");
        }
    }
    public void onRequestPermissionsResult(int requestCode,
                                          @NonNull String[] permissions,
                                          @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            System.out.println("Camera Permission Granted");

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(SplashScreen.this, "Camera Permission Granted", Toast.LENGTH_SHORT) .show();
            }
            else {
                Toast.makeText(SplashScreen.this, "Camera Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }


    }

    void  createDirectory(String dName){
       File yourAppDir = new File(Environment.getExternalStorageDirectory()+File.separator+dName);

       if(!yourAppDir.exists() && !yourAppDir.isDirectory())
       {
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
               try {
                   Files.createDirectory(Paths.get(yourAppDir.getAbsolutePath()));
               } catch (IOException e) {
                   e.printStackTrace();
                   Toast.makeText(getApplicationContext(),"problem", Toast.LENGTH_LONG).show();
               }
           } else {
               yourAppDir.mkdir();
           }

//           System.out.println(yourAppDir.getAbsolutePath().toString()+"my Adressss");
//
//
//           // create empty directory
//           if (yourAppDir.mkdirs())
//           {
//               Log.i("CreateDir","App dir created");
//           }
//           else
//           {
//               Log.w("CreateDir","Unable to create app dir!");
//           }
       }
       else
       {
           Log.i("CreateDir","App dir already exists");
       }


   }

    }
