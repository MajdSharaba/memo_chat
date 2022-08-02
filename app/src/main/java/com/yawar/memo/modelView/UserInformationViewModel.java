package com.yawar.memo.modelView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.model.MediaModel;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.repositry.BlockUserRepo;
import com.yawar.memo.repositry.ChatRoomRepo;
import com.yawar.memo.repositry.UserInformationRepo;
import com.yawar.memo.utils.BaseApp;

import java.util.ArrayList;


public class UserInformationViewModel extends ViewModel {
    BaseApp baseApp = BaseApp.getInstance();
    private final UserInformationRepo repository = baseApp.getUserInformationRepo();
    private  final BlockUserRepo blockUserRepo = baseApp.getBlockUserRepo();





    public  MutableLiveData<Boolean>  isBlocked;


    public UserInformationViewModel() {
        isBlocked = new MutableLiveData<>(null);

    }

    public void setBlockedFor(String blockedFor) {
        blockUserRepo.setBlockedForRepo(blockedFor);
    }
    public MutableLiveData<String> blockedFor(){
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
    public void mediaRequest (String user_id , String anthor_user_id){
        repository.getMedia(user_id, anthor_user_id);
    }
    public  MutableLiveData<ArrayList<MediaModel>> getMedia(){
        return  repository.mediaModelsMutableLiveData;
    }







}