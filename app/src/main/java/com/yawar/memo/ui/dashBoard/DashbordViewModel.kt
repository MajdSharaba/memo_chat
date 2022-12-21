package com.yawar.memo.ui.dashBoard

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.yawar.memo.model.ChatRoomModel
//import com.yawar.memo.repositry.chatRoomRepo.ChatRoomRepoImp
import com.yawar.memo.utils.BaseApp
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.ArrayList
import javax.inject.Inject
//
//@HiltViewModel
//class DashbordViewModel @Inject constructor(private val chatRoomRepoo: ChatRoomRepoImp) : ViewModel() {
class DashbordViewModel : ViewModel() {


    var baseApp: BaseApp = BaseApp.getInstance()
    private val chatRoomRepoo = baseApp.chatRoomRepoo

    fun loadData(): LiveData<ArrayList<ChatRoomModel?>?> {
        return chatRoomRepoo.chatRoomListMutableLiveData
    }


}