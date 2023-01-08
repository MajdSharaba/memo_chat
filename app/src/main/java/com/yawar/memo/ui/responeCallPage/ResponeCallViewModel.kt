package com.yawar.memo.ui.responeCallPage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.PeerConnection
import javax.inject.Inject

@HiltViewModel
class ResponeCallViewModel @Inject constructor(val savedStateHandle: SavedStateHandle) :ViewModel() {


    public var peerConnection: PeerConnection? = null

    var username = ""
    var imageUrl: String? = null
    var anthor_user_id: String? = null


    private val _isVideoForMe =  MutableLiveData<Boolean>(false)
    val isVideoForMe : LiveData<Boolean>
        get() = _isVideoForMe

    private val _backPressClicked =  MutableLiveData<Boolean>(false)
    val backPressClicked : LiveData<Boolean>
        get() = _backPressClicked


    private val _isVideoForYou =  MutableLiveData<Boolean>(false)
    val isVideoForYou : LiveData<Boolean>
        get() = _isVideoForYou


    private val _isAudio =  MutableLiveData<Boolean>(true)
    val isAudio : LiveData<Boolean>
        get() = _isAudio

    private val _isSpeaker =  MutableLiveData<Boolean>(true)
    val isSpeaker : LiveData<Boolean>
        get() = _isSpeaker

    private val _connected =  MutableLiveData<Boolean>(false)
    val connected : LiveData<Boolean>
        get() = _connected

    private var time = 0


    private var callId: String? = null

    init {
        Log.d("TaGGGGGGGGG", savedStateHandle.get<String>("callRequest").toString())
        var message: JSONObject? = null
        val userObject: JSONObject
        val typeObject: JSONObject

        try {
            message = JSONObject(savedStateHandle.get<String>("callRequest"))
            callId = message.getString("call_id")
//            setCallId(callId)

            userObject = JSONObject(message!!.getString("user"))
            typeObject = JSONObject(message!!.getString("type"))
            _isVideoForYou.value = typeObject.getBoolean("video")
            username = userObject.getString("name")
            imageUrl = userObject.getString("image_profile")
            anthor_user_id = message.getString("snd_id")
        } catch (  e: JSONException) {
            e.printStackTrace()
        }
    }


    fun setIsVideoForMe(isVideoForMe: Boolean) {
        _isVideoForMe.value = isVideoForMe
    }
    fun setBackPressClicked(backPressClicked: Boolean) {
        _backPressClicked.value = backPressClicked
    }



    fun setIsVideoForYou(isVideoForYou: Boolean) {
        _isVideoForYou.value = isVideoForYou
    }











    fun setIsSpeaker(isSpeaker: Boolean) {
        _isSpeaker.value  = isSpeaker
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



    fun setCallId( _callId :String) {
        callId = _callId
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

    fun setPeerConection(perConnect: PeerConnection?) {
        peerConnection = perConnect

    }







}