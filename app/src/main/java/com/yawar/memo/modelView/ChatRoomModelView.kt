package com.yawar.memo.modelView

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yawar.memo.model.ChatRoomModel
import com.yawar.memo.utils.BaseApp
import kotlinx.coroutines.*
import java.util.*

class ChatRoomViewModel : ViewModel() {
    var baseApp: BaseApp = BaseApp.getInstance()
//    private val repository = baseApp.chatRoomRepo
    private val chatRoomRepoo = baseApp.chatRoomRepoo

    fun loadData(): LiveData<ArrayList<ChatRoomModel?>?> {
       return chatRoomRepoo.chatRoomListMutableLiveData
    }

    fun addToArchived(my_id: String, anthor_user_id: String) {
        chatRoomRepoo.addToArchived(my_id, anthor_user_id)
    }

    fun deleteChatRoom(my_id: String, anthor_user_id: String) {
        chatRoomRepoo.deleteChatRoom(my_id, anthor_user_id)
    }
    fun setArchived(state: Boolean) {
        chatRoomRepoo.setisArchived(state)
    }
    fun getIsArchived(): LiveData<Boolean>{
        return chatRoomRepoo.isArchivedMutableLiveData
    }
}