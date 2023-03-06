package com.yawar.memo.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ChatMessage(
    var  messageId:String = "",
//    var  id:String = "",
    var  isMe:Boolean = false,
    var  message:String = "",
    var  image:String = "",
    var  type:String= "",
    var  senderId:String= "",
    var  recivedId:String = "",
    var  dateTime:String= "",
    var  state:String = "",
    var  fileName:String= "",
    var  isUpdate:String="",
    var  isDownload:Boolean = false,
    var  upload:Boolean = false,
    var  isChecked:Boolean = false,
):  Cloneable, Parcelable {

     public override fun  clone() : ChatMessage {

            var clone : ChatMessage
            try {
                  clone = super.clone() as ChatMessage
            } catch ( e : CloneNotSupportedException) {
                  throw  RuntimeException(e); //should not happen
            }

            return clone;
      }

}