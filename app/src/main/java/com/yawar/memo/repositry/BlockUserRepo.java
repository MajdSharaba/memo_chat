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
import com.yawar.memo.model.UserModel;
import com.yawar.memo.retrofit.RetrofitClient;
import com.yawar.memo.utils.BaseApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


    public class BlockUserRepo {
        public final MutableLiveData<ArrayList<UserModel>> userBlockListMutableLiveData;
        public   ArrayList<UserModel> userBlockList;
        public MutableLiveData<String> blockedForRepo;
        public MutableLiveData<Boolean> blockedRepo;
        public MutableLiveData<Boolean> unBlockedRepo;
        BaseApp myBase = BaseApp.getInstance();
        ChatRoomRepo chatRoomRepo = myBase.getChatRoomRepo();






        public BlockUserRepo(Application application) { //application is subclass of context

            //cant call abstract func but since instance is there we can do this
            userBlockList = new ArrayList<>();
            userBlockListMutableLiveData = new MutableLiveData<>();
            blockedForRepo = new MutableLiveData<>(null);
            blockedRepo = new MutableLiveData<>(null);
            unBlockedRepo = new MutableLiveData<>(null);

        }
        public MutableLiveData<String> getBlockedForRepo() {
            return blockedForRepo;
        }

        public void setBlockedForRepo(String blockedForRepo) {
            this.blockedForRepo.setValue(blockedForRepo);
        }

        public MutableLiveData<Boolean> getBlockedRepo() {
            return blockedRepo;
        }

        public void setBlockedRepo(Boolean blockedRepo) {
            this.blockedRepo.setValue(blockedRepo);
        }

        public MutableLiveData<Boolean> getUnBlockedRepo() {
            return unBlockedRepo;
        }

        public void setUnBlockedRepo(Boolean blockedRepo) {
            this.unBlockedRepo.setValue(blockedRepo);
        }

        @SuppressLint("CheckResult")
        public void getUserBlock(String user_id) {
            userBlockList= new ArrayList<>();
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

        @SuppressLint("CheckResult")
        public void sendBlockRequest(String my_id, String anthor_user_id) {


            Single<String> observable = RetrofitClient.getInstance(AllConstants.base_node_url).getapi().blockUser(my_id,anthor_user_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
            observable.subscribe(s -> {
                        try {
                    String blokedForRespone = "";
                    boolean blockedRespone = false;
                    try {
                        System.out.println("blockedddddddd");
                        JSONObject jsonObject = new JSONObject(s);
                        blokedForRespone = jsonObject.getString("blocked_for");
                        blockedRespone= jsonObject.getBoolean("blocked");


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    blockedForRepo.setValue(blokedForRespone);

                    chatRoomRepo.setBlockedState(anthor_user_id,blokedForRespone);
                    blockedRepo.setValue(true);

//


                    }catch (Exception e){
                        }
                    },
                    s -> {
                       System.out.println("problem");
                    });

        }
        @SuppressLint("CheckResult")
        public void sendUnbBlockUser(String my_id, String anthor_user_id) {
            Single<String> observable = RetrofitClient.getInstance(AllConstants.base_node_url).getapi().unBlockUser(my_id,anthor_user_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());


            observable.subscribe(s -> {
                        try {
                            System.out.println("unnnnBlockeddddddd");

                            String blokedForRespone = "";
                            Boolean unBlockedRespone = false;

                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                blokedForRespone = jsonObject.getString("blocked_for");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            blockedForRepo.setValue(blokedForRespone);
                            unBlockedRepo.setValue(unBlockedRespone);
                            chatRoomRepo.setBlockedState(anthor_user_id, blokedForRespone);
//


                        } catch (Exception e) {

                        }
                    },
                    s -> {
                        System.out.println("problem");
                    });



        }
    }
