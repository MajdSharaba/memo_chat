package com.yawar.memo.repositry

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yawar.memo.Api.GdgApi
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.model.ChatRoomModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*


class ChatRoomRepoo {
    private val TAG: String = "ChatRoomRepoo"
    private val _chatRoomListMutableLiveData = MutableLiveData<ArrayList<ChatRoomModel?>?>()
    val chatRoomListMutableLiveData: LiveData<ArrayList<ChatRoomModel?>?>
        get() = _chatRoomListMutableLiveData


    private val _isArchivedMutableLiveData = MutableLiveData<Boolean>(false)
    val isArchivedMutableLiveData: LiveData<Boolean>
        get() = _isArchivedMutableLiveData


    private val _loadingMutableLiveData =  MutableLiveData<Boolean>()
    val loadingMutableLiveData : LiveData<Boolean>
        get() = _loadingMutableLiveData


    private val _showErrorMessage =  MutableLiveData<Boolean>(false)
    val showErrorMessage : LiveData<Boolean>
        get() = _showErrorMessage

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )
     fun loadChatRoom(user_id : String) {
         _loadingMutableLiveData.value = true
        coroutineScope.launch {

            val getChatRoomsDeferred =   GdgApi(AllConstants.base_url).apiService
                .getChatRoom(user_id)
//            val getChatRoomsDeferred =   GdgApi.apiService
//                .getChatRoom(user_id)


            try {
                val listResult = getChatRoomsDeferred.await()
                _loadingMutableLiveData.value = false
                _showErrorMessage.value = false

                _chatRoomListMutableLiveData.value = listResult.data
                Log.d("chatRoomTag: ","Success: ${chatRoomListMutableLiveData.value} Mars properties retrieved")
            } catch (e: Exception) {
                _loadingMutableLiveData.value = false
                _showErrorMessage.value = true


                Log.d("chatRoomTag: ","Failure: ${e.message}")

            }
        }
    }
     fun addToArchived(my_id : String, your_id :String) {
        coroutineScope.launch {

            var srchivedDeferred =  GdgApi(AllConstants.base_node_url).apiService
                .addToArchived(my_id , your_id)
//            var srchivedDeferred =  GdgApi.apiService
//                .addToArchived(my_id , your_id)


            try {
                val listResult = srchivedDeferred.await()
                setState(your_id, my_id)
                Log.d(TAG,"Success: $listResult Mars properties retrieved")
            } catch (e: Exception) {
                Log.d(TAG,"Failure: ${e.message}")

            }
        }
    }

    /// state null for remove from Archived
     fun removeFromArchived(my_id : String, your_id :String) {
        coroutineScope.launch {

            var srchivedDeferred =  GdgApi(AllConstants.base_node_url).apiService
                .removeFromArchived(my_id , your_id)
//            var srchivedDeferred =  GdgApi.apiService
//                .removeFromArchived(my_id , your_id)

            try {
                var listResult = srchivedDeferred.await()
                setState(your_id, "null")
                Log.d(TAG,"Success: $listResult Mars properties retrieved")
            } catch (e: Exception) {
                Log.d(TAG,"Failure: ${e.message}")

            }
        }
    }

    /// for delete chatRoom
    fun deleteChatRoom(my_id : String, your_id :String) {
        var  chatRooms = _chatRoomListMutableLiveData.value
         coroutineScope.launch  {

             var deleteDeferred = GdgApi(AllConstants.base_node_url).apiService.deleteChatRoom(my_id , your_id)
//             var deleteDeferred = GdgApi.apiService.deleteChatRoom(my_id , your_id)



             try {
                var listResult = deleteDeferred?.await()
                for (chatRoom in chatRooms!!) {
                    if (chatRoom != null) {
                        if (chatRoom.other_id == your_id) {
                            chatRooms.remove(chatRoom)
                            Log.d(TAG, "deleteChatRoom: "+chatRooms.size)
                            _chatRoomListMutableLiveData.value = chatRooms

                            break
                        }
                    }
                }

            } catch (e: Exception) {
                Log.d(TAG,"Failure: ${e.message}")

            }
        }
    }


    /// state 1 for Archived ChatRoom
    fun setState(anthor_user_id: String, state: String?) {
        var  chatRooms = _chatRoomListMutableLiveData.value
        for (chatRoom in chatRooms!!) {
            if (chatRoom != null) {
                if (chatRoom.other_id == anthor_user_id) {
                    chatRoom.state = state!!
                    Log.d(TAG,"Failure: $state")

                    break
                }
            }
        }
        _chatRoomListMutableLiveData.value = chatRooms

    }

    fun setLastMessage(
        message: String?,
        chatId: String,
        senderId: String,
        reciverId: String,
        type: String,
        state: String?,
        dateTime: String?,
        sender_id: String?,
    ) {
        var chatRoomsList = _chatRoomListMutableLiveData.value


        var inList = false
        for (chatRoom in chatRoomsList!!) {
            if (chatRoom != null) {
                if (chatRoom.id == chatId) {
                    chatRoom.last_message = message!!
                    chatRoom.message_type = type
                    chatRoom.mstate = state!!
                    chatRoom.created_at = dateTime!!
                    chatRoom.msg_sender = sender_id!!
                    inList = true
                    if (!chatRoom.inChat) {
                        chatRoom.num_msg = (Integer.parseInt(chatRoom.num_msg)+1).toString()
                    }
                    chatRoomsList.remove(chatRoom)
                    chatRoomsList.add(0, chatRoom)
                    break
                }
            }
        }
        if (!inList) {
            for (chatRoom in chatRoomsList) {
                if (chatRoom != null) {
                    if (chatRoom.id == senderId + reciverId) {
                        chatRoomsList.remove(chatRoom)
                        if (!chatRoom.inChat) {
                            chatRoom.num_msg = (Integer.parseInt(chatRoom.num_msg)+1).toString()
                        }
                        chatRoom.id = chatId
                        chatRoomsList.remove(chatRoom)
                        chatRoomsList.add(0, chatRoom)
                        break
                    }
                }
            }
        }
        _chatRoomListMutableLiveData.value = chatRoomsList
    }

    fun updateLastMessageState(state: String?, chat_id: String) {
        var chatRoomsList = _chatRoomListMutableLiveData.value

        if (chatRoomsList != null) {
            for (chatRoom in chatRoomsList) {
                if (chatRoom != null) {
                    if (chatRoom.id == chat_id) {
                        chatRoom.mstate = "3"
                        break
                    }
                }
            }
        }
        _chatRoomListMutableLiveData.value = chatRoomsList
    }
    fun setTyping(chat_id: String, isTyping: Boolean) {
        var chatRoomsList = _chatRoomListMutableLiveData.value

        if (chatRoomsList != null) {
            for (chatRoom in chatRoomsList) {
                if (chatRoom != null) {
                    if (chatRoom.id == chat_id) {
                        chatRoom.isTyping = isTyping
                        break
                    }
                }
            }
        }
        _chatRoomListMutableLiveData.value = chatRoomsList
    }
    fun setInChat(user_id: String, state: Boolean) {
        var chatRoomsList = _chatRoomListMutableLiveData.value

        if (chatRoomsList != null) {
            for (chatRoom in chatRoomsList) {
                if (chatRoom != null) {
                    if (chatRoom.other_id == user_id) {
                        chatRoom.inChat = state
                        if (state){
                            chatRoom.num_msg = "0";


                        }                        }
                        break
                    }
                }
            }
        _chatRoomListMutableLiveData.value = chatRoomsList

    }

    fun checkInChat(anthor_user_id: String): Boolean {
        var chatRoomsList = _chatRoomListMutableLiveData.value

        if (chatRoomsList != null) {
            for (chatRoom in chatRoomsList) {
                if (chatRoom != null) {
                    if (chatRoom.other_id == anthor_user_id) {
                        return chatRoom.inChat
                    }
                }
            }
        }
        return false
    }


    fun getChatId(anthor_user_id: String): String {
        var chatRoomsList = _chatRoomListMutableLiveData.value

        if (chatRoomsList != null) {
            for (chatRoom in chatRoomsList) {
                if (chatRoom != null) {
                    if (chatRoom.other_id == anthor_user_id) {
                        return chatRoom.id
                    }
                }
            }
        }
        return ""
    }



    fun setBlockedState(anthor_user_id: String, blockedFor: String?) {
        var chatRoomsList = _chatRoomListMutableLiveData.value
        if (chatRoomsList != null) {
            for (chatRoom in chatRoomsList) {
                if (chatRoom != null) {
                    if (chatRoom.other_id == anthor_user_id) {
                        chatRoom.blocked_for = blockedFor!!
                        break
                    }
                }
            }
        }
        _chatRoomListMutableLiveData.value = chatRoomsList
    }

    fun addChatRoom(chatRoomModel: ChatRoomModel) {
        var chatRoomsList = _chatRoomListMutableLiveData.value

        chatRoomsList?.add(0, chatRoomModel)
       _chatRoomListMutableLiveData.value = chatRoomsList
    }


    fun getChatRoomModelList(): ArrayList<ChatRoomModel?>? {
        return _chatRoomListMutableLiveData.value
    }


    fun setisArchived (state : Boolean){
        _isArchivedMutableLiveData.value = state
    }
    fun setLoading (state : Boolean){
        _loadingMutableLiveData.value = state
    }
    fun setErrorMessage (state : Boolean){
        _showErrorMessage.value = state
    }
    fun setChatRoomListMutableLiveData(data:ArrayList<ChatRoomModel?>?){
        _chatRoomListMutableLiveData.value = data

    }

}
