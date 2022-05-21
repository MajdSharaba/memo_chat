package com.yawar.memo.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.PhoneAuthProvider;
import com.yawar.memo.Api.AuthApi;
import com.yawar.memo.Api.ClassSharedPreferences;
import com.yawar.memo.R;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.repositry.AuthRepo;
import com.yawar.memo.utils.BaseApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

public class VerificationActivity extends AppCompatActivity implements Observer {
    Button virvectbtn;
    Button resendbtn;
    ClassSharedPreferences classSharedPreferences;
    private EditText  edtOTP;
    BaseApp myBase;
    int count=60;
    AuthRepo authRepo;
    Timer T;
    public PhoneAuthProvider.ForceResendingToken forceResendingToken ;

    TextView text ;
    TextView orText ;
//    float textSize = 14.0F ;
    SharedPreferences sharedPreferences ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        sharedPreferences =  getSharedPreferences("txtFontSize", Context.MODE_PRIVATE);


        virvectbtn = findViewById(R.id.btn_verification);

        text = findViewById(R.id.text);
//        text.setTextSize(textSize);
//        text.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        orText = findViewById(R.id.orText);
//        orText.setTextSize(textSize);
//        orText.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        myBase = BaseApp.getInstance();
        authRepo = myBase.getAuthRepo();
        authRepo.jsonObjectMutableLiveData.observe(this ,new androidx.lifecycle.Observer<JSONObject>() {
            @Override
            public void onChanged(JSONObject jsonObject) {
                if(jsonObject!=null) {
                    System.out.println("json object not null"+jsonObject);

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

                        Intent intent = new Intent(VerificationActivity.this, RegisterActivity.class);

                        startActivity(intent);
                        finish();}
                    else{
                        UserModel userModel = new UserModel(user_id,first_name,last_name,email,number,secret_number,profile_image,status);
                        classSharedPreferences.setUser(userModel);
                        Intent intent = new Intent(VerificationActivity.this, IntroActivity.class);
                        startActivity(intent);
                        finish();
                        UserModel userModel1 = new UserModel(user_id,first_name,last_name,email,number,secret_number,profile_image,status);
                        classSharedPreferences.setUser(userModel1);
                    }
                }
            }
        });



        timer();
        edtOTP = findViewById(R.id.et_verifiction);
        resendbtn = findViewById(R.id.btn_resendCode);
        resendbtn.setEnabled(false);
        myBase = BaseApp.getInstance();
        myBase.getObserver().addObserver(this);
        forceResendingToken =myBase.getForceResendingToken().getForceResendingToken();

        classSharedPreferences = new ClassSharedPreferences(VerificationActivity.this);

        virvectbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edtOTP.getText().toString())) {
                    // when mobile number text field is empty
                    // displaying a toast message.
                    Toast.makeText(VerificationActivity.this, R.string.valied_message, Toast.LENGTH_SHORT).show();
                } else {
                    // if the text field is not empty we are calling our
                    // send OTP method for getting OTP from Firebase.
                    AuthApi authApi = new  AuthApi(VerificationActivity.this);
                    authApi.verifyCode(edtOTP.getText().toString());
                }

            }
        });
        resendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendbtn.setEnabled(false);
                timer();
                System.out.println(classSharedPreferences.getNumber());
                AuthApi authApi = new AuthApi(VerificationActivity.this);
                authApi.resendVerificationCode(classSharedPreferences.getNumber(),forceResendingToken, VerificationActivity.this);

            }
        });
    }
    void timer(){
        T=new Timer();
        T.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        resendbtn.setText(count+"");
                        count--;
                        if(count<0){
                            resendbtn.setEnabled(true);
                            resendbtn.setText(R.string.resend);
                            count=60;
                            T.cancel();


                        }
                    }
                });
            }
        }, 1000, 1000);

    }

    @Override
    public void update(Observable observable, Object o) {
        forceResendingToken =myBase.getForceResendingToken().getForceResendingToken();
        System.out.println(forceResendingToken+"forceResendingToken");
    }
}