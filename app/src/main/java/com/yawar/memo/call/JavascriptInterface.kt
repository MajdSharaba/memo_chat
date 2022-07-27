package com.example.videocallapp

import android.webkit.JavascriptInterface
import com.yawar.memo.call.ResponeCallActivity

class JavascriptInterface(val ResponeCallActivity: ResponeCallActivity) {

    @JavascriptInterface
    fun onPeerConnected(string: String) {
        println("this is javaScript code")
        ResponeCallActivity.onPeerConnected(string)
    }
    @JavascriptInterface
    fun print(string:String) {
       println("this is javaScript code2$string")
    }

}