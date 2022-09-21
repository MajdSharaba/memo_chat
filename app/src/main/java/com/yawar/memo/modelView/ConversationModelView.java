package com.yawar.memo.modelView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yawar.memo.model.ChatMessage;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.repositry.BlockUserRepo;
import com.yawar.memo.repositry.ChatMessageRepo;
import com.yawar.memo.repositry.ChatRoomRepo;
import com.yawar.memo.utils.BaseApp;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Objects;

public class ConversationModelView extends ViewModel {
        BaseApp baseApp = BaseApp.getInstance();
        private final ChatMessageRepo repository = baseApp.getChatMessageRepo();
        private  final BlockUserRepo blockUserRepo = baseApp.getBlockUserRepo();
        private String lastSeen = "null";


    private   String _state;
    public MutableLiveData<String> state ;
    private   String _isTyping;
    public MutableLiveData<String> isTyping ;
    public MutableLiveData<Boolean> isFirst ;





    public ConversationModelView() {

        this.state=new MutableLiveData<>("false");
        this.isTyping = new MutableLiveData<>();
        this.isFirst = new MutableLiveData<>(true);

    }


//
    public void setBlockedFor(String blockedFor) {
        blockUserRepo.setBlockedForRepo(blockedFor);
    }
    public MutableLiveData<String> blockedFor(){
        System.out.println("block for changeeeeeee");
        return blockUserRepo.blockedForRepo;
    }

    public MutableLiveData<Boolean> isBlocked(){
        return blockUserRepo.blockedRepo;
    }
    public void setBlocked(Boolean blocked){
        blockUserRepo.setBlockedRepo(blocked);
    }

    public MutableLiveData<Boolean> isUnBlocked(){
        return blockUserRepo.getUnBlockedRepo();
    }
    public void setUnBlocked(Boolean unBlocked){
        blockUserRepo.setUnBlockedRepo(unBlocked);
    }

    public void sendBlockRequest(String my_id, String another_user_id){
        blockUserRepo.sendBlockRequest(my_id,another_user_id);
    }
    public void sendUnBlockRequest(String my_id, String another_user_id){
        blockUserRepo.sendUnbBlockUser(my_id,another_user_id);
    }


    public MutableLiveData<ArrayList<ChatMessage>> getChatMessaheHistory() {
            return repository.chatMessageistMutableLiveData;

        }



    public void addSelectedMessage(ChatMessage message){
        repository.addSelectedMessage(message);
//        _selectedMessage.add(message);
//        selectedMessage.setValue(_selectedMessage);
    }
    public void removeSelectedMessage(ChatMessage message){
        repository.removeSelectedMessage(message);
//        _selectedMessage.remove(message);
//        selectedMessage.setValue(_selectedMessage);
    }
    public void clearSelectedMessage(){
        repository.clearSelectedMessage();
//        repository.clearMessageChecked();

    }
    public MutableLiveData<ArrayList<ChatMessage>> getSelectedMessage() {
        return repository.selectedMessage;

    }





    public void addMessage(ChatMessage message) {
            repository.addMessage(message);
        }

        public void deleteMessageFromList(JSONArray jsonArray) {
            try {


                for (int i = 0; i < jsonArray.length(); i++) {

                    String message_id = jsonArray.getString(i);
                    for (ChatMessage chatMessage : Objects.requireNonNull(getChatMessaheHistory().getValue())) {
                        if (chatMessage.getId().equals(message_id)) {
                            System.out.println(chatMessage.getId() + " " + message_id);
                            repository.deleteMessage(chatMessage);
                            break;
                        }
                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    public void deleteMessage( ) {
        for (int i = 0; i < getSelectedMessage().getValue().size(); i++) {

            String message_id = getSelectedMessage().getValue().get(i).getId();
            for (ChatMessage chatMessage : Objects.requireNonNull(getChatMessaheHistory().getValue())) {
                if (chatMessage.getId().equals(message_id)) {
                    System.out.println(chatMessage.getId() + " " + message_id);
                    repository.deleteMessage(chatMessage);
                    break;
                }
            }


        }
    }
    public void ubdateMessage( String messge_id,String message) {
        repository.UpdateMessage(messge_id,message);
    }
    public void setMessageChecked( String messge_id,boolean isChecked) {
        repository.setMessageChecked(messge_id,isChecked);
    }
    public void setMessageState( String messge_id,String messageState) {
        repository.setMessageState(messge_id,messageState);
    }



    public void set_state(String _state) {
        System.out.println("stateTooles"+_state);
        this._state = _state;
        state.setValue(_state);
    }
    public void set_isTyping(String _isTyping) {
        this._isTyping = _isTyping;
        isTyping.setValue(_isTyping);
    }

    public void setMessageDownload(String message_id,boolean isDwonload) {
        repository.setMessageDownload(message_id,isDwonload);
    }

    public void deleteMessageForMe(ArrayList<String> message_id,String user_id) {

         repository.deleteMessageForMe(message_id.toString(),user_id,getSelectedMessage().getValue());




    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public MutableLiveData<Boolean> getLoading(){
        return repository.loading;
    }

    public MutableLiveData<Boolean> getErrorMessage(){
        return repository.showErrorMessage;
    }
    public void setLoading(Boolean check){
        repository.loading.setValue(check);
    }
    public void setErrorMessage(Boolean check){
        repository.showErrorMessage.setValue(check);
    }


}
