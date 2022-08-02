package com.yawar.memo.modelView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.repositry.ChatMessageRepo;
import com.yawar.memo.repositry.ChatRoomRepo;
import com.yawar.memo.utils.BaseApp;

import java.util.ArrayList;

public class ForwardDialogViewModel extends ViewModel {
    BaseApp baseApp = BaseApp.getInstance();
    private final ChatRoomRepo repository = baseApp.getChatRoomRepo();
    private final ChatMessageRepo chatMessageRepo = baseApp.getChatMessageRepo();





    public MutableLiveData<ArrayList<ChatRoomModel>> chatRoomListMutableLiveData;
    public final ArrayList<ChatRoomModel> chatRoomsList = new ArrayList<>();






    public  MutableLiveData<ArrayList<ChatRoomModel>> loadData() {
        return repository.chatRoomListMutableLiveData;

    }

    public  void clearSelectedMessage() {
         chatMessageRepo.clearSelectedMessage();

    }

}

