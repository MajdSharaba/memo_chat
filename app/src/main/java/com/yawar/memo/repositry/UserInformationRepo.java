package com.yawar.memo.repositry;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.model.MediaModel;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.retrofit.RetrofitClient;
import com.yawar.memo.utils.BaseApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class UserInformationRepo {
    public MutableLiveData<String> blockedFor;
     public MutableLiveData<ArrayList<MediaModel>> mediaModelsMutableLiveData;
    private ArrayList<MediaModel> mediaModels;
    public MutableLiveData<UserModel> userInformation;




//    ChatRoomRepo chatRoomRepo = BaseApp.getInstance().getChatRoomRepo();


    public MutableLiveData<Boolean> blocked;
    public MutableLiveData<Boolean> unBlocked;
    BaseApp myBase = BaseApp.getInstance();




    public UserInformationRepo(Application application) { //application is subclass of context

        mediaModelsMutableLiveData = new MutableLiveData<>();
        mediaModels = new ArrayList<>();
        userInformation = new MutableLiveData<>();

    }





    @SuppressLint("CheckResult")
    public void getMedia(String user_id , String anthor_user_id) {
        mediaModels.clear();


        Single<String> observable = RetrofitClient.getInstance().getapi().getMedia(user_id,anthor_user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(s -> {
                      try {

                    JSONArray jsonArray = new JSONArray(s);

                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String image = jsonObject.getString("message");
                        mediaModels.add(new MediaModel(image));

                    }
                    mediaModelsMutableLiveData.setValue(mediaModels);


                } catch (JSONException e) {
                    e.printStackTrace();
                }



                },
                s -> {

                    mediaModelsMutableLiveData.setValue(null);
                });
    }

    @SuppressLint("CheckResult")
    public MutableLiveData<UserModel> getUserInformation(String anthor_user_id) {


        Single<String> observable = RetrofitClient.getInstance().getapi().getUserInformation(anthor_user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(s -> {
            System.out.println("s"+s);
//            return userInformation;
                    try {

                        JSONArray jsonArray = new JSONArray(s);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        UserModel userModel = new UserModel(jsonObject.getString("id"),jsonObject.getString("first_name"),jsonObject.getString("last_name"),jsonObject.getString("email"),jsonObject.getString("phone"),jsonObject.getString("sn"),jsonObject.getString("profile_image"),jsonObject.getString("status"));


                     userInformation.setValue(userModel);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                },
                s -> {
                    userInformation.setValue(null);
//                    mediaModelsMutableLiveData.setValue(null);
                });
        return userInformation;
    }


}
