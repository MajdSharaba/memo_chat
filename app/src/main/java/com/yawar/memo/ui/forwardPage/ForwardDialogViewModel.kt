package com.yawar.memo.ui.forwardPage

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.yawar.memo.model.ChatRoomModel
import com.yawar.memo.repositry.ChatMessageRepoo
import com.yawar.memo.repositry.ChatRoomRepoo
//import com.yawar.memo.repositry.chatRoomRepo.ChatRoomRepoImp
import com.yawar.memo.utils.BaseApp
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

//@HiltViewModel
//class ForwardDialogViewModel @Inject constructor(private val repository: ChatRoomRepoImp) : ViewModel() {
@HiltViewModel
class ForwardDialogViewModel@Inject constructor(private val chatMessageRepo:ChatMessageRepoo, private val repository: ChatRoomRepoo) : ViewModel() {

    var baseApp = BaseApp.getInstance()
//    private val repository = baseApp.chatRoomRepoo
//    private val chatMessageRepo = baseApp.chatMessageRepoo


    fun loadData(): LiveData<ArrayList<ChatRoomModel?>?> {
        return repository.chatRoomListMutableLiveData as LiveData<ArrayList<ChatRoomModel?>?>
    }
    fun clearSelectedMessage() {
        chatMessageRepo.clearSelectedMessage()
    }

}