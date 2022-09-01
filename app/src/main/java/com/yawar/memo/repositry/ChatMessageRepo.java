package com.yawar.memo.repositry;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yawar.memo.R;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.ChatMessage;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.retrofit.RetrofitClient;
import com.yawar.memo.service.SocketIOService;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
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

public class ChatMessageRepo {
    public final MutableLiveData<ArrayList<ChatMessage>> chatMessageistMutableLiveData;
    public ArrayList<ChatMessage> chatMessageList;
    ChatRoomRepo chatRoomRepo = BaseApp.getInstance().getChatRoomRepo();

    public final MutableLiveData<ArrayList<ChatMessage>> selectedMessage;


    public MutableLiveData<String> blockedFor;
    public MutableLiveData<Boolean> blocked;
    public MutableLiveData<Boolean> unBlocked;




    public ArrayList<ChatMessage> _selectedMessage;
    BaseApp myBase = BaseApp.getInstance();






    public ChatMessageRepo(Application application) { //application is subclass of context

        //cant call abstract func but since instance is there we can do this
        chatMessageList = new ArrayList<>();
        chatMessageistMutableLiveData = new MutableLiveData<>();
        selectedMessage = new MutableLiveData<>();
        _selectedMessage = new ArrayList<>();







    }

    public void addSelectedMessage(ChatMessage message){
        _selectedMessage.add(message);
        selectedMessage.setValue(_selectedMessage);
    }
    public void removeSelectedMessage(ChatMessage message){
        _selectedMessage.remove(message);
        selectedMessage.setValue(_selectedMessage);
    }
    public void clearSelectedMessage(){
        for(ChatMessage chatMessage: selectedMessage.getValue()){
            setMessageChecked(chatMessage.getId(),false);

        }
        _selectedMessage.clear();
        selectedMessage.setValue(_selectedMessage);
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

                            if(jsonObject.optJSONObject("reply_message") != null && !jsonObject.optJSONObject("reply_message").equals("") && !jsonObject.optJSONObject("reply_message").equals("null")) {
//                                chatMessage.setReply(jsonObject.getJSONObject("reply_message").getString("message"));
                            }else{
                              //  chatMessage.setReply("");
                            }
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

                    break;
                }
                else
                    chatMessageList.get(i).setState(state);
            }
            ////////////// state 2
            else if(state.equals("2")){
                if (chatMessageList.get(i).getState().equals("2")||chatMessageList.get(i).getState().equals("3")) {

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

    @SuppressLint("CheckResult")
    public void deleteMessageForMe(String message_id, String user_id, ArrayList<ChatMessage> chatMessages){
        Single<String> observable = RetrofitClient.getInstance(AllConstants.base_node_url).getapi().deleteMessage(message_id,user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(s -> {
                    System.out.println("respone delem"+chatMessages.size()+""+chatMessageList.size());


                    for (int i = 0; i < chatMessages.size(); i++) {

                String id = chatMessages.get(i).getId();
                for (ChatMessage chatMessage : chatMessageList) {
                    if (chatMessage.getId().equals(id)) {
                        System.out.println("majd ssssss"+chatMessage.getId() + " " + message_id);
                       deleteMessage(chatMessage);
                        break;
                    }
                }



                    }
                    clearSelectedMessage();

                    return ;

                },
                s-> {

                    return ;

                });

    }


}

