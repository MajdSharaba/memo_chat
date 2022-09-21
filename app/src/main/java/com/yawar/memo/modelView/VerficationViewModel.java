package com.yawar.memo.modelView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yawar.memo.model.UserModel;
import com.yawar.memo.repositry.AuthRepo;
import com.yawar.memo.utils.BaseApp;
import org.json.JSONObject;

public class VerficationViewModel extends ViewModel {
    BaseApp baseApp = BaseApp.getInstance();
    AuthRepo authRepo = baseApp.getAuthRepo();

    public MutableLiveData<JSONObject> getSpecialNumber(){
        return authRepo.jsonObjectMutableLiveData;
    }
    public MutableLiveData<Boolean> getLoading(){
        return authRepo.loading;
    }

    public MutableLiveData<Boolean> getErrorMessage(){
        return authRepo.showErrorMessage;
    }
    public void setLoading(Boolean check){
        authRepo.loading.setValue(check);
    }
    public void setErrorMessage(Boolean check){
        authRepo.showErrorMessage.setValue(check);
    }









}
