package com.yawar.memo.ui.AcrchivedPage

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.yawar.memo.model.ChatRoomModel
import com.yawar.memo.repositry.ChatRoomRepoo
import com.yawar.memo.utils.BaseApp
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject
@HiltViewModel
class ArchivedActViewModel @Inject constructor(private val chatRoomRepoo: ChatRoomRepoo) : ViewModel() {
//class ArchivedActViewModel  : ViewModel() {

    var baseApp: BaseApp = BaseApp.getInstance()
//    private val repository = baseApp.chatRoomRepo
//    private val chatRoomRepoo = baseApp.chatRoomRepoo

    fun loadData(): LiveData<ArrayList<ChatRoomModel?>?> {
        return chatRoomRepoo.chatRoomListMutableLiveData

    }
    fun removeFromArchived(my_id: String, your_id: String) {
        return chatRoomRepoo.removeFromArchived(my_id, your_id)

//        repository.removeFromArchived(my_id, your_id)
    }

    fun setArchived(state: Boolean) {
        chatRoomRepoo.setisArchived( state)
    }
    fun getIsArchived(): LiveData<Boolean>{
        return chatRoomRepoo.isArchivedMutableLiveData
    }
}