package com.yawar.memo.modelView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yawar.memo.repositry.RequestCallRepo;
import com.yawar.memo.utils.BaseApp;

public class RequestCallViewModel extends ViewModel {
//    RequestCallRepo  requestCallRepo = BaseApp.getInstance().getRequestCallRepo();

    public MutableLiveData<String> peerIdRecived;
    //    private   Boolean _isVideoForMe;
    public MutableLiveData<Boolean> isVideoForMe;
//    private   Boolean _isVideoForYou;
    public MutableLiveData<Boolean> isVideoForYou;
//    private   Boolean _endCall;
    public MutableLiveData<Boolean> endCall;
//    private   Boolean _rining;
    public MutableLiveData<String> rining;

    public MutableLiveData<Boolean> isAudio;



    public MutableLiveData<Boolean> isSpeaker;



    public RequestCallViewModel() {
        this.isVideoForMe = new MutableLiveData<>(false);
        this.isVideoForYou=new MutableLiveData<>(false);
        this.endCall = new MutableLiveData<>(false);
        this.rining = new MutableLiveData<>("connect");
        this.peerIdRecived = new MutableLiveData<>("no connect");
        this.isAudio = new MutableLiveData<>(true);
        this.isSpeaker = new MutableLiveData<>(true);


    }

    public MutableLiveData<Boolean> getIsVideoForMe() {
        return this.isVideoForMe;
    }

    public void setIsVideoForMe(boolean isVideoForMe) {
        this.isVideoForMe.setValue(isVideoForMe);
    }

    public MutableLiveData<Boolean> getIsVideoForYou() {
        return this.isVideoForYou;
    }

    public void setIsVideoForYou( boolean isVideoForYou) {
        this.isVideoForYou.setValue(isVideoForYou); ;
    }

    public MutableLiveData<Boolean> getEndCall() {
        return this.endCall;
    }

    public void setEndCall(boolean endCall) {
        this.endCall.setValue( endCall);
    }

    public MutableLiveData<String> getRining() {
        return this.rining;
    }

    public void setRining(String rining) {
        this.rining.setValue(rining);
    }

    public MutableLiveData<Boolean> getIsSpeaker() {
        return this.isSpeaker;
    }

    public void setIsSpeaker(boolean isSpeaker) {
        this.isSpeaker.setValue(isSpeaker);
    }
    public void setPeerId(String peerId) {
        this.peerIdRecived.setValue(peerId);
    }
    public MutableLiveData<String> getPeerId( ) {
       return this.peerIdRecived;
    }
    public void setAudio(Boolean audio) {
        this.isAudio.setValue(audio);
    }
    public MutableLiveData<Boolean> getAudio( ) {
        return this.isAudio;
    }


}
