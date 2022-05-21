package com.yawar.memo.modelView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.repositry.ChatRoomRepo;
import com.yawar.memo.utils.BaseApp;

import java.util.ArrayList;

public class ArchivedActViewModel extends ViewModel {
    BaseApp baseApp = BaseApp.getInstance();
    private final ChatRoomRepo repository = baseApp.getChatRoomRepo() ;

    public MutableLiveData<ArrayList<ChatRoomModel>> loadData() {
        return repository.chatRoomListMutableLiveData;

    }
}
