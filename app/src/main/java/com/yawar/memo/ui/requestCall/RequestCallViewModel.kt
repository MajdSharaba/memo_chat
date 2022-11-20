package com.yawar.memo.ui.requestCall

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class RequestCallViewModel : ViewModel() {
    //    RequestCallRepo  requestCallRepo = BaseApp.getInstance().getRequestCallRepo();
    private val _peerIdRecived =  MutableLiveData<String>("no connect")
    val peerIdRecived : LiveData<String>
        get() = _peerIdRecived

    private val _rining =  MutableLiveData<String>("connect")
    val rining : LiveData<String>
        get() = _rining

    private val _isVideoForMe =  MutableLiveData<Boolean>(false)
    val isVideoForMe : LiveData<Boolean>
        get() = _isVideoForMe

    private val _isVideoForYou =  MutableLiveData<Boolean>(false)
    val isVideoForYou : LiveData<Boolean>
        get() = _isVideoForYou

    private val _isAudio =  MutableLiveData<Boolean>(true)
    val isAudio : LiveData<Boolean>
        get() = _isAudio

    private val _endCall =  MutableLiveData<Boolean>(false)
    val endCall : LiveData<Boolean>
        get() = _endCall

    private val _isSpeaker =  MutableLiveData<Boolean>(true)
    val isSpeaker : LiveData<Boolean>
        get() = _isSpeaker

    private val _connected =  MutableLiveData<Boolean>(false)
    val connected : LiveData<Boolean>
        get() = _connected

    private var time = 0


    private var callId: String? = null


//    fun getIsVideoForMe(): LiveData<Boolean> {
//        return _isVideoForMe
//    }

    fun setIsVideoForMe(isVideoForMe: Boolean) {
        _isVideoForMe.value = isVideoForMe
    }


    fun setIsVideoForYou(isVideoForYou: Boolean) {
        _isVideoForYou.value = isVideoForYou
    }

    fun setEndCalll(endCall: Boolean) {
        _endCall.value = endCall
    }


    fun setRining(rining: String) {
        _rining.value = rining
    }



    fun setIsSpeaker(isSpeaker: Boolean) {
        _isSpeaker.value  = isSpeaker
    }

    fun setPeerId(peerId: String) {
        _peerIdRecived.value = peerId
    }



    fun setAudio(audio: Boolean) {
        _isAudio.value = audio
    }



    fun getCallId(): String? {
        return callId
    }

    fun setConnected(connected: Boolean) {
        _connected.postValue(connected)
    }



    fun setCallId() {
        callId = UUID.randomUUID().toString()
    }

    fun setTime(_time: Int) {
        time = _time
    }

    fun getTime(): Int {
        return time
    }
    fun getTimeString(): String {
        val seconds = time % 60
        val minutes = time / 60
        val hour = minutes / 60
        return String.format("%02d:%02d:%02d", hour, minutes, seconds)
    }


}