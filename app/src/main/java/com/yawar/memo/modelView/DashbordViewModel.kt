package com.yawar.memo.modelView

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.yawar.memo.model.ChatRoomModel
import com.yawar.memo.utils.BaseApp
import java.util.ArrayList

class DashbordViewModel : ViewModel() {

    var baseApp: BaseApp = BaseApp.getInstance()
    private val chatRoomRepoo = baseApp.chatRoomRepoo

    fun loadData(): LiveData<ArrayList<ChatRoomModel?>?> {
        return chatRoomRepoo.chatRoomListMutableLiveData
    }


}