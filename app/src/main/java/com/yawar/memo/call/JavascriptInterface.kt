package com.example.videocallapp

import android.webkit.JavascriptInterface
import com.yawar.memo.call.CallMainActivity
import com.yawar.memo.views.ConversationActivity

class JavascriptInterface(val CallMainActivity: CallMainActivity) {

    @JavascriptInterface
    public fun onPeerConnected(string: String) {
        CallMainActivity.onPeerConnected(string)
    }
    @JavascriptInterface
    public fun print() {
       println("this is javaScript code")
    }

}