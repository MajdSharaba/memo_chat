package com.yawar.memo.repositry


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.yawar.memo.Api.ChatApi
import com.yawar.memo.database.dao.ChatRoomDatabase
import com.yawar.memo.database.entity.chatRoomEntity.ChatRoomEntityMapper
import com.yawar.memo.domain.model.ChatRoomModel
import com.yawar.memo.network.networkModel.chatRoomModel.ChatRoomDtoMapper
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class ChatRoomRepoo  @Inject constructor(
    private val chatApi: ChatApi,
    private val mapper: ChatRoomDtoMapper,
    private val chatRoomEntityMapper: ChatRoomEntityMapper,
    private val database : ChatRoomDatabase,
     ){

    private val TAG: String = "ChatRoomRepoo"
    private val _chatRoomListMutableLiveData = MutableLiveData<ArrayList<ChatRoomModel?>?>()
    val chatRoomListMutableLiveData: LiveData<List<ChatRoomModel>> = database.chatRoomDao.getChatRooms().map {
        chatRoomEntityMapper.toDomainList(it) as List<ChatRoomModel>
    }

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
        coroutineScope.launch {
            _loadingMutableLiveData.value = true


//            val getChatRoomsDeferred =   GdgApi(AllConstants.base_url).apiService
//                .getChatRoom(user_id)
//            val getChatRoomsDeferred =   GdgApi.apiService
//                .getChatRoom(user_id)
            val getChatRoomsDeferred = chatApi.getChatRoom(user_id)

            try {
                withContext(Dispatchers.IO) {
                    val listResult = getChatRoomsDeferred.await()
                    Log.d(TAG, "loadChatRoom: "+listResult.data)
                    database.chatRoomDao.insertAll(*(mapper.toEntityList(listResult.data)))
                }

                _loadingMutableLiveData.value = false
                _showErrorMessage.value = false
//                Log.d("chatRoomTag: ","Success: ${listResult.data} Mars properties retrieved")
//                _chatRoomListMutableLiveData.value = mapper.toDomainList(listResult.data) as ArrayList<ChatRoomModel?>?
//                _chatRoomListMutableLiveData.value = listResult.data

            } catch (e: Exception) {
                _loadingMutableLiveData.value = false
                _showErrorMessage.value = true


                Log.d("chatRoomTag: ","Failure: ${e}")

            }
        }
    }
     fun addToArchived(my_id : String, your_id :String) {
        coroutineScope.launch {
//
//            var srchivedDeferred =  GdgApi(AllConstants.base_node_url).apiService
//                .addToArchived(my_id , your_id)
//            var srchivedDeferred =  GdgApi.apiService
//                .addToArchived(my_id , your_id)
            var srchivedDeferred =  chatApi
                .addToArchived(my_id , your_id)


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

//            var srchivedDeferred =  GdgApi(AllConstants.base_node_url).apiService
//                .removeFromArchived(my_id , your_id)
//            var srchivedDeferred =  GdgApi.apiService
//                .removeFromArchived(my_id , your_id)
            var srchivedDeferred = chatApi
                .removeFromArchived(my_id , your_id)
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
//        var  chatRooms = _chatRoomListMutableLiveData.value
        var  chatRooms   = chatRoomListMutableLiveData.value as ArrayList
         coroutineScope.launch {
             withContext(Dispatchers.IO) {

//             var deleteDeferred = GdgApi(AllConstants.base_node_url).apiService.deleteChatRoom(my_id , your_id)
//             var deleteDeferred = GdgApi.apiService.deleteChatRoom(my_id , your_id)

                 var deleteDeferred = chatApi.deleteChatRoom(my_id, your_id)



                 try {
                     var listResult = deleteDeferred?.await()
                     if (chatRooms != null) {
                         for (chatRoom in chatRooms) {
                             if (chatRoom != null) {
                                 if (chatRoom.other_id == your_id) {
//                                     chatRooms.remove(chatRoom)
//                                     Log.d(TAG, "deleteChatRoom: " + chatRooms.size)
                                     //                            _chatRoomListMutableLiveData.value = chatRooms
                                     database.chatRoomDao.deleteChatRoom(
                                         chatRoom.other_id
                                         )

                                     database.chatRoomDao.deleteChatMessages( chatRoom.other_id)


                                 }

                                 break
                             }
                         }
                     }


             } catch (e: Exception) {
             Log.d(TAG, "Failure: ${e.message}")

         }
         }

        }
    }


    /// state 1 for Archived ChatRoom
    fun setState(anthor_user_id: String, state: String?) {
//        var  chatRooms = _chatRoomListMutableLiveData.value
        coroutineScope.launch {
        var  chatRooms   = chatRoomListMutableLiveData.value as ArrayList

        for (chatRoom in chatRooms!!) {
            if (chatRoom != null) {
                if (chatRoom.other_id == anthor_user_id) {
                    chatRoom.state = state!!
                    withContext(Dispatchers.IO) {

                        database.chatRoomDao.updateState(chatRoom.other_id, chatRoom.state!!)
                    }


                    break
                }
            }
        }
        }
//        _chatRoomListMutableLiveData.value = chatRooms

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
        Log.d(TAG, "setLastMessage: ${chatId}")
//        var chatRoomsList = _chatRoomListMutableLiveData.value
        coroutineScope.launch {

              if(chatRoomListMutableLiveData.value!=null){
            var chatRoomsList = chatRoomListMutableLiveData.value as ArrayList

            var inList = false
            if (chatRoomsList != null) {
                for (chatRoom in chatRoomsList!!) {
                    if (chatRoom != null) {
                        if (chatRoom.id == chatId) {
                            chatRoom.last_message = message!!
                            chatRoom.message_type = type
                            chatRoom.mstate = state!!
                            chatRoom.created_at = dateTime!!
                            chatRoom.msg_sender = sender_id!!
                            inList = true
                            if (!chatRoom.inChat!!) {
                                chatRoom.num_msg =
                                    (Integer.parseInt(chatRoom.num_msg) + 1).toString()
                            }
                            Log.d(TAG, "setLastMessage:${chatRoom.num_msg + message} ")
//                        chatRoomsList.remove(chatRoom)
//                        chatRoomsList.add(0, chatRoom)
                            withContext(Dispatchers.IO) {
                                database.chatRoomDao.updateChatRoom(chatRoom.other_id, chatRoom.last_message,chatRoom.message_type.toString(), chatRoom.mstate.toString(),chatRoom.created_at!!.toLong(),chatRoom.msg_sender.toString(), chatRoom.num_msg)
                            }
                            break
                        }
                    }
                }
                Log.d(TAG, "inChattttttttt${inList}: ")
                if (!inList) {
                    for (chatRoom in chatRoomsList) {
                        if (chatRoom != null) {
                            if (chatRoom.id == senderId + reciverId) {
//                                chatRoomsList.remove(chatRoom)
                                if (!chatRoom.inChat!!) {

                                    chatRoom.num_msg =
                                        (Integer.parseInt(chatRoom.num_msg) + 1).toString()
                                }
                                chatRoom.id = chatId
                                withContext(Dispatchers.IO) {
                                    database.chatRoomDao.updateChatRoomId(chatRoom.other_id,chatRoom.id, chatRoom.num_msg)
                                }
//                                chatRoomsList.remove(chatRoom)
//                                chatRoomsList.add(0, chatRoom)

                                break
                            }
                        }
                    }
                }
            }
        }}
//        _chatRoomListMutableLiveData.postValue( chatRoomsList)
    }


    fun setLastMessageBySenderId(
        message: String?,
        chatId: String,
        senderId: String,
        reciverId: String,
        type: String,
        state: String?,
        dateTime: String?,
        sender_id: String?,
    ) {
        coroutineScope.launch {
            if (chatRoomListMutableLiveData.value != null) {
                var chatRoomsList = chatRoomListMutableLiveData.value as ArrayList
                var inList = false
                for (chatRoom in chatRoomsList!!) {
                    if (chatRoom != null) {
                        Log.d(TAG, "setLastMessage: ${chatRoom.other_id + senderId}")
                        if (chatRoom.other_id == senderId) {
                            chatRoom.last_message = message!!
                            chatRoom.message_type = type
                            chatRoom.mstate = state!!
                            chatRoom.created_at = dateTime!!
                            chatRoom.msg_sender = sender_id!!
                            inList = true
                            if (!chatRoom.inChat!!) {
                                chatRoom.num_msg =
                                    (Integer.parseInt(chatRoom.num_msg) + 1).toString()
                            }
                            Log.d(TAG, "setLastMessage:${chatRoom.num_msg + message} ")
                            withContext(Dispatchers.IO) {
                                database.chatRoomDao.updateChatRoom(
                                    chatRoom.other_id,
                                    chatRoom.last_message,
                                    chatRoom.message_type.toString(),
                                    chatRoom.mstate.toString(),
                                    chatRoom.created_at!!.toLong(),
                                    chatRoom.msg_sender.toString(),
                                    chatRoom.num_msg
                                )
                            }
//                    chatRoomsList.remove(chatRoom)
//                    chatRoomsList.add(0, chatRoom)


                            break
                        }
                    }
                }
            }
        }

//        _chatRoomListMutableLiveData.postValue( chatRoomsList)
    }

    fun updateLastMessageState(state: String?, chat_id: String) {
        coroutineScope.launch {

//        var chatRoomsList = chatRoomListMutableLiveData.value as ArrayList
//        if (chatRoomsList != null) {
//            for (chatRoom in chatRoomsList) {
//                if (chatRoom != null) {
//                    if (chatRoom.id == chat_id) {
//                        chatRoom.mstate = state
                        withContext(Dispatchers.IO) {

                            database.chatRoomDao.updateLastMessaageState(chat_id, state!!)
                        }

//                        break
                    }
                }
//            }
//        }
//        }
//        _chatRoomListMutableLiveData.postValue(chatRoomsList)
//    }
    fun checkISNewChat( chatId: String) : Boolean {
        if(chatRoomListMutableLiveData.value!=null) {
            var chatRoomsList = chatRoomListMutableLiveData.value

            if (chatRoomsList != null) {
                for (chatRoom in chatRoomsList) {
                    if (chatRoom != null) {
                        if (chatRoom.id == chatId) {
                            return false
                            break
                        }
                    }
                }
            }
            return true
        }
        else return  false
    }

    fun setTyping(chat_id: String, isTyping: Boolean) {
        coroutineScope.launch {

            var chatRoomsList = chatRoomListMutableLiveData.value as ArrayList

            if (chatRoomsList != null) {
                for (chatRoom in chatRoomsList) {
                    if (chatRoom != null) {
                        if (chatRoom.id == chat_id) {
//                        chatRoom.isTyping = isTyping
                            withContext(Dispatchers.IO) {
                                database.chatRoomDao.setTyping(
                                    chatRoom.id,
                                    isTyping

                                )
                            }
                            break
                        }
                    }
                }
            }
        }


//        chatRoomListMutableLiveData. = chatRoomsList!!
    }
    fun setInChat(user_id: String, state: Boolean) {
        Log.d(TAG, "setInChat: ${user_id+state}")
        coroutineScope.launch {

            var chatRoomsList = chatRoomListMutableLiveData.value
            Log.d(TAG, "chatRoomsList: ${chatRoomsList}")


            if (chatRoomsList != null) {
                for (chatRoom in chatRoomsList) {
                    if (chatRoom != null) {
                        if (chatRoom.other_id == user_id) {
                            Log.d(TAG, "withContext: ${chatRoomsList}")
                            chatRoom.inChat = state
                            if (state) {
                                chatRoom.num_msg = "0"
                            }
                            withContext(Dispatchers.IO) {
                                database.chatRoomDao.setInChat(
                                    chatRoom.other_id,
                                    state,
                                    chatRoom.num_msg

                                )
                            }


                            break
                        }
                    }
                }
            }
        }
//        _chatRoomListMutableLiveData.value = chatRoomsList

    }

    fun checkInChat(anthor_user_id: String): Boolean {
        var chatRoomsList = chatRoomListMutableLiveData.value

        if (chatRoomsList != null) {
            for (chatRoom in chatRoomsList) {
                if (chatRoom != null) {
                    Log.d(TAG, "checkInChat: ${chatRoom.inChat}")
                    if (chatRoom.other_id == anthor_user_id) {
                        Log.d(TAG, "checkInChatsss: ${chatRoom.inChat}")
                        return chatRoom.inChat!!
                    }
                }
            }
        }
        return false
    }


    fun getChatId(anthor_user_id: String): String {
        var chatRoomsList = chatRoomListMutableLiveData.value

        if (chatRoomsList != null) {
            for (chatRoom in chatRoomsList) {
                if (chatRoom != null) {
                    if (chatRoom.other_id == anthor_user_id) {
                        return chatRoom.id!!
                    }
                }
            }
        }
        return ""
    }



    fun setBlockedState(anthor_user_id: String, blockedFor: String?) {
        coroutineScope.launch {
            var chatRoomsList = chatRoomListMutableLiveData.value
            if (chatRoomsList != null) {
                for (chatRoom in chatRoomsList) {
                    if (chatRoom != null) {
                        if (chatRoom.other_id == anthor_user_id) {
//                        chatRoom.blocked_for = blockedFor!!
                            withContext(Dispatchers.IO) {
                                database.chatRoomDao.setBlockState(
                                    chatRoom.other_id,
                                    blockedFor!!
                                )
                            }
                            break
                        }
                    }
                }
            }
        }
//        _chatRoomListMutableLiveData.value = chatRoomsList
    }

    fun addChatRoom(chatRoomModel: ChatRoomModel) {
        coroutineScope.launch {

            var chatRoomsList = chatRoomListMutableLiveData.value as ArrayList

            chatRoomsList?.add(0, chatRoomModel)
            withContext(Dispatchers.IO) {
                database.chatRoomDao.insertAll(*chatRoomEntityMapper.fromDomainList(chatRoomsList))
            }
        }
//       _chatRoomListMutableLiveData.postValue( chatRoomsList)
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
