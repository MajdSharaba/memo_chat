package com.yawar.memo.modelView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class ResponeCallViewModel extends ViewModel {

    public MutableLiveData<Boolean> isVideoForMe ;


    public MutableLiveData<Boolean> isVideoForYou ;
    public MutableLiveData<Boolean> isAudio ;
    public MutableLiveData<Boolean> isSpeaker;
    private int time = 0;
    private String callId;





    public ResponeCallViewModel() {
        this.isVideoForMe = new MutableLiveData<>(false);
        this.isVideoForYou=new MutableLiveData<>(false);
        this.isAudio = new MutableLiveData<>(true);
        this.isSpeaker = new MutableLiveData<>(true);



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
    public MutableLiveData<Boolean> getIsSpeaker() {
        return isSpeaker;
    }

    public void setIsSpeaker(boolean isSpeaker) {
        this.isSpeaker.setValue(isSpeaker);
    }


    public String getCallId( ) {
        return this.callId;
    }

    public void setCallId(String callId) {

        this.callId = callId;
    }
    public  void setTime (int time){
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public  String getTimeString(){
        int seconds = this.time % 60;
        int minutes = this.time / 60;
        int hour = minutes/60;
        String stringTime = String.format("%02d:%02d:%02d",hour, minutes, seconds);
        return  stringTime;
    }


}
