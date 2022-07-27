package com.yawar.memo.modelView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RequestCallViewModel extends ViewModel {

    public MutableLiveData<String> peerIdRecived;
    //    private   Boolean _isVideoForMe;
    public MutableLiveData<Boolean> isVideoForMe ;
//    private   Boolean _isVideoForYou;
    public MutableLiveData<Boolean> isVideoForYou ;
//    private   Boolean _endCall;
    public MutableLiveData<Boolean> endCall ;
//    private   Boolean _rining;
    public MutableLiveData<Boolean> rining ;

    public RequestCallViewModel() {
        this.isVideoForMe = new MutableLiveData<>(false);
        this.isVideoForYou=new MutableLiveData<>(false);
        this.endCall = new MutableLiveData<>(false);
        this.rining = new MutableLiveData<>(false);
        this.peerIdRecived = new MutableLiveData<>("no connect");


    }

    public MutableLiveData<Boolean> getIsVideoForMe() {
        return isVideoForMe;
    }

    public void setIsVideoForMe(MutableLiveData<Boolean> isVideoForMe) {
        this.isVideoForMe = isVideoForMe;
    }

    public MutableLiveData<Boolean> getIsVideoForYou() {
        return isVideoForYou;
    }

    public void setIsVideoForYou( boolean isVideoForYou) {
        this.isVideoForYou.setValue(isVideoForYou); ;
    }

    public MutableLiveData<Boolean> getEndCall() {
        return endCall;
    }

    public void setEndCall(boolean endCall) {
        this.endCall.setValue( endCall);
    }

    public MutableLiveData<Boolean> getRining() {
        return rining;
    }

    public void setRining(boolean rining) {
        this.rining.setValue( rining);
    }
}
