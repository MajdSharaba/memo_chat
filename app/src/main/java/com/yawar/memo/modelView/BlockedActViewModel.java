package com.yawar.memo.modelView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yawar.memo.model.UserModel;
import com.yawar.memo.repositry.BlockUserRepo;
import com.yawar.memo.utils.BaseApp;

import java.util.ArrayList;

public class BlockedActViewModel extends ViewModel {
    BaseApp baseApp = BaseApp.getInstance();
//    private final ChatRoomRepo repository = baseApp.getChatRoomRepo();
    private  final BlockUserRepo blockUserRepo = baseApp.getBlockUserRepo();



    public MutableLiveData<ArrayList<UserModel>> blockUserListMutableLiveData;
    public final ArrayList<UserModel> blockUserList = new ArrayList<>();


    public BlockedActViewModel() {
        blockUserRepo.getUserBlock(baseApp.getClassSharedPreferences().getUser().getUserId());

    }


    public void setBlockedFor(String blockedFor) {
        blockUserRepo.setBlockedForRepo(blockedFor);
    }
    public LiveData<String> blockedFor(){
//        return blockUserRepo.blockedForRepo;
        return blockUserRepo.getBlockedForRepo();

    }

    public LiveData<Boolean> isBlocked(){
        return blockUserRepo.getBlockedRepo();
    }
    public void setBlocked(Boolean blocked){
        blockUserRepo.setBlockedRepo(blocked);
    }

    public LiveData<Boolean> isUnBlocked(){
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

    public  LiveData<ArrayList<UserModel>> loadData() {
        return blockUserRepo.getUserBlockListMutableLiveData();
//        System.out.println(chatRoomListMutableLiveData.getValue().size()+"chatRoomListMutableLiveData.getValue().size()");

    }
}
