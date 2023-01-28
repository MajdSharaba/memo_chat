package com.yawar.memo.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.yawar.memo.BaseApp;
import com.yawar.memo.repositry.AuthRepo;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.ui.verficationPage.VerificationActivity;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

/////this class for Api with firebase
public class AuthApi implements Observer {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    BaseApp myBase = BaseApp.Companion.getInstance();
    ProgressDialog progressDialog;
    AuthRepo authApi= myBase.authRepo;
    public MutableLiveData<Boolean> loading;
    public MutableLiveData<Boolean> showErrorMessage;
    public String errorMessage = "ERROR";
    public AuthApi(Activity context) {
        loading = new MutableLiveData<>(false);
        showErrorMessage = new MutableLiveData<>(false);
        this.context = context;
    }

    Activity context;
//    ServerApi serverApi;
    private String verificationId;
    ClassSharedPreferences classSharedPreferences;


    private void signInWithCredential(PhoneAuthCredential credential) {
//        serverApi = new  ServerApi(context);

        ClassSharedPreferences classSharedPreferences = BaseApp.Companion.getInstance().getClassSharedPreferences();

        // inside this method we are checking if
        // the code entered is correct or not.
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            System.out.println(user.getPhoneNumber()+"phone number");
//                            classSharedPreferences.setVerficationNumber(user.getPhoneNumber());
                            classSharedPreferences.setVerficationNumber(user.getUid());

                            // if the code is correct and the task is successful
                            // we are sending our user to new activity.
//                            serverApi.register();

                            authApi.getspecialNumbers(classSharedPreferences.getVerficationNumber());

                        } else {
                            // if the code is not correct then we are
                            // displaying an error message to the user.
                            errorMessage = task.getException().getMessage() ;

                            showErrorMessage.setValue(true);
//                            System.out.println(" showErrorMessage.setValue(true)"+ showErrorMessage.getValue());


                            BaseApp.Companion.getInstance().authRepo.setLoading(false);

//                            Toast.makeText(context, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    public void sendVerificationCode(String number,Activity activity) {
        loading.setValue(true);
//        progressDialog = new ProgressDialog(context);
//        progressDialog.setMessage(context.getResources().getString(R.string.prograss_message));
//        progressDialog.setCancelable(false);
//        progressDialog.show();
        // this method is used for getting
        // OTP on user phone number.
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)

                        .setPhoneNumber(number)
                        .setActivity(activity)// Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setCallbacks(mCallBack)           // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }
    public void resendVerificationCode(String phoneNumber,PhoneAuthProvider.ForceResendingToken forceResendingToken,
                                        Activity activity) {
//        progressDialog = new ProgressDialog(context);
//        progressDialog.setMessage(context.getResources().getString(R.string.prograss_message));
//        progressDialog.show();
//        System.out.println(forceResendingToken);
        loading.setValue(true);

        SharedPreferences prefs = context.getSharedPreferences("auth", MODE_PRIVATE);

        String name = prefs.getString("verificationid",null);
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(activity)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(forceResendingToken)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks

            // initializing our callbacks for on
            // verification callback method.
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        // below method is used when
        // OTP is sent from Firebase
        @Override
        public void onCodeSent(String verificationid, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            SharedPreferences prefs = context.getSharedPreferences("auth", MODE_PRIVATE);



            super.onCodeSent(verificationid, forceResendingToken);

            loading.setValue(false);
//            progressDialog.dismiss();


            // when we receive the OTP it
            // contains a unique id which
            // we are storing in our string
            // which we have alread y created.
            Log.d("nnmn", "onCodeSent:" + verificationid);
//            AllConstants.forceResendingToken=forceResendingToken;
            myBase = BaseApp.Companion.getInstance();
            myBase.getForceResendingToken().addObserver(AuthApi.this);
            myBase.getForceResendingToken().setForceResendingToken(forceResendingToken);
            System.out.println(forceResendingToken);

            prefs.edit().putString("verificationid",verificationid).commit();
            Intent i = new Intent(context, VerificationActivity.class);

            context.startActivity(i);
            context.finish();


        }

        // this method is called when user
        // receive OTP from Firebase.
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            System.out.println("coooodeeee"+phoneAuthCredential.getSmsCode());
            // below line is used for getting OTP code
            // which is sent in phone auth credentials.
            // final String code = phoneAuthCredential.getSmsCode();

            // checking if the code
            // is null or not.
//            if (code != null) {
//                // if the code is not null then
//                // we are setting that code to
//                // our OTP edittext field.
////                edtOTP.setText(code);
//
//                // after setting this code
//                // to OTP edittext field we
//                // are calling our verifycode method.
//               /// verifyCode(code,activity);
//            }

        }

        // this method is called when firebase doesn't
        // sends our OTP code due to any error or issue.
        @Override
        public void onVerificationFailed(FirebaseException e) {
            // displaying error message with firebase exception.
//            progressDialog.dismiss();
            System.out.println("loading false");
            loading.setValue(false);
            errorMessage = e.getMessage();

            showErrorMessage.setValue(true);
            System.out.println(e.getMessage());

//            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    // below method is use to verify code from Firebase.
    public void verifyCode(String code) {

        // below line is used for getting getting
        // credentials from our verification id and code.
//        Globle globle = new Globle();
//        System.out.println(globle.getVerificationId()+"mnnnnnnnnnnnnnnnnn");
//        progressDialog = new ProgressDialog(context);
//        progressDialog.setMessage(context.getResources().getString(R.string.prograss_message));
//        progressDialog.show();
        SharedPreferences prefs = context.getSharedPreferences("auth", MODE_PRIVATE);

        String name = prefs.getString("verificationid",null);
        System.out.println(name+"kkkkkkkkkkkkkkkkkkk");

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(name, code);

        // after getting credential we are
        // calling sign in method.
        signInWithCredential(credential);
    }


    @Override
    public void update(Observable observable, Object o) {

    }

}



