package com.yawar.memo.repositry;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.model.ChatRoomRespone;
import com.yawar.memo.retrofit.RetrofitClient;

import java.util.ArrayList;


import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ChatRoomRepo {
    public final MutableLiveData<ArrayList<ChatRoomModel>> chatRoomListMutableLiveData;
    private ArrayList<ChatRoomModel> chatRoomsList;
    public final MutableLiveData<Boolean> isArchivedMutableLiveData;
    public final MutableLiveData<ChatRoomModel> chatRoomModelMutableLiveData;
    public MutableLiveData<Boolean> loading;
    public MutableLiveData<Boolean> showErrorMessage;

    boolean isArchived = false;

    public ChatRoomRepo(Application application) { //application is subclass of context

        //cant call abstract func but since instance is there we can do this
        chatRoomsList = null;
        chatRoomListMutableLiveData = new MutableLiveData<>();
        isArchivedMutableLiveData = new MutableLiveData<>();
        chatRoomModelMutableLiveData = new MutableLiveData<>();
        loading = new MutableLiveData<>(false);
        showErrorMessage = new MutableLiveData<>(false);



    }

    @SuppressLint("CheckResult")
    public MutableLiveData<ArrayList<ChatRoomModel>> callAPI(String user_id) {
        try {
            chatRoomsList = new ArrayList<>();
            chatRoomsList.clear();
            loading.setValue(true);
            System.out.println("AllConstants.base_url_final"+AllConstants.base_url_final);


            Single<ChatRoomRespone> observable = RetrofitClient.getInstance().getapi().getChatRoom(user_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
            observable.subscribe(s -> {
                chatRoomsList = s.getData();

                chatRoomListMutableLiveData.setValue(chatRoomsList);
                loading.setValue(false);
                showErrorMessage.setValue(false);

            }, s -> {
                chatRoomListMutableLiveData.setValue(null);
                loading.setValue(false);
                showErrorMessage.setValue(true);
            });
        }
        catch (Exception e){
            System.out.println("error");
        }


//        Single<String> observable = RetrofitClient.getInstance(AllConstants.base_url).getapi().getChatRoom(user_id)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//        observable.subscribe(s ->{
//        try {
//            Log.d("getUserrr", "callAPI: "+s);
//                        JSONObject respObj = new JSONObject(s);
//                        JSONArray dataArray = (JSONArray) respObj.get("data");
//                        System.out.println(dataArray +"dataArray");
//
//
//
//
//                        for (int i = 0; i < dataArray.length(); i++) {
//
//                            JSONObject jsonObject = dataArray.getJSONObject(i);
//                            //System.out.println(jsonObject.getString("last_message"));
//                            String image = jsonObject.getString("image");
////                            isArchived = jsonObject.getBoolean("archive");
//                            String special_number = jsonObject.getString("sn");
//                            String username = "mustafa";
//                            username = jsonObject.getString("username");
//                            String state = jsonObject.getString("state");
//                            if (state.equals("0")||state.equals(user_id)) {
//                                isArchived = true;
//                            }
//                            String numberUnRMessage = jsonObject.getString("num_msg");
//                            String lastMessageType = "text";
////                        if(jsonObject.getString("message_type")!=null){
//                            lastMessageType =  jsonObject.getString("message_type");
//                            String lastMeesageState = jsonObject.getString("mstate");
//                            String lastMeesageTime = jsonObject.getString("created_at");
//                            String blockedFor = jsonObject.getString("blocked_for");
////                            boolean isBlocked = false;
//
//
//
//
//
//
//                            chatRoomsList.add(new ChatRoomModel(
//                                    username,
//                                    jsonObject.getString("other_id"),
//                                    jsonObject.getString("last_message"),
//                                    image,
//                                    false,
//                                    jsonObject.getString("num_msg"),
//                                    jsonObject.getString("id"),
//                                    state,
//                                    numberUnRMessage,
//                                    false,
//                                    jsonObject.getString("user_token")
//                                    ,special_number
//                                    ,lastMessageType
//                                    ,lastMeesageState
//                                    ,lastMeesageTime
//                                    ,false
//                                    ,blockedFor
//
//
//
//                            ));
//                        }
//                        if (isArchived) {
//                            isArchivedMutableLiveData.setValue(isArchived);
//
//                        }

//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    System.out.println(chatRoomsList.size()+"chatRoomsList.size()");
//                    chatRoomListMutableLiveData.setValue(chatRoomsList);
//                },
//
//
//                s -> {System.out.println(s+"ssssssssss");
//            chatRoomListMutableLiveData.setValue(null);});
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
    @SuppressLint("CheckResult")
    public  void deleteChatRoom(String my_id, String your_id){
        Single<String> observable = RetrofitClient.getInstance().getapi().deleteChatRoom(my_id,your_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(s -> {
            System.out.println("responee" + s);

            for(ChatRoomModel chatRoom:chatRoomsList){
            if(chatRoom.getOther_id().equals(your_id)){
                chatRoomsList.remove(chatRoom);
                break;
            }}
        chatRoomListMutableLiveData.setValue(chatRoomsList);


        },s-> {
            System.out.println("Errorrrrrrrr" + s);
        });

    }
    public  void addChatRoom(ChatRoomModel chatRoomModel){
//        System.out.println("addddddddddddddddddddddddddddddddddd");
        chatRoomsList.add( 0,chatRoomModel);
        chatRoomListMutableLiveData.setValue(chatRoomsList);
    }
    public void setLastMessage(String message , String chatId,String senderId, String reciverId,String type,String state,String dateTime,String sender_id){
        boolean inList=false;
        for(ChatRoomModel chatRoom:chatRoomsList){
            if(chatRoom.getId().equals(chatId)){
                System.out.println("typeMessage"+type);
                chatRoom.setLast_message(message);
                chatRoom.setMessage_type(type);
                chatRoom.setMstate(state);
                chatRoom.setCreated_at(dateTime);
                chatRoom.setMsg_sender(sender_id);





                inList=true;

                if(!chatRoom.getInChat()){
                    chatRoom.setNum_msg(String.valueOf(Integer.parseInt(chatRoom.getNum_msg())+1));
                }

                chatRoomsList.remove(chatRoom);
                chatRoomsList.add(0,chatRoom);
                break;
            }
        }


        if(!inList){
            for(ChatRoomModel chatRoom:chatRoomsList){
                if(chatRoom.getId().equals(senderId+reciverId)){
                    chatRoomsList.remove(chatRoom);
                    System.out.println("chatRoom.setLastMessage(message)outtttttttttttttt chatrommmmmmmmmmm");
                    if(!chatRoom.getInChat()){
                        chatRoom.setNum_msg(String.valueOf(Integer.parseInt(chatRoom.getNum_msg())+1));
                    }
                    chatRoom.setId(chatId);
                    System.out.println("removeeeeeeeeeeeee and Addddddddd");
                    chatRoomsList.remove(chatRoom);
                    chatRoomsList.add(0,chatRoom);

                    break;
                }

            }
        }

        chatRoomListMutableLiveData.setValue(chatRoomsList);
    }
    public void updateLastMessageState(String state,String chat_id) {
        for (ChatRoomModel chatRoom : chatRoomsList) {
            if (chatRoom.getId().equals(chat_id)) {
                System.out.println("update State");
                chatRoom.setMstate("3");
                 break;

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
    public void setState(String anthor_user_id,String state) {
        System.out.println("setState");
        for(ChatRoomModel chatRoom:chatRoomsList){
            if(chatRoom.getOther_id().equals(anthor_user_id)){
                System.out.println("chatRoom.getOther_id()"+chatRoom.getOther_id()+" "+anthor_user_id);
                chatRoom.setState(state);

                break;
            }
        }
        chatRoomListMutableLiveData.setValue(chatRoomsList);
    }

    public void setInChat(String user_id,boolean state){
        for(ChatRoomModel chatRoom:chatRoomsList) {
            if (chatRoom.getOther_id().equals(user_id)) {
                chatRoom.setInChat(state);
                if (state) {
                    chatRoom.setNum_msg("0");
                }

                break;
            }
        }






        chatRoomListMutableLiveData.setValue(chatRoomsList);

    }
    public String getChatId(String anthorUserId){
        System.out.println("getChatId"+anthorUserId);
        if(chatRoomsList!=null) {
            for (ChatRoomModel chatRoom : chatRoomsList) {
                if (chatRoom.getOther_id().equals(anthorUserId)) {
                    return chatRoom.getId();
                }


            }
            return "";
        }
        else{
            return "";
        }

    }
    public  boolean checkInChat(String anthor_user_id) {
        if(chatRoomsList==null){
            return false;
        }
        for (ChatRoomModel chatRoom : chatRoomsList) {
            if (chatRoom.getOther_id().equals(anthor_user_id)) {
                return chatRoom.getInChat();


            }
        }

        return  false;
    }
    public  void setTyping(String chat_id,boolean isTyping) {
        for (ChatRoomModel chatRoom : chatRoomsList) {
            if (chatRoom.getId().equals(chat_id)) {
                chatRoom.setTyping(isTyping);
                break;

            }
        }
        chatRoomListMutableLiveData.setValue(chatRoomsList);
    }
    public  void setBlockedState(String anthor_user_id,String blockedFor) {
        System.out.println("setBlockedStateBefore");

        for (ChatRoomModel chatRoom : chatRoomsList) {
            if (chatRoom.getOther_id().equals(anthor_user_id)) {
                chatRoom.setBlocked_for(blockedFor);
                System.out.println("setBlockedState");
                break;

            }
        }
        chatRoomListMutableLiveData.setValue(chatRoomsList);
    }
    public void getChatRoom(String chat_id){
        for (ChatRoomModel chatRoom : chatRoomsList) {
            if (chatRoom.getId().equals(chat_id)) {
                chatRoomModelMutableLiveData.setValue(chatRoom);
                break;


            }
        }
    }
    @SuppressLint("CheckResult")
    public void addToArchived(String my_id, String your_id){
        Single<String> observable = RetrofitClient.getInstance().getapi().addToArchived(my_id,your_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(s -> {
            setState(your_id,my_id);
            System.out.println("add to Archived");
            setArchived(true);



        },s-> {
            System.out.println("Errorrrrrrrr" + s);
        });
    }
    @SuppressLint("CheckResult")
    public void removeFromArchived(String my_id, String your_id){
        Single<String> observable = RetrofitClient.getInstance().getapi().removeFromArchived(my_id,your_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(s -> {
            setState(your_id,"null");



        },s-> {
            System.out.println("Errorrrrrrrr" + s);
        });
    }
}
