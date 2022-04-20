package com.yawar.memo.modelView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.repositry.BlockUserRepo;
import com.yawar.memo.repositry.ChatRoomRepo;
import com.yawar.memo.utils.BaseApp;

import java.util.ArrayList;

public class BlockedActViewModel extends ViewModel {
    BaseApp baseApp = BaseApp.getInstance();
    private final BlockUserRepo repository = baseApp.getBlockUserRepo();


    public MutableLiveData<ArrayList<UserModel>> blockUserListMutableLiveData;
    public final ArrayList<UserModel> blockUserList = new ArrayList<>();


//    public IntroActModelView(@NonNull Application application) {
//        super(application);
//        repository = new ChatRoomRepo(application);
//        baseApp = BaseApp.getInstance();
//        chatRoomsList = new ArrayList<>();
//        chatRoomListMutableLiveData = new MutableLiveData<>();
//
//    }




    public  MutableLiveData<ArrayList<UserModel>> loadData() {
        return repository.userBlockListMutableLiveData;
//        System.out.println(chatRoomListMutableLiveData.getValue().size()+"chatRoomListMutableLiveData.getValue().size()");

    }
}
