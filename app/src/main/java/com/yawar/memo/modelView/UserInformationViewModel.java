package com.yawar.memo.modelView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.repositry.ChatRoomRepo;
import com.yawar.memo.repositry.UserInformationRepo;
import com.yawar.memo.utils.BaseApp;

import java.util.ArrayList;


public class UserInformationViewModel extends ViewModel {
    BaseApp baseApp = BaseApp.getInstance();
    private final UserInformationRepo repository = baseApp.getUserInformationRepo();




    public  MutableLiveData<Boolean>  isBlocked;


    public UserInformationViewModel() {
        isBlocked = new MutableLiveData<>(null);

    }

    public void setBlockedFor(String blockedFor) {
        repository.setBlockedFor(blockedFor);
    }
    public MutableLiveData<String> blockedFor(){
        return repository.blockedFor;
    }

    public MutableLiveData<Boolean> isBlocked(){
        return repository.blocked;
    }
    public void setBlocked(Boolean blocked){
        repository.setBlocked(blocked);
    }

    public MutableLiveData<Boolean> isUnBlocked(){
        return repository.getUnBlocked();
    }
    public void setUnBlocked(Boolean unBlocked){
        repository.setUnBlocked(unBlocked);
    }

    public void sendBlockRequest(String my_id, String another_user_id){
        repository.sendBlockRequest(my_id,another_user_id);
    }
    public void sendUnBlockRequest(String my_id, String another_user_id){
        repository.sendUnbBlockUser(my_id,another_user_id);
    }






}