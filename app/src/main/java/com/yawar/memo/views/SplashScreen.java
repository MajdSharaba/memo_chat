package com.yawar.memo.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.yawar.memo.Api.ClassSharedPreferences;
import com.yawar.memo.Api.ServerApi;
import com.yawar.memo.R;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SplashScreen extends AppCompatActivity {
    private static int SPLASH_SCREEN_TIME_OUT=2000;
    ClassSharedPreferences classSharedPreferences;
    private static final int STORAGE_PERMISSION_CODE = 101;

    ServerApi serverApi;

//    float textSize = 14.0F ;
    SharedPreferences sharedPreferences ;
    TextView text ;
    TextView powerd ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final String[] darkModeValues = getResources().getStringArray(R.array.dark_mode_values);
        // The apps theme is decided depending upon the saved preferences on app startup
        String pref = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.dark_mode), getString(R.string.dark_mode_def_value));
        // Comparing to see which preference is selected and applying those theme settings
        if (pref.equals(darkModeValues[0])){
            System.out.println(darkModeValues[0]+"majdddddddddddddd"+pref);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);}
        else if (pref.equals(darkModeValues[1])){
            System.out.println(darkModeValues[0]+"ahmaaadddddddddddddddd"+pref);

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);}
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ///// for dark modeee
//        final String[] darkModeValues = getResources().getStringArray(R.array.dark_mode_values);
//        // The apps theme is decided depending upon the saved preferences on app startup
//        String pref = PreferenceManager.getDefaultSharedPreferences(this)
//                .getString(getString(R.string.dark_mode), getString(R.string.dark_mode_def_value));
//        // Comparing to see which preference is selected and applying those theme settings
//        if (pref.equals(darkModeValues[0])){
//            System.out.println(darkModeValues[0]+"majdddddddddddddd"+pref);
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);}
//        else if (pref.equals(darkModeValues[1])){
//            System.out.println(darkModeValues[0]+"ahmaaadddddddddddddddd"+pref);
//
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);}
        classSharedPreferences = new ClassSharedPreferences(this);
        serverApi = new ServerApi(this);
//        setContentView(R.layout.activity_splash_screen);

        sharedPreferences = getSharedPreferences("txtFontSize", Context.MODE_PRIVATE);
        text = findViewById(R.id.text);
//        text.setTextSize(textSize);
//        text.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        powerd = findViewById(R.id.powerd);
//        powerd.setTextSize(textSize);
//        powerd.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));



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
                    serverApi.register();

                }


                //invoke the SecondActivity.

//                finish();
                //the current activity will get finished.
            }
        }, SPLASH_SCREEN_TIME_OUT);
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
