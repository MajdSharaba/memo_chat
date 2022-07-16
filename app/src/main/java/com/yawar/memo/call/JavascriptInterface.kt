package com.example.videocallapp

import android.webkit.JavascriptInterface
import com.yawar.memo.call.CallMainActivity
import com.yawar.memo.views.ConversationActivity

class JavascriptInterface(val CallMainActivity: CallMainActivity) {

    @JavascriptInterface
    fun onPeerConnected(string: String) {
        println("this is javaScript code")
        CallMainActivity.onPeerConnected(string)
    }
    @JavascriptInterface
    fun print(string:String) {
       println("this is javaScript code2$string")
    }

}