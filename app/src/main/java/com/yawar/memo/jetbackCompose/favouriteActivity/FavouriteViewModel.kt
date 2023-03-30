package com.yawar.memo.jetbackCompose.favouriteActivity

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import com.yawar.memo.BaseApp
import com.yawar.memo.domain.model.ChatMessage
import com.yawar.memo.domain.model.ChatRoomModel
import com.yawar.memo.domain.model.SpecialMessageModel
import com.yawar.memo.repositry.ChatMessageRepoo
import com.yawar.memo.repositry.ChatRoomRepoo
import com.yawar.memo.repositry.SpecialMessagesRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.ArrayList
import javax.inject.Inject

///senderId=107, recivedId=118
@HiltViewModel
class FavouriteViewModel @Inject constructor(private val  specialMessagesRepo: SpecialMessagesRepo) : ViewModel() {
//class ChatRoomViewModel: ViewModel() {
private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )
    var baseApp: BaseApp = BaseApp.instance!!
     var user_id  = "107"


    //    private val repository = baseApp.chatRoomRepo
//    private val chatRoomRepoo = baseApp.chatRoomRepoo
init {
        coroutineScope.launch {

            specialMessagesRepo.loadData(BaseApp.instance?.classSharedPreferences?.user?.userId!!)
        }

}
    fun getSpecialMessages(): Flow<List<SpecialMessageModel>> {

        return  specialMessagesRepo.specialMessagesListMutableLiveData.asFlow()
    }

    fun setMessageChecked(message_id: String, isChecked: Boolean){
        coroutineScope.launch {

            specialMessagesRepo.setMessageChecked(message_id, isChecked)
        }
    }

}