package com.yawar.memo.ui.introPage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yawar.memo.model.ChatRoomModel
import com.yawar.memo.repositry.ChatRoomRepoo
import com.yawar.memo.utils.BaseApp
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject
@HiltViewModel
 class IntroActModelView @Inject  constructor( val chatRoomRepoo: ChatRoomRepoo) : ViewModel() {
//class IntroActModelView : ViewModel() {

    var baseApp = BaseApp.getInstance()
//    private val repository = baseApp.chatRoomRepo
//    var chatRoomRepoo = baseApp.chatRoomRepoo
    var chatRoomListMutableLiveData = MutableLiveData<ArrayList<ChatRoomModel>>()
    init {
        Log.d("IntroActModelView", chatRoomRepoo.chatRoomListMutableLiveData.toString())
            if(chatRoomRepoo.chatRoomListMutableLiveData.value == null) {
                Log.d("IntroActModelView2", chatRoomRepoo.chatRoomListMutableLiveData.toString())

                    chatRoomRepoo.loadChatRoom(baseApp.classSharedPreferences.user.userId!!)

            }
    }
    fun loadData(): LiveData<ArrayList<ChatRoomModel?>?> {
        return chatRoomRepoo.chatRoomListMutableLiveData
    }


    fun setLoading(check: Boolean) {
        chatRoomRepoo.setLoading(check)
    }

    fun setErrorMessage(check: Boolean) {
        chatRoomRepoo.setErrorMessage(check)
    }

    fun getLoading(): LiveData<Boolean> {
        return chatRoomRepoo.loadingMutableLiveData;
    }

    fun getErrorMessage(): LiveData<Boolean> {
        return chatRoomRepoo.showErrorMessage;
    }


}