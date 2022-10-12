package com.yawar.memo.modelView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.repositry.ChatMessageRepoo;
import com.yawar.memo.repositry.ChatRoomRepoo;
import com.yawar.memo.utils.BaseApp;

import java.util.ArrayList;

public class ForwardDialogViewModel extends ViewModel {
    BaseApp baseApp = BaseApp.getInstance();
    private final ChatRoomRepoo repository = baseApp.getChatRoomRepoo();
    private final ChatMessageRepoo chatMessageRepo = baseApp.getChatMessageRepoo();

    public MutableLiveData<ArrayList<ChatRoomModel>> chatRoomListMutableLiveData;
    public final ArrayList<ChatRoomModel> chatRoomsList = new ArrayList<>();

    public LiveData<ArrayList<ChatRoomModel>> loadData() {
        return repository.getChatRoomListMutableLiveData();

    }

    public  void clearSelectedMessage() {
         chatMessageRepo.clearSelectedMessage();

    }

}

