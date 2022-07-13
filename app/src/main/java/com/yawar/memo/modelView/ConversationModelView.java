package com.yawar.memo.modelView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yawar.memo.model.ChatMessage;
import com.yawar.memo.model.ChatRoomModel;
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

    private   ArrayList<ChatMessage> _selectedMessage;
    public MutableLiveData<ArrayList<ChatMessage>> selectedMessage ;
    private   String _state;
    public MutableLiveData<String> state ;



    private   String _isTyping;



    public MutableLiveData<String> isTyping ;

    public ConversationModelView() {
        this._selectedMessage = new ArrayList<>();
        this.selectedMessage = new MutableLiveData<>();
        this.state=new MutableLiveData<>();
        this.isTyping = new MutableLiveData<>();
    }


    public MutableLiveData<ArrayList<ChatMessage>> getChatMessaheHistory() {
            return repository.chatMessageistMutableLiveData;

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
        _selectedMessage.clear();
        selectedMessage.setValue(_selectedMessage);
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
                            deleteMessage(chatMessage);
                            break;
                        }
                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    public void deleteMessage( ChatMessage chatMessage) {
        System.out.println(chatMessage.getMessage()+"chatMesssagefffff");
        repository.deleteMessage(chatMessage);
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

}
