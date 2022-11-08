package com.yawar.memo.repositry

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yawar.memo.Api.GdgApi
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.model.ChatMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class ChatMessageRepoo {
    private val _chatMessageistMutableLiveData = MutableLiveData<ArrayList<ChatMessage?>?>()
    //            return repository.chatMessageistMutableLiveData;
    val chatMessaheHistory: LiveData<ArrayList<ChatMessage?>?>
        get() = _chatMessageistMutableLiveData



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


    fun loadChatRoom( my_id: String,  anthor_user_id:String) {
        _loadingMutableLiveData.value = true
        _chatMessageistMutableLiveData.value = null
        coroutineScope.launch {
            val chatMessageList = ArrayList<ChatMessage?>()


            val getChatRoomsDeferred = GdgApi(AllConstants.base_node_url).apiService
                .getChatMessgeHistory( my_id,  anthor_user_id)
//            val getChatRoomsDeferred = GdgApi.apiService
//                .getChatMessgeHistory( my_id,  anthor_user_id)

            try {
                val listResult = getChatRoomsDeferred?.await()
                _loadingMutableLiveData.value = false

                val jsonArray = JSONArray(listResult)


                for (i in 0 until jsonArray.length()) {
                    val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                    val chatMessage = ChatMessage()
                    chatMessage.userId = jsonObject.getString("sender_id")
                    chatMessage.state = jsonObject.getString("state")
                    chatMessage.isMe = jsonObject.getString("sender_id") == my_id
                    if (jsonObject.getString("message_type") == "file" || jsonObject.getString("message_type") == "voice" || jsonObject.getString(
                            "message_type") == "video" || jsonObject.getString("message_type") == "contact" || jsonObject.getString(
                            "message_type") == "imageWeb"
                    ) {
                        chatMessage.fileName = jsonObject.getString("orginalName")
                    }
                    //                            chatMessage.setFileName("orginalName");}
                    chatMessage.id = jsonObject.getString("message_id")
                    chatMessage.isChecked = false
                    if (jsonObject.getString("message_type") != "imageWeb") {
                        chatMessage.message = jsonObject.getString("message")
                    } else {
                        chatMessage.image = jsonObject.getString("message")
                    }
                    chatMessage.type = jsonObject.getString("message_type")
                    chatMessage.dateTime = jsonObject.getString("created_at")
                    chatMessage.isUpdate = jsonObject.getString("edited")
                    chatMessageList.add(chatMessage)
                }


                _chatMessageistMutableLiveData.value = chatMessageList




        } catch (e: Exception) {

                _loadingMutableLiveData.value = false
                _showErrorMessage.value = true

                Log.d("getMarsRealEstateProperties: ","Failure: ${e.message}")

            }
        }
    }



    @SuppressLint("SuspiciousIndentation")
    fun addMessage(chatMessage: ChatMessage) {

     var chatMessageList = _chatMessageistMutableLiveData.value
        chatMessageList?.add(chatMessage)
        _chatMessageistMutableLiveData.value = chatMessageList
    }

    fun setMessageState(message_id: String, state: String) {
        var chatMessageList = _chatMessageistMutableLiveData.value

        if (chatMessageList != null) {
            System.out.println("chatMessageList.size ${chatMessageList.size}")

            for (i in chatMessageList.size -1 downTo 0) {
                System.out.println("chatMessageList.sizzzzz ${chatMessageList.size}")

                if (state == "3") {
                    if (chatMessageList != null) {
                        if (chatMessageList.get(i)!!.state == "3" || chatMessageList.get(i)!!.state == "0") {
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
                        if (chatMessageList.get(i)!!.id == message_id) {
                            chatMessageList.get(i)!!.state = state
                            break
                        }
                    }
                }
            }
        }
        _chatMessageistMutableLiveData.value = chatMessageList
    }

    fun setMessageUpload(message_id: String, isDownload: Boolean) {
        var chatMessageList = _chatMessageistMutableLiveData.value
        if (chatMessageList != null) {
            for (chatMessage in chatMessageList) {
                if (chatMessage != null) {
                    if (chatMessage.id == message_id) {
                        chatMessage.upload = isDownload
                        break
                    }
                }
            }
        }
        _chatMessageistMutableLiveData.value = chatMessageList
    }

    fun setMessageDownload(message_id: String, isDownload: Boolean) {
        var chatMessageList = _chatMessageistMutableLiveData.value
        if (chatMessageList != null) {
            for (chatMessage in chatMessageList) {
                if (chatMessage != null) {
                    if (chatMessage.id == message_id) {
                        chatMessage.isDownload = isDownload
                        break
                    }
                }
            }
        }
        _chatMessageistMutableLiveData.value = chatMessageList
    }
    fun setMessageChecked(message_id: String, isChecked: Boolean) {
        var chatMessageList = _chatMessageistMutableLiveData.value
        if (chatMessageList != null) {
            for (chatMessage in chatMessageList) {
                if (chatMessage != null) {
                    if (chatMessage.id == message_id) {
                        chatMessage.isChecked = isChecked
                        break
                    }
                }
            }
        }
        _chatMessageistMutableLiveData.value = chatMessageList
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
                    if (message.id == chatMessageList[i]!!.id) {
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

        if (chatMessageList != null) {
            for ( chatMessage in chatMessageList) {
                if (chatMessage != null) {
                    setMessageChecked(chatMessage.id, false)


                }
            }
            chatMessageList.clear()

        }


        _selectedMessage.value = chatMessageList
    }



    fun deleteMessage(chatMessagee: ChatMessage?) {
        var chatMessageList = _chatMessageistMutableLiveData.value
        if (chatMessageList != null) {
            chatMessageList.remove(chatMessagee)
        }
        _chatMessageistMutableLiveData.value = chatMessageList
    }


    fun UpdateMessage(message_id: String, message: String?) {
        var chatMessageList = _chatMessageistMutableLiveData.value
        if (chatMessageList != null) {
            for (chatMessage in chatMessageList) {
                if (chatMessage != null) {
                    if (chatMessage.id == message_id) {
                        chatMessage.message = message!!
                        chatMessage.isUpdate = "1"
                        break
                    }
                }
            }
        }
        _chatMessageistMutableLiveData.value = chatMessageList
    }


    fun deleteMessageForMe(
        message_id: String,
        user_id: String?,
        chatMessages: ArrayList<ChatMessage?>?,
    ) {
        coroutineScope.launch {
            var chatMessageList = _chatMessageistMutableLiveData.value


            var deleteDeferred =  GdgApi(AllConstants.base_node_url).apiService
                .deleteMessage(message_id,user_id)
//            var deleteDeferred =  GdgApi.apiService
//                .deleteMessage(message_id,user_id)


            try {
                var listResult = deleteDeferred?.await()
                if (chatMessages != null) {
                    for ( i in 0 until chatMessages.size) {

                        val id = chatMessages?.get(i)?.id
                        if (chatMessageList != null) {
                            for (chatMessage in chatMessageList) {
                                if (chatMessage != null) {
                                    if (chatMessage.id == id) {
                                        deleteMessage(chatMessage)
                                        break
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {



                Log.d("getMarsRealEstateProperties: ","Failure: ${e.message}")

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