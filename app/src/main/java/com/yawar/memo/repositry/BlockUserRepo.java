package com.yawar.memo.repositry;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


    public class BlockUserRepo {
        public final MutableLiveData<ArrayList<UserModel>> userBlockListMutableLiveData;
        public   ArrayList<UserModel> userBlockList;






        public BlockUserRepo(Application application) { //application is subclass of context

            //cant call abstract func but since instance is there we can do this
            userBlockList = new ArrayList<>();
            userBlockListMutableLiveData = new MutableLiveData<>();




        }

        @SuppressLint("CheckResult")
        public void getUserBlock(String user_id) {
            userBlockList.clear();


            Single<String> observable = RetrofitClient.getInstance(AllConstants.base_url).getapi().getBlockKist(user_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
            observable.subscribe(s -> {
                try {


                    System.out.println(s + "responeeeeeeee");
                    JSONObject respObj = new JSONObject(s);
                    JSONArray jsonArray = (JSONArray) respObj.get("data");

                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        //System.out.println(jsonObject.getString("last_message"));
                        String image = jsonObject.getString("profile_image");

                        String special_number = jsonObject.getString("sn");
                        String fName = jsonObject.getString("first_name");
                        String lName = jsonObject.getString("last_name");
                        String phone = jsonObject.getString("phone");
                        String userId = jsonObject.getString("id");
                        String email = jsonObject.getString("email");
                        String blockedFor = jsonObject.getString("blocked_for");

                        userBlockList.add(new UserModel(userId, fName, lName, email, phone, special_number, image, blockedFor));
                    }

                    userBlockListMutableLiveData.setValue(userBlockList);
                }catch (Exception e){
                    Log.d("Error", "getUserBlock: "+e);
                }





                    },
                    s -> {
                        userBlockList = null;
                        userBlockListMutableLiveData.setValue(null);
                    });
        }
        public MutableLiveData<ArrayList<UserModel>> getUserBlockList() {

            return userBlockListMutableLiveData;
        }
        public  void deleteBlockUser(String user_id,String status){
            for(UserModel user:userBlockList){
                if(user.getUserId().equals(user_id)){
                    user.setStatus(status);
                    break;
                }}
            userBlockListMutableLiveData.postValue(userBlockList);
        }
        public  void addBlockUser(UserModel userModel){
            boolean searchBlock = false;
            for(UserModel user:userBlockList){
                if(user.getUserId().equals(userModel.getUserId())){
                    user.setStatus(userModel.getStatus());
                    searchBlock = true;
                    break;
                }}
            if(!searchBlock){
            userBlockList.add( 0,userModel);}
            userBlockListMutableLiveData.postValue(userBlockList);
        }
    }
