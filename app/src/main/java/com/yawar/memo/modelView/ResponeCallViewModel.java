package com.yawar.memo.modelView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class ResponeCallViewModel extends ViewModel {

    public MutableLiveData<Boolean> isVideoForMe ;

    public MutableLiveData<Boolean> isVideoForYou ;

    public ResponeCallViewModel() {
        this.isVideoForMe = new MutableLiveData<>(false);
        this.isVideoForYou=new MutableLiveData<>(false);


    }

    public MutableLiveData<Boolean> getIsVideoForMe() {
        return isVideoForMe;
    }



    public MutableLiveData<Boolean> getIsVideoForYou() {
        return isVideoForYou;
    }

    public void setIsVideoForYou( boolean isVideoForYou) {
        this.isVideoForYou.setValue(isVideoForYou); ;
    }


}
