package com.yawar.memo.ui.forwardPage

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.yawar.memo.model.ChatRoomModel
import com.yawar.memo.utils.BaseApp

class ForwardDialogViewModel : ViewModel() {
    var baseApp = BaseApp.getInstance()
    private val repository = baseApp.chatRoomRepoo
    private val chatMessageRepo = baseApp.chatMessageRepoo


    fun loadData(): LiveData<ArrayList<ChatRoomModel?>?> {
        return repository.chatRoomListMutableLiveData
    }
    fun clearSelectedMessage() {
        chatMessageRepo.clearSelectedMessage()
    }

}