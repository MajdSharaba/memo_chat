package com.yawar.memo.ui.introPage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yawar.memo.model.ChatRoomModel
import com.yawar.memo.utils.BaseApp
import java.util.*

class IntroActModelView : ViewModel() {
    var baseApp = BaseApp.getInstance()
//    private val repository = baseApp.chatRoomRepo
    var chatRoomRepoo = baseApp.chatRoomRepoo
    var chatRoomListMutableLiveData = MutableLiveData<ArrayList<ChatRoomModel>>()
    init {
//        if(chatRoomRepoo.chatRoomListMutableLiveData == null){
//            Log.d("chatRoomTag", ": is null")
        chatRoomRepoo.loadChatRoom(baseApp.classSharedPreferences.user.userId!!)}
//    }
    fun loadData(): LiveData<ArrayList<ChatRoomModel?>?> {
        return chatRoomRepoo.chatRoomListMutableLiveData
    }


    fun setLoading(check: Boolean) {
        chatRoomRepoo.setLoading(check)
    }

    fun setErrorMessage(check: Boolean) {
        chatRoomRepoo.setErrorMessage(check)
    }
    fun  getLoading() : LiveData<Boolean>{
        return chatRoomRepoo.loadingMutableLiveData;
    }
    fun  getErrorMessage() : LiveData<Boolean>{
        return chatRoomRepoo.showErrorMessage;
    }





}