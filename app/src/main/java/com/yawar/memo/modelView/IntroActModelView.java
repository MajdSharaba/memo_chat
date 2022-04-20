package com.yawar.memo.modelView;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;


import android.app.Application;

        import androidx.annotation.NonNull;
        import androidx.lifecycle.AndroidViewModel;
        import androidx.lifecycle.LiveData;
        import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.repositry.BlockUserRepo;
import com.yawar.memo.repositry.ChatRoomRepo;
import com.yawar.memo.utils.BaseApp;

import java.util.ArrayList;
        import java.util.List;

public class IntroActModelView extends ViewModel {

    BaseApp baseApp = BaseApp.getInstance();
    private final ChatRoomRepo repository = baseApp.getChatRoomRepo();
    private final BlockUserRepo blockUserRepo = baseApp.getBlockUserRepo();



    public  MutableLiveData<ArrayList<ChatRoomModel>> chatRoomListMutableLiveData= new MutableLiveData<>();
    public final ArrayList<ChatRoomModel> chatRoomsList = new ArrayList<>();
    public final MutableLiveData<ArrayList<UserModel>> userBlockListMutableLiveData= new MutableLiveData<>();;




//    public IntroActModelView(@NonNull Application application) {
//        super(application);
//        repository = new ChatRoomRepo(application);
//        baseApp = BaseApp.getInstance();
//        chatRoomsList = new ArrayList<>();
//        chatRoomListMutableLiveData = new MutableLiveData<>();
//
//    }




    public  MutableLiveData<ArrayList<ChatRoomModel>> loadData() {
//        chatRoomListMutableLiveData.setValue((ArrayList<ChatRoomModel>) repository.getChatRoomModelList());
        return repository.getChatRoomListMutableLiveData();
//        System.out.println(chatRoomListMutableLiveData.getValue().size()+"chatRoomListMutableLiveData.getValue().size()");

    }
    public MutableLiveData<ArrayList<UserModel>> getUserBlock (){

//        userBlockListMutableLiveData.setValue( blockUserRepo.getUserBlockList());
        return blockUserRepo.getUserBlockList();


    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}