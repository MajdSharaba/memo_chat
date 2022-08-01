package com.yawar.memo.videocalltest;

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telecom.Connection
import android.telecom.DisconnectCause
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.yawar.memo.R
import com.yawar.memo.views.DashBord


@RequiresApi(Build.VERSION_CODES.M)
class VoipConnection(ctx: Context) : Connection(){

    val TAG = "VoipConnection"
    val context: Context

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            connectionProperties = PROPERTY_SELF_MANAGED
        }
        context = ctx
    }


    override fun onDisconnect() {
        super.onDisconnect()
        dropCall()
    }

    override fun onAnswer(videoState: Int) {
        super.onAnswer(videoState)
//        this.setActive()
//        var string =this.extras.get("callRequest")
//        Log.i("onAnswer", "onAnswer: ")
//        val myIntent: Intent = Intent(context, CallMainActivity::class.java)
//        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
//        val extras = Bundle()
//        extras.putString("callRequest", string.toString())
//        myIntent.putExtras(extras)
//        context.startActivity(myIntent)
//        setDisconnected(DisconnectCause(DisconnectCause.LOCAL))

        dropCall()


//        destroy()
    }

//    override fun onAnswer() {
//        println("onAnswer()")
//        var string =this.extras.get("callRequest")
//        println("onAnswer"+string)
//        val myIntent: Intent = Intent(context, CallMainActivity::class.java)
//        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        val extras = Bundle()
//        extras.putString("callRequest", string.toString())
//        myIntent.putExtras(extras)
//        context.startActivity(myIntent)
//        dropCall();
//        super.onAnswer()
//
//
//
//    }

    override fun onReject() {
        dropCall()
        super.onReject()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onShowIncomingCallUi() {
        super.onShowIncomingCallUi()
        println("on create incoming call")
        setActive()
//        println("onShowIncomingCallUi")
//        val myIntent: Intent = Intent(context, DashBord::class.java)
//        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
////        val extras = Bundle()
////        extras.putString("callRequest", string.toString())
////        myIntent.putExtras(extras)
//        context.startActivity(myIntent)
        val intent = Intent(Intent.ACTION_ANSWER, null)

        intent.flags = Intent.FLAG_ACTIVITY_NO_USER_ACTION or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.setClass(context, DashBord::class.java)
        val pendingIntent =
            PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_IMMUTABLE)

        // Build the notification as an ongoing high priority item; this ensures it will show as
        // a heads up notification which slides down over top of the current content.

        // Build the notification as an ongoing high priority item; this ensures it will show as
        // a heads up notification which slides down over top of the current content.
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context)
        builder.setOngoing(true)
        builder.priority = Notification.PRIORITY_HIGH

        // Set notification content intent to take user to fullscreen UI if user taps on the
        // notification body.

        // Set notification content intent to take user to fullscreen UI if user taps on the
        // notification body.
        builder.setContentIntent(pendingIntent)
        // Set full screen intent to trigger display of the fullscreen UI when the notification
        // manager deems it appropriate.
        // Set full screen intent to trigger display of the fullscreen UI when the notification
        // manager deems it appropriate.
        builder.setFullScreenIntent(pendingIntent, true)

        // Setup notification content.

        // Setup notification content.
        builder.setSmallIcon(R.drawable.ic_mic)
        builder.setContentTitle("Your notification title")
        builder.setContentText("Your notification content.")

        // Set notification as insistent to cause your ringtone to loop.

        // Set notification as insistent to cause your ringtone to loop.
        val notification: Notification = builder.build()
        notification.flags = notification.flags or Notification.FLAG_INSISTENT

        // Use builder.addAction(..) to add buttons to answer or reject the call.

        // Use builder.addAction(..) to add buttons to answer or reject the call.
        val notificationManager: NotificationManager = context.getSystemService(
            NotificationManager::class.java
        )
        notificationManager.notify(1, notification)


    }

    private fun dropCall() {
        setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
        destroy()
    }
}