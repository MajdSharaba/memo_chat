package com.yawar.memo.repositry;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;

import androidx.lifecycle.MutableLiveData;

import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.retrofit.RetrofitClient;
import com.yawar.memo.views.DashBord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRoomRepo {
    public final MutableLiveData<ArrayList<ChatRoomModel>> chatRoomListMutableLiveData;
    private ArrayList<ChatRoomModel> chatRoomsList;
    public final MutableLiveData<Boolean> isArchivedMutableLiveData;
    public final MutableLiveData<ChatRoomModel> chatRoomModelMutableLiveData;

    boolean isArchived = false;




    public ChatRoomRepo(Application application) { //application is subclass of context

        //cant call abstract func but since instance is there we can do this
        chatRoomsList = null;
        chatRoomListMutableLiveData = new MutableLiveData<>();
        isArchivedMutableLiveData = new MutableLiveData<>();
        chatRoomModelMutableLiveData = new MutableLiveData<>();



    }

    @SuppressLint("CheckResult")
    public MutableLiveData<ArrayList<ChatRoomModel>> callAPI(String user_id) {
        chatRoomsList=new ArrayList<>();
        chatRoomsList.clear();


        Single<String> observable = RetrofitClient.getInstance(AllConstants.base_url).getapi().getChatRoom(user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(s ->{
        try {
                        System.out.println(s.toString()+"dddddddddd");
                        JSONObject respObj = new JSONObject(s);
                        JSONArray dataArray = (JSONArray) respObj.get("data");
                        System.out.println(dataArray.toString()+"dataArray");




                        for (int i = 0; i < dataArray.length(); i++) {

                            JSONObject jsonObject = dataArray.getJSONObject(i);
                            //System.out.println(jsonObject.getString("last_message"));
                            String image = jsonObject.getString("image");
//                            isArchived = jsonObject.getBoolean("archive");
                            String special_number = jsonObject.getString("sn");
                            String username = "mustafa";
                            username = jsonObject.getString("username");
                            String state = jsonObject.getString("state");
                            if (state.equals("0")||state.equals(user_id)) {
                                isArchived = true;
                            }
                            String numberUnRMessage = jsonObject.getString("num_msg");
                            String lastMessageType = "text";
//                        if(jsonObject.getString("message_type")!=null){
                            lastMessageType =  jsonObject.getString("message_type");
                            String lastMeesageState = jsonObject.getString("mstate");
                            String lastMeesageTime = jsonObject.getString("created_at");
                            String blockedFor = jsonObject.getString("blocked_for");
//                            boolean isBlocked = false;






                            chatRoomsList.add(new ChatRoomModel(
                                    username,
                                    jsonObject.getString("other_id"),
                                    jsonObject.getString("last_message"),


                                    image,
                                    false,
                                    jsonObject.getString("num_msg"),
                                    jsonObject.getString("id"),
                                    state,
                                    numberUnRMessage,
                                    false,
                                    jsonObject.getString("user_token")
                                    ,special_number
                                    ,lastMessageType
                                    ,lastMeesageState
                                    ,lastMeesageTime
                                    ,false
                                    ,blockedFor



                            ));
                        }
                        if (isArchived) {
                            isArchivedMutableLiveData.setValue(isArchived);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.println(chatRoomsList.size()+"chatRoomsList.size()");
                    chatRoomListMutableLiveData.setValue(chatRoomsList);
                },


                s -> {System.out.println(s+"ssssssssss");
            chatRoomListMutableLiveData.setValue(null);});
        return chatRoomListMutableLiveData;
    }
    public MutableLiveData<ArrayList<ChatRoomModel>> getChatRoomListMutableLiveData() {
        return  chatRoomListMutableLiveData;
    }
    public ArrayList <ChatRoomModel>getChatRoomModelList(){
        return chatRoomsList;
    }

    public void setChatRoomModelList(ArrayList<ChatRoomModel> chatRoomModelList) {
//        this.chatRoomsList = chatRoomModelList;
        chatRoomListMutableLiveData.setValue(chatRoomModelList);
    }
    public  void deleteChatRoom(String chatId){
        for(ChatRoomModel chatRoom:chatRoomsList){
            if(chatRoom.chatId.equals(chatId)){
                chatRoomsList.remove(chatRoom);
                break;
            }}
        chatRoomListMutableLiveData.setValue(chatRoomsList);
    }
    public  void addChatRoom(ChatRoomModel chatRoomModel){
//        System.out.println("addddddddddddddddddddddddddddddddddd");
        chatRoomsList.add( 0,chatRoomModel);
        chatRoomListMutableLiveData.setValue(chatRoomsList);
    }
    public void setLastMessage(String message , String chatId,String senderId, String reciverId,String type,String state,String dateTime){
        boolean inList=false;
        for(ChatRoomModel chatRoom:chatRoomsList){
            if(chatRoom.chatId.equals(chatId)){
                chatRoom.setLastMessage(message);
                chatRoom.setLastMessageType(type);
                chatRoom.setLastMessageState(state);
                chatRoom.setLastMessageTime(dateTime);





                inList=true;

                if(!chatRoom.inChat){
                    chatRoom.setNumberUnRMessage(String.valueOf(Integer.parseInt(chatRoom.getNumberUnRMessage())+1));
                }

                chatRoomsList.remove(chatRoom);
                chatRoomsList.add(0,chatRoom);
                break;
            }
        }

        if(!inList){
            for(ChatRoomModel chatRoom:chatRoomsList){
                if(chatRoom.chatId.equals(senderId+reciverId)){
                    chatRoomsList.remove(chatRoom);
                    System.out.println("chatRoom.setLastMessage(message)outtttttttttttttt chatrommmmmmmmmmm");
                    if(!chatRoom.inChat){
                        chatRoom.setNumberUnRMessage(String.valueOf(Integer.parseInt(chatRoom.getNumberUnRMessage())+1));
                    }
                    chatRoom.setChatId(chatId);
                    System.out.println("removeeeeeeeeeeeee and Addddddddd");
                    chatRoomsList.remove(chatRoom);
                    chatRoomsList.add(0,chatRoom);

                    break;
                }

            }
        }

        chatRoomListMutableLiveData.setValue(chatRoomsList);
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
        isArchivedMutableLiveData.setValue(isArchived);
//        chatRoomListMutableLiveData.setValue(chatRoomsList);
    }
    /// state 1 for Archived ChatRoom
    public void setState(String chatId,String state) {
        for(ChatRoomModel chatRoom:chatRoomsList){
            if(chatRoom.chatId.equals(chatId)){
                chatRoom.setState(state);

                break;
            }
        }
        chatRoomListMutableLiveData.setValue(chatRoomsList);
    }

    public void setInChat(String chat_id,boolean state){
        for(ChatRoomModel chatRoom:chatRoomsList){
            if(chatRoom.chatId.equals(chat_id)){
                chatRoom.setInChat(state);
                if(state){
                    chatRoom.setNumberUnRMessage("0");}

                break;
            }
            chatRoomListMutableLiveData.setValue(chatRoomsList);

        }
    }
    public String getChatId(String anthorUserId){
        System.out.println("getChatId"+anthorUserId);
        for(ChatRoomModel chatRoom:chatRoomsList){
            if(chatRoom.userId.equals(anthorUserId)){

                return chatRoom.chatId;
            }


        }
        return "";

    }
    public  boolean checkInChat(String chat_id) {
        for (ChatRoomModel chatRoom : chatRoomsList) {
            if (chatRoom.chatId.equals(chat_id)) {
                if (chatRoom.inChat) {
                    return true;
                } else {
                    return false;
                }

            }
        }

        return  false;
    }
    public  void setTyping(String chat_id,boolean isTyping) {
        for (ChatRoomModel chatRoom : chatRoomsList) {
            if (chatRoom.chatId.equals(chat_id)) {
                chatRoom.setTyping(isTyping);
                break;

            }
        }
        chatRoomListMutableLiveData.setValue(chatRoomsList);
    }
    public  void setBlockedState(String chat_id,String blockedFor) {
        for (ChatRoomModel chatRoom : chatRoomsList) {
            if (chatRoom.chatId.equals(chat_id)) {
                chatRoom.setBlockedFor(blockedFor);
                break;

            }
        }
        chatRoomListMutableLiveData.setValue(chatRoomsList);
    }
    public void getChatRoom(String chat_id){
        for (ChatRoomModel chatRoom : chatRoomsList) {
            if (chatRoom.chatId.equals(chat_id)) {
                chatRoomModelMutableLiveData.setValue(chatRoom);
                break;


            }
        }
    }
}
