package com.yawar.memo.repositry

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.yawar.memo.Api.ChatApi
import com.yawar.memo.BaseApp
import com.yawar.memo.database.dao.ChatRoomDatabase
import com.yawar.memo.database.entity.ChatMessageEntity.ChatMessageEntityMapper
import com.yawar.memo.domain.model.AnthorUserInChatRoomId
//import com.yawar.memo.Api.GdgApi
import com.yawar.memo.domain.model.ChatMessage
import com.yawar.memo.network.networkModel.chatMessageModel.ChatMessageDtoMapper
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import kotlin.collections.ArrayList

class ChatMessageRepoo
   @Inject constructor(
    private val chatApi: ChatApi,
    private val database: ChatRoomDatabase,
    private val chatMessageDtoMapper: ChatMessageDtoMapper,
    private val chatMessageEntityMapper: ChatMessageEntityMapper,
        ) {

    val anthorUserInChatRoomId = AnthorUserInChatRoomId.getInstance("2")
    private val _chatMessageistMutableLiveData = MutableLiveData<ArrayList<ChatMessage?>?>()
    //            return repository.chatMessageistMutableLiveData;
//    val chatMessaheHistory: LiveData<ArrayList<ChatMessage?>?>
//        get() = _chatMessageistMutableLiveData
    val chatMessaheHistory: LiveData<List<ChatMessage>> = Transformations.map(database.chatRoomDao.getChatMessage(anthorUserInChatRoomId.id)) {
        chatMessageEntityMapper.toDomainList(it) as List<ChatMessage>?
    }

    private val _selectedMessage = MutableLiveData<ArrayList<ChatMessage?>?>()
    val selectedMessage: LiveData<ArrayList<ChatMessage?>?>
        get() = _selectedMessage


    private val _loadingMutableLiveData =  MutableLiveData<Boolean>(false)
    val loading : LiveData<Boolean>
        get() = _loadingMutableLiveData



    private val _showErrorMessage =  MutableLiveData<Boolean>(false)
    val errorMessage : LiveData<Boolean>
        get() = _showErrorMessage



    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )


    fun loadChatRoom(my_id: String?, anthor_user_id:String) {
        _loadingMutableLiveData.value = true
        _chatMessageistMutableLiveData.value = null
        coroutineScope.launch {
//            val chatMessageList = ArrayList<ChatMessage?>()


//            val getChatRoomsDeferred = GdgApi(AllConstants.base_node_url).apiService
//                .getChatMessgeHistory( my_id,  anthor_user_id)
//            val getChatRoomsDeferred = GdgApi.apiService
//                .getChatMessgeHistory( my_id,  anthor_user_id)
            val getChatRoomsDeferred = chatApi.getChatMessgeHistory( my_id,  anthor_user_id)
            try {
                val listResult = getChatRoomsDeferred?.await()
                _loadingMutableLiveData.value = false
                Log.d("loadChatMessage", listResult.toString())
                withContext(Dispatchers.IO) {

                    database.chatRoomDao.insertChatMessage(*(chatMessageDtoMapper.toEntityList(listResult)))

                }
//                val jsonArray = JSONArray(listResult)
//
//
//                for (i in 0 until jsonArray.length()) {
//                    val jsonObject: JSONObject = jsonArray.getJSONObject(i)
//                    val chatMessage = ChatMessage()
//                    chatMessage.senderId = jsonObject.getString("sender_id")
//                    chatMessage.state = jsonObject.getString("state")
//                    chatMessage.isMe = jsonObject.getString("sender_id") == my_id
//                    if (jsonObject.getString("message_type") == "file" || jsonObject.getString("message_type") == "voice" || jsonObject.getString(
//                            "message_type") == "video" || jsonObject.getString("message_type") == "contact" || jsonObject.getString(
//                            "message_type") == "imageWeb"
//                    ) {
//                        chatMessage.fileName = jsonObject.getString("orginalName")
//                    }
//                    //                            chatMessage.setFileName("orginalName");}
//                    chatMessage.id = jsonObject.getString("message_id")
//                    chatMessage.recivedId = jsonObject.getString("reciver_id")
//                    chatMessage.isChecked = false
//                    if (jsonObject.getString("message_type") != "imageWeb") {
//                        chatMessage.message = jsonObject.getString("message")
//                    } else {
//                        chatMessage.image = jsonObject.getString("message")
//                    }
//                    chatMessage.type = jsonObject.getString("message_type")
//                    chatMessage.dateTime = jsonObject.getString("created_at")
//                    chatMessage.isUpdate = jsonObject.getString("edited")
//                    chatMessageList.add(chatMessage)
//                }
//                _chatMessageistMutableLiveData.value = chatMessageList

        } catch (e: Exception) {

                _loadingMutableLiveData.value = false
                _showErrorMessage.value = true

                Log.d("getMarsRealEstateProperties: ","Failure: ${e.message}")

            }
        }
    }



    @SuppressLint("SuspiciousIndentation")
    fun addMessage(chatMessage: ChatMessage) {
        coroutineScope.launch {
            Log.d("addMessageeeeeee", "addMessage: ")

//          if(chatMessaheHistory.value!=null) {
//
//              var chatMessageList = chatMessaheHistory.value as ArrayList
//              chatMessageList?.add(chatMessage)
//              Log.d("addMessage: ", chatMessage.message)
              withContext(Dispatchers.IO) {
                  database.chatRoomDao.insertOneChatMessage(
                      chatMessageEntityMapper.mapFromDominModel(
                          chatMessage
                      )
                  )
          }
        }
    }

    fun setMessageState(message_id: String, state: String) {
        coroutineScope.launch {

            var chatMessageList = chatMessaheHistory.value as ArrayList

            if (chatMessageList != null) {
                System.out.println("chatMessageList.size ${chatMessageList.size}")

                for (i in chatMessageList.size - 1 downTo 0) {
                    System.out.println("chatMessageList.sizzzzz ${chatMessageList.size}")

                    if (state == "3") {
                        if (chatMessageList != null) {
//                        if (chatMessageList.get(i)!!.state == "3" || chatMessageList.get(i)!!.state == "0") {
                            if (chatMessageList.get(i)!!.state == "3") {

                                break
                            } else chatMessageList.get(i)!!.state = state
                        }
                    } else if (state == "2") {
                        if (chatMessageList != null) {
                            if (chatMessageList.get(i)!!.state == "2" || chatMessageList.get(i)!!.state == "3") {
                                break
                            } else {
                                chatMessageList.get(i)!!.state = state
                            }
                        }
                    } else if (state == "1") {
                        if (chatMessageList != null) {
                            if (chatMessageList.get(i)!!.messageId == message_id) {
                                chatMessageList.get(i)!!.state = state
                                break
                            }
                        }
                    }
                }
            }
            withContext(Dispatchers.IO) {
                database.chatRoomDao.insertChatMessage(*(chatMessageEntityMapper.fromDomainList(chatMessageList)))

            }
        }
    }

    fun setMessageUpload(message_id: String, isUpload: Boolean) {
//        var chatMessageList = _chatMessageistMutableLiveData.value
        coroutineScope.launch {

            withContext(Dispatchers.IO) {
                database.chatRoomDao.setIsMessageUpload(
                    message_id,isUpload
                    )

            }
        }
//        if (chatMessageList != null) {
//            for (chatMessage in chatMessageList) {
//                if (chatMessage != null) {
//                    if (chatMessage.id == message_id) {
//                        chatMessage.upload = isUpload
//                        break
//                    }
//                }
//            }
//        }
//        _chatMessageistMutableLiveData.value = chatMessageList
    }

    fun setMessageDownload(message_id: String, isDownload: Boolean) {
        Log.d("setMessageDownload", "setMessageDownload: ")
        coroutineScope.launch {

            withContext(Dispatchers.IO) {
                database.chatRoomDao.setIsMessageDownload(
                    message_id,isDownload
                )

            }
        }
//        var chatMessageList = _chatMessageistMutableLiveData.value
//        if (chatMessageList != null) {
//            for (chatMessage in chatMessageList) {
//                if (chatMessage != null) {
//                    if (chatMessage.id == message_id) {
//                        chatMessage.isDownload = isDownload
//                        Log.d("setMessageDownload",  chatMessage.isDownload.toString())
//
//                        break
//                    }
//                }
//            }
//        }
//        _chatMessageistMutableLiveData.value = chatMessageList
    }


    fun setMessageChecked(message_id: String, isChecked: Boolean) {
        coroutineScope.launch {

            withContext(Dispatchers.IO) {
                database.chatRoomDao.setIsMessageChecked(
                    message_id, isChecked
                )

            }
//        var chatMessageList = _chatMessageistMutableLiveData.value
//        if (chatMessageList != null) {
//            for (chatMessage in chatMessageList) {
//                if (chatMessage != null) {
//                    if (chatMessage.id == message_id) {
//                        chatMessage.isChecked = isChecked
//                        break
//                    }
//                }
//            }
//        }
//        _chatMessageistMutableLiveData.value = chatMessageList

        }
    }

    fun addSelectedMessage(message: ChatMessage?) {
        var chatMessageList = _selectedMessage.value
        System.out.println("chatMessageList $chatMessageList")
        if (chatMessageList != null) {
            chatMessageList.add(message)
        }
        else{
            chatMessageList = ArrayList<ChatMessage?>()
            chatMessageList.add(message)

        }

        _selectedMessage.value = chatMessageList
    }

    fun removeSelectedMessage(message: ChatMessage) {
        val chatMessageList = _selectedMessage.value

        System.out.println("removvvvvvvvedddd ${chatMessageList}")

        if (chatMessageList != null) {

            for ( i in 0 until chatMessageList.size) {

                if (chatMessageList[i] != null) {
                    if (message.messageId == chatMessageList[i]!!.messageId) {
                        chatMessageList.remove(chatMessageList[i])
                        break
                    }
                }
            }
        }
        _selectedMessage.value = chatMessageList
    }

    fun clearSelectedMessage() {

        var chatMessageList = _selectedMessage.value
        Log.d("clearSelectedMessage", "clearSelectedMessage: ${chatMessageList?.size}")

        if (chatMessageList != null) {
            for ( chatMessage in chatMessageList) {
                if (chatMessage != null) {
                    setMessageChecked(chatMessage.messageId, false)


                }
            }
            chatMessageList.clear()
            Log.d("clearSelectedMessage", "clearSelectedMessage: ${chatMessageList.size}")


        }


        _selectedMessage.value = chatMessageList
    }



    fun deleteMessage(chatMessagee: ChatMessage?) {

        coroutineScope.launch {

            withContext(Dispatchers.IO) {
                if (chatMessagee != null) {
                    database.chatRoomDao.deleteMessage(
                        chatMessagee.messageId
                    )
                }

            }
//        var chatMessageList = _chatMessageistMutableLiveData.value
//        if (chatMessageList != null) {
//            chatMessageList.remove(chatMessagee)
//        }
//        _chatMessageistMutableLiveData.value = chatMessageList
        }
    }


    fun UpdateMessage(message_id: String, message: String?) {
        coroutineScope.launch {

            withContext(Dispatchers.IO) {
                    database.chatRoomDao.updateMessage(
                        message_id,message!!, "1"
                    )
                }

            }
        }
