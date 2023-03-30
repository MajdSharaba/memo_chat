package com.yawar.memo

import android.content.Intent
import com.yawar.memo.domain.model.AnthorUserInChatRoomId
import com.yawar.memo.domain.model.ChatRoomModel
import com.yawar.memo.repositry.ChatRoomRepoo
import com.yawar.memo.service.SocketIOService
import org.json.JSONException
import org.json.JSONObject
val  anthorUserInChatRoomId = AnthorUserInChatRoomId.getInstance("","","","","","","","")
val chatRoomRepoo = BaseApp.instance?.chatRoomRepoo
fun newMeesage(chatMessage: JSONObject) {
    var message: String? = ""
    var type = ""
    var time: String? = "1646028789098"
    var message_id: String? = ""

    try {
        type = chatMessage.getString("message_type")
        time = chatMessage.getString("dateTime")
        message_id = chatMessage.getString("message_id")
        message = if (type == "text") {
            chatMessage.getString("message")
        } else if (type == "imageWeb" || type == "location") {
            "photo"
        } else {
            chatMessage.getString("orginalName")
        }
    } catch (e: JSONException) {
        e.printStackTrace()
    }
    if (anthorUserInChatRoomId.chatId.isEmpty()) {
        anthorUserInChatRoomId.chatId = BaseApp.instance?.classSharedPreferences?.user?.userId+ anthorUserInChatRoomId.id
        chatRoomRepoo!!.addChatRoom(
            ChatRoomModel(
                anthorUserInChatRoomId.userName, anthorUserInChatRoomId.id, message!!,
                anthorUserInChatRoomId.imageUrl!!, false, "0", BaseApp.instance?.classSharedPreferences?.user?.userId + anthorUserInChatRoomId.id, "null", "0",
                true, anthorUserInChatRoomId.fcmToken!!, anthorUserInChatRoomId.specialNumber, type, "1", time!!, false, "null", BaseApp.instance?.classSharedPreferences?.user?.userId, ""
            )
        )
    }
//        serverApi!!.sendNotification(
//            message,
//            type,
//            fcmToken,
//            chat_id,
//            conversationModelView!!.blockedFor().value,
//            message_id,
//                    time
//        )
    println("contact $chatMessage")
    val service = Intent(BaseApp.instance?.baseContext, SocketIOService::class.java)
    service.putExtra(SocketIOService.EXTRA_NEW_MESSAGE_PARAMTERS, chatMessage.toString())
    service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_MESSAGE)
    BaseApp.instance?.baseContext?.startService(service)
}
