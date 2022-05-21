package com.yawar.memo.repositry;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AuthRepo {


 public MutableLiveData<JSONObject> jsonObjectMutableLiveData;




        public AuthRepo(Application application) {
            jsonObjectMutableLiveData = new MutableLiveData<JSONObject>();
            jsonObjectMutableLiveData.setValue(null);





        }

        @SuppressLint("CheckResult")
        public void getspecialNumbers(String phoneNumber) {
            System.out.println(jsonObjectMutableLiveData.getValue()+"jsonObjectMutableLiveData");


            Single<String> observable = RetrofitClient.getInstance(AllConstants.base_url).getapi().getSpecialNumbers(phoneNumber)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
            observable.subscribe(s -> {
                JSONObject respObj = new JSONObject(s);
                JSONObject data = respObj.getJSONObject("data");
                jsonObjectMutableLiveData.setValue(data);
                System.out.println(jsonObjectMutableLiveData.getValue()+"data");

            },s-> {
                System.out.println("Error" + s);
                jsonObjectMutableLiveData.setValue(null);});

        }


    }