//        var chatMessageList = _chatMessageistMutableLiveData.value
//        if (chatMessageList != null) {
//            for (chatMessage in chatMessageList) {
//                if (chatMessage != null) {
//                    if (chatMessage.id == message_id) {
//                        chatMessage.message = message!!
//                        chatMessage.isUpdate = "1"
//                        break
//                    }
//                }
//            }
//        }
//        _chatMessageistMutableLiveData.value = chatMessageList




    fun deleteMessageForMe(
        message_id: String,
        user_id: String?,
        chatMessages: ArrayList<ChatMessage?>?,
    ) {
        coroutineScope.launch {
            var chatMessageList = chatMessaheHistory.value as ArrayList


//            var deleteDeferred = GdgApi(AllConstants.base_node_url).apiService
//                .deleteMessage(message_id, user_id)
//            var deleteDeferred =  GdgApi.apiService
//                .deleteMessage(message_id,user_id)

            var deleteDeferred =  chatApi
                .deleteMessage(message_id,user_id)
            try {
                var listResult = deleteDeferred?.await()
                if (chatMessages != null) {
                    for (i in 0 until chatMessages.size) {

                        val id = chatMessages?.get(i)?.messageId
                        if (chatMessageList != null) {
                            for (chatMessage in chatMessageList) {
                                if (chatMessage != null) {
                                    if (chatMessage.messageId == id) {
                                        deleteMessage(chatMessage)
                                        break
                                    }
                                }
                            }
                        }
                    }
                }
                        clearSelectedMessage()

            } catch (e: Exception) {


                Log.d("getMarsRealEstateProperties: ", "Failure: ${e.message}")

            }
        }
//        clearSelectedMessage();
    }
        fun getUnRecivedMessages(
        ) {
            val chatMessageList = ArrayList<ChatMessage?>()

            coroutineScope.launch {


//                var deleteDeferred = GdgApi(AllConstants.base_node_url).apiService
//                    .getUnRecivedMessages(BaseApp.getInstance().classSharedPreferences.user.userId)
//                val deleteDeferred = GdgApi.apiService
//                    .getUnRecivedMessages( BaseApp.getInstance().classSharedPreferences.user.userId)
                val deleteDeferred = chatApi
                    .getUnRecivedMessages( BaseApp.instance?.classSharedPreferences!!.user.userId)


                try {
                    var listResult = deleteDeferred?.await()

                    Log.d("getMarsRealEstateProperties: ", listResult.toString())
                    val jsonArray = JSONArray(listResult)


                    for (i in 0 until jsonArray.length()) {
                        val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                        val chatMessage = ChatMessage()
                        chatMessage.senderId = jsonObject.getString("sender_id")
                        chatMessage.state = jsonObject.getString("state")
                        chatMessage.isMe = jsonObject.getString("sender_id") == BaseApp.instance?.classSharedPreferences!!.user.userId
                        if (jsonObject.getString("message_type") == "file" || jsonObject.getString("message_type") == "voice" || jsonObject.getString(
                                "message_type") == "video" || jsonObject.getString("message_type") == "contact" || jsonObject.getString(
                                "message_type") == "imageWeb"
                        ) {
                            chatMessage.fileName = jsonObject.getString("orginalName")
                        }
                        //                            chatMessage.setFileName("orginalName");}
                        chatMessage.messageId = jsonObject.getString("message_id")
                        chatMessage.isChecked = false
                        if (jsonObject.getString("message_type") != "imageWeb") {
                            chatMessage.message = jsonObject.getString("message")
                        } else {
                            chatMessage.image = jsonObject.getString("message")
                        }
                        chatMessage.type = jsonObject.getString("message_type")
                        chatMessage.dateTime = jsonObject.getString("created_at")
                        chatMessage.isUpdate = jsonObject.getString("edited")
                       if( BaseApp.instance?.chatRoomRepoo!!.checkInChat(chatMessage.senderId)){
                           addMessage(chatMessage)
                           Log.d("getMarsRealEstateProperties: ", "addddddddddddd")
                       }
                        else{
                           Log.d("elseeeeeeeeeee: ", "addddddddddddd")

                           BaseApp.instance?.chatRoomRepoo!!.setLastMessageBySenderId(
                               chatMessage.message,chatMessage.messageId, chatMessage.senderId,
                               chatMessage.messageId, chatMessage.type, chatMessage.state, chatMessage.dateTime, chatMessage.senderId
                           )
                        }

                        chatMessageList.add(chatMessage)
                    }

                    Log.d("getMarsRealEstateProperties: ", chatMessageList.size.toString())



                } catch (e: Exception) {


                    Log.d("getMarsRealEstateProperties: ", "Failure: ${e.message}")

                }
            }
        }






    fun setLoading (state : Boolean){
        _loadingMutableLiveData.value = state
    }
    fun setErrorMessage (state : Boolean){
        _showErrorMessage.value = state
    }


}