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
    public MutableLiveData<Boolean> loading;
    public MutableLiveData<Boolean> showErrorMessage;






    public AuthRepo(Application application) {
            jsonObjectMutableLiveData = new MutableLiveData<JSONObject>();
            jsonObjectMutableLiveData.setValue(null);
            loading = new MutableLiveData<>(false);
            showErrorMessage = new MutableLiveData<>(false);



        }

        @SuppressLint("CheckResult")
        public MutableLiveData<JSONObject> getspecialNumbers(String phoneNumber) {
            System.out.println(jsonObjectMutableLiveData.getValue()+"jsonObjectMutableLiveData");
            try {

                loading.setValue(true);
                Single<String> observable = RetrofitClient.getInstance(AllConstants.base_url).getapi().getSpecialNumbers(phoneNumber)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                observable.subscribe(s -> {
                    JSONObject respObj = new JSONObject(s);
                    JSONObject data = respObj.getJSONObject("data");
                    jsonObjectMutableLiveData.setValue(data);
                    loading.setValue(false);
                    showErrorMessage.setValue(false);
                    System.out.println(jsonObjectMutableLiveData.getValue() + "data");

                }, s -> {
                    System.out.println("Error" + s);
                    loading.setValue(false);
                    showErrorMessage.setValue(true);
                    jsonObjectMutableLiveData.setValue(null);
                });
            }catch (Exception error) {
                System.out.println("l,l,");
            }


            return jsonObjectMutableLiveData;

        }





    }

