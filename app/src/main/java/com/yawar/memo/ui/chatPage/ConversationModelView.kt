package com.yawar.memo.ui.chatPage
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.yawar.memo.BaseApp
import com.yawar.memo.domain.model.ChatMessage
import com.yawar.memo.repositry.BlockUserRepo
import com.yawar.memo.repositry.ChatMessageRepoo
import dagger.hilt.android.lifecycle.HiltViewModel
import org.json.JSONArray
import org.json.JSONException
import java.util.*
import javax.inject.Inject

@HiltViewModel
//class ConversationModelView(anthorUesrId: String, blockedForState: String) : ViewModel() {
class ConversationModelView @Inject constructor (val chatMessageRepoo : ChatMessageRepoo,
                                                  val blockUserRepo :BlockUserRepo,
                                                   val savedStateHandle: SavedStateHandle) : ViewModel() {

    var baseApp = BaseApp.instance
//    private val chatMessageRepoo = baseApp.chatMessageRepoo
    var lastSeen = "null"




    private val _state =  MutableLiveData<String>("false")
    val state : LiveData<String>
        get() = _state



    private val _isFirst =  MutableLiveData<Boolean>(true)
    val isFirst : LiveData<Boolean>
        get() = _isFirst

    private val _isTyping =  MutableLiveData<String>()
    val isTyping : LiveData<String>
        get() = _isTyping
init {
    chatMessageRepoo?.loadChatRoom(BaseApp.instance?.classSharedPreferences?.user?.userId, savedStateHandle.get<String>("reciver_id").toString())
        blockUserRepo.setBlockedForRepo(savedStateHandle.get<String>("blockedFor").toString())
    Log.d("ConversationModelView", BaseApp.instance?.classSharedPreferences?.user?.userId.toString()+"   "+savedStateHandle.get<String>("reciver_id").toString())



}



    //
    fun setBlockedFor(blockedFor: String?) {
        if (blockedFor != null) {
            blockUserRepo.setBlockedForRepo(blockedFor)
        }
    }

    fun blockedFor(): LiveData<String> {
        return blockUserRepo.blockedForRepo
    }

    val isBlocked: LiveData<Boolean>
        get() = blockUserRepo.isBlocked

    fun setBlocked(blocked: Boolean) {
        blockUserRepo.setBlockedRepo(blocked)
    }

    val isUnBlocked: LiveData<Boolean>
        get() = blockUserRepo.isUnBlocked

    fun setUnBlocked(unBlocked: Boolean) {
        blockUserRepo.setUnBlockedRepo(unBlocked)
    }

    fun sendBlockRequest(my_id: String, another_user_id: String) {
        blockUserRepo.sendBlockRequest(my_id, another_user_id)
    }

    fun sendUnBlockRequest(my_id: String, another_user_id: String) {
        blockUserRepo.sendUnbBlockUser(my_id, another_user_id)
    }
    fun getChatMessaheHistory(): LiveData<ArrayList<ChatMessage?>?> {
//            return repository.chatMessageistMutableLiveData;
        return chatMessageRepoo.chatMessaheHistory
    }

    fun addSelectedMessage(message: ChatMessage?) {
        chatMessageRepoo.addSelectedMessage(message)
        //        _selectedMessage.add(message);
//        selectedMessage.setValue(_selectedMessage);
    }

    fun removeSelectedMessage(message: ChatMessage?) {
        chatMessageRepoo.removeSelectedMessage(message!!)
        //        _selectedMessage.remove(message);
//        selectedMessage.setValue(_selectedMessage);
    }

    fun clearSelectedMessage() {

        chatMessageRepoo.clearSelectedMessage()
        //        repository.clearMessageChecked();
    }

    val selectedMessage: LiveData<ArrayList<ChatMessage?>?>
        get() = chatMessageRepoo.selectedMessage

    fun addMessage(message: ChatMessage?) {
        chatMessageRepoo.addMessage(message!!)
    }

    fun deleteMessageFromList(jsonArray: JSONArray) {
        try {
            var chatMessageList = chatMessageRepoo.chatMessaheHistory.value

            for (i in 0 .. jsonArray.length()) {

                val message_id = jsonArray.getString(i)
                if (chatMessageList != null) {
                    for (chatMessage in chatMessageList) {
                        if (chatMessage != null) {
                            if (chatMessage.id == message_id) {
                                chatMessageRepoo.deleteMessage(chatMessage)
                                break
                            }
                        }
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

//    fun deleteMessage() {
//        for (i in selectedMessage.value.indices) {
//            val message_id = selectedMessage.value!![i]!!.id
//            for (chatMessage in Objects.requireNonNull(chatMessaheHistory.getValue())) {
//                if (chatMessage.id == message_id) {
//                    println(chatMessage.id + " " + message_id)
//                    chatMessageRepoo.deleteMessage(chatMessage)
//                    break
//                }
//            }
//        }
//    }

    fun ubdateMessage(messge_id: String?, message: String?) {
        chatMessageRepoo.UpdateMessage(messge_id!!, message)
    }

    fun setMessageChecked(messge_id: String?, isChecked: Boolean) {
        chatMessageRepoo.setMessageChecked(messge_id!!, isChecked)
    }

    fun setMessageState(messge_id: String?, messageState: String?) {
        chatMessageRepoo.setMessageState(messge_id!!, messageState!!)
    }

    fun set_state(state: String) {
        _state.value = state
    }

    fun set_isTyping(isTyping: String) {
        _isTyping.value = isTyping
    }

    fun setMessageDownload(message_id: String?, isDwonload: Boolean) {
        chatMessageRepoo.setMessageDownload(message_id!!, isDwonload)
    }

    fun deleteMessageForMe(message_id: ArrayList<String?>, user_id: String?) {
        chatMessageRepoo.deleteMessageForMe(message_id.toString(),
            user_id,
            selectedMessage.value)
    }

    fun setLoading(value: Boolean) {
        chatMessageRepoo.setLoading(value)
    }

    fun setErrorMessage(value: Boolean) {
        chatMessageRepoo.setErrorMessage(value)
    }
    fun setState(value: String) {
       _state.value = value
    }
    fun setIsFirst(value: Boolean) {
        _isFirst.value = value
    }
    fun setTyping(value: String) {
        _isTyping.value = value
    }

    fun  getLoading() :LiveData<Boolean>{
        return chatMessageRepoo.loading;
    }
    fun  getErrorMessage() :LiveData<Boolean>{
        return chatMessageRepoo.errorMessage;
    }


}
