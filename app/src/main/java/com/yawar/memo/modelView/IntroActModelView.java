package com.yawar.memo.modelView;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;


import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
        import androidx.lifecycle.AndroidViewModel;
        import androidx.lifecycle.LiveData;
        import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.repositry.BlockUserRepo;
import com.yawar.memo.repositry.ChatRoomRepo;
import com.yawar.memo.retrofit.RetrofitClient;
import com.yawar.memo.utils.BaseApp;

import org.json.JSONObject;

import java.util.ArrayList;
        import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class IntroActModelView extends ViewModel {

    BaseApp baseApp = BaseApp.getInstance();
    private final ChatRoomRepo repository = baseApp.getChatRoomRepo();
    private final BlockUserRepo blockUserRepo = baseApp.getBlockUserRepo();



    public  MutableLiveData<ArrayList<ChatRoomModel>> chatRoomListMutableLiveData= new MutableLiveData<>();
    public final ArrayList<ChatRoomModel> chatRoomsList = new ArrayList<>();
    public final MutableLiveData<ArrayList<UserModel>> userBlockListMutableLiveData= new MutableLiveData<>();


    public IntroActModelView() {
        repository.callAPI(baseApp.getClassSharedPreferences().getUser().getUserId());


    }





    public  MutableLiveData<ArrayList<ChatRoomModel>> loadData() {
//        chatRoomListMutableLiveData.setValue((ArrayList<ChatRoomModel>) repository.getChatRoomModelList());
        return repository.getChatRoomListMutableLiveData();
//        System.out.println(chatRoomListMutableLiveData.getValue().size()+"chatRoomListMutableLiveData.getValue().size()");

    }
    public MutableLiveData<ArrayList<UserModel>> getUserBlock (){

//        userBlockListMutableLiveData.setValue( blockUserRepo.getUserBlockList());
        return blockUserRepo.getUserBlockList();


    }
    @SuppressLint("CheckResult")
    public void sendFcmToken(String user_id, String token){
        Single<String> observable = RetrofitClient.getInstance(AllConstants.base_node_url).getapi().sendFcmToken(user_id,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(s -> {


        },s-> {
            System.out.println("Errorrrrrrrr" + s);
          });
    }

    public MutableLiveData<Boolean> getLoading(){
        return repository.loading;
    }

    public MutableLiveData<Boolean> getErrorMessage(){
        return repository.showErrorMessage;
    }
    public void setLoading(Boolean check){
        repository.loading.setValue(check);
    }
    public void setErrorMessage(Boolean check){
        repository.showErrorMessage.setValue(check);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}