package com.yawar.memo.ui.AcrchivedPage

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.yawar.memo.BaseApp
import com.yawar.memo.domain.model.ChatRoomModel
import com.yawar.memo.repositry.ChatRoomRepoo
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject
@HiltViewModel
class ArchivedActViewModel @Inject constructor(private val chatRoomRepoo: ChatRoomRepoo) : ViewModel() {
//class ArchivedActViewModel  : ViewModel() {

    var baseApp: BaseApp = BaseApp.instance!!
//    private val repository = baseApp.chatRoomRepo
//    private val chatRoomRepoo = baseApp.chatRoomRepoo

    fun loadData(): LiveData<ArrayList<ChatRoomModel?>?> {
        return chatRoomRepoo.chatRoomListMutableLiveData as  LiveData<ArrayList<ChatRoomModel?>?>
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