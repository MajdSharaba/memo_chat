package com.yawar.memo.repositry;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.ChatMessage;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ChatMessageRepo {
    public final MutableLiveData<ArrayList<ChatMessage>> chatMessageistMutableLiveData;
    public ArrayList<ChatMessage> chatMessageList;


    public ChatMessageRepo(Application application) { //application is subclass of context

        //cant call abstract func but since instance is there we can do this
        chatMessageList = new ArrayList<>();
        chatMessageistMutableLiveData = new MutableLiveData<>();


    }

    @SuppressLint("CheckResult")
    public void getChatHistory(String my_id, String anthor_user_id) {
        chatMessageList.clear();


        Single<String> observable = RetrofitClient.getInstance(AllConstants.base_node_url).getapi().getChatMessgeHistory(my_id, anthor_user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(s -> {
                    try {
                        JSONArray jsonArray = new JSONArray(s);


                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            ChatMessage chatMessage = new ChatMessage();
                            chatMessage.setUserId(jsonObject.getString("sender_id"));
                            chatMessage.setState(jsonObject.getString("state"));
                            chatMessage.setMe(jsonObject.getString("sender_id").equals(my_id));
                            if (jsonObject.getString("message_type").equals("file") || jsonObject.getString("message_type").equals("voice") || jsonObject.getString("message_type").equals("video") || jsonObject.getString("message_type").equals("contact") || jsonObject.getString("message_type").equals("imageWeb")) {
                                chatMessage.setFileName(jsonObject.getString("orginalName"));
                                System.out.println(jsonObject.getString("message_type") + jsonObject.getString("message_id") + jsonObject.getString("orginalName") + "majjjjjjjjjjjjjjjjd");
                            }
//                            chatMessage.setFileName("orginalName");}


                            chatMessage.setId(jsonObject.getString("message_id"));
                            chatMessage.setChecked(false);
                            if (!jsonObject.getString("message_type").equals("imageWeb")) {
                                chatMessage.setMessage(jsonObject.getString("message"));

                            } else {
                                chatMessage.setImage(jsonObject.getString("message"));
                            }
                            chatMessage.setType(jsonObject.getString("message_type"));
                            chatMessage.setDate(jsonObject.getString("created_at"));
//                            chatMessage.setIsUpdate(jsonObject.getString("edited"));
                            chatMessage.setIsUpdate("0");


                            chatMessageList.add(chatMessage);


                        }


                        chatMessageistMutableLiveData.setValue(chatMessageList);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }//


                },
                s -> {
                    chatMessageList = null;
                    chatMessageistMutableLiveData.setValue(null);


                });
    }

    public void addMessage(ChatMessage chatMessage) {
//        System.out.println("addddddddddddddddddddddddddddddddddd");
        chatMessageList.add(chatMessage);
        chatMessageistMutableLiveData.setValue(chatMessageList);
    }

    public void deleteMessage(ChatMessage chatMessagee) {
        System.out.println("size before"+chatMessageList.size());
//        for(ChatMessage message:chatMessageList){
//            if(chatMessage.getId().equals(chatMessage.getId())){
//                chatMessageList.remove(message);
//                break;
//
//            }
//        }
        chatMessageList.remove(chatMessagee);


        System.out.println("delete message is done"+chatMessageList.size());

        chatMessageistMutableLiveData.setValue(chatMessageList);
    }
    public void UpdateMessage(String message_id,String message) {
//        chatMessageList.remove(chatMessage);
        for (ChatMessage chatMessage : chatMessageList) {
            System.out.println(chatMessage.getId() + "majdfadi" + message_id);
            if (chatMessage.getId().equals(message_id)) {
                chatMessage.setMessage(message);
                chatMessage.setIsUpdate("1");
                break;
            }
        }
        chatMessageistMutableLiveData.setValue(chatMessageList);
    }

    public void setMessageChecked(String message_id,boolean isChecked) {
//        chatMessageList.remove(chatMessage);
        for (ChatMessage chatMessage : chatMessageList) {
            if (chatMessage.getId().equals(message_id)) {
                chatMessage.setChecked(isChecked);
                break;
            }
        }
        chatMessageistMutableLiveData.setValue(chatMessageList);
    }

    public void setMessageState(String message_id,String state) {

        for (int i = chatMessageList.size() - 1; i >= 0; i--) {
            ////////state 3
            if(state.equals("3")){
                if (chatMessageList.get(i).getState().equals("3")) {
                    System.out.println(i + "===============");

                    break;
                }
                else
                    chatMessageList.get(i).setState(state);
            }
            ////////////// state 2
            else if(state.equals("2")){
                if (chatMessageList.get(i).getState().equals("2")) {
                    System.out.println(i + "===============");

                    break;
                }
                else {
                chatMessageList.get(i).setState(state);}

            }
            ////state 1
            else if (state.equals("1")){
                if(chatMessageList.get(i).getId().equals(message_id)){
                    chatMessageList.get(i).setState(state);
                    break;
                }
            }

        }
            chatMessageistMutableLiveData.setValue(chatMessageList);
    }
    public void setMessageDownload(String message_id,boolean isDownload) {
//        chatMessageList.remove(chatMessage);
        for (ChatMessage chatMessage : chatMessageList) {
            if (chatMessage.getId().equals(message_id)) {
                chatMessage.setDownload(isDownload);
                break;
            }
        }
        chatMessageistMutableLiveData.setValue(chatMessageList);
    }
}
//    public MutableLiveData<ArrayList<UserModel>> getUserBlockList() {
//
//        return userBlockListMutableLiveData;
//    }
//    public  void deleteBlockUser(String user_id,String status){
//        for(UserModel user:userBlockList){
//            if(user.getUserId().equals(user_id)){
//                user.setStatus(status);
//                break;
//            }}
//        userBlockListMutableLiveData.postValue(userBlockList);
//    }
//    public  void addBlockUser(UserModel userModel){
//        boolean searchBlock = false;
//        for(UserModel user:userBlockList){
//            if(user.getUserId().equals(userModel.getUserId())){
//                user.setStatus(userModel.getStatus());
//                searchBlock = true;
//                break;
//            }}
//        if(!searchBlock){
//            userBlockList.add( 0,userModel);}
//        userBlockListMutableLiveData.postValue(userBlockList);
//    }
//}
//
