package com.yawar.memo.modelView;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.repositry.ChatRoomRepo;
import com.yawar.memo.utils.BaseApp;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomViewModel extends ViewModel {

    BaseApp baseApp = BaseApp.getInstance();
    private final ChatRoomRepo repository = baseApp.getChatRoomRepo()  ;


    public  MutableLiveData<ArrayList<ChatRoomModel>> chatRoomListMutableLiveData;
    public final ArrayList<ChatRoomModel> chatRoomsList = new ArrayList<>();

    public ChatRoomViewModel() {
    }


//    public IntroActModelView(@NonNull Application application) {
//        super(application);
//        repository = new ChatRoomRepo(application);
//        baseApp = BaseApp.getInstance();
//        chatRoomsList = new ArrayList<>();
//        chatRoomListMutableLiveData = new MutableLiveData<>();
//
//    }




    public  MutableLiveData<ArrayList<ChatRoomModel>> loadData() {
        return repository.chatRoomListMutableLiveData;
//        System.out.println(chatRoomListMutableLiveData.getValue().size()+"chatRoomListMutableLiveData.getValue().size()");

    }
    public  void addToArchived(String my_id,String anthor_user_id){
        repository.addToArchived(my_id,anthor_user_id);
    }
    public  void deleteChatRoom(String my_id,String anthor_user_id){
        repository.deleteChatRoom(my_id,anthor_user_id);
    }


}
