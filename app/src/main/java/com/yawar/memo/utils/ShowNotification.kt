package com.yawar.memo.utils

import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.yawar.memo.R
import com.yawar.memo.notification.NotificationWorker
import com.yawar.memo.service.FirebaseMessageReceiver

    fun showNotification(name : String?, image : String?, body : String?,
                         channel :String, blockedFor :String?, special: String, fcmToken: String, type: String)
    {
        //        myBase.getObserver().addObserver(this);
        var message = ""

        when (type) {
            "imageWeb" -> {
                message = BaseApp.getInstance().baseContext.resources.getString(R.string.n_photo)

            }
            "voice" -> {
                message = BaseApp.getInstance().baseContext.getString(R.string.n_voice)

            }
            "video" -> {
                message = BaseApp.getInstance().baseContext.getString(R.string.n_video)

            }
            "file" -> {
                message = BaseApp.getInstance().baseContext.getString(R.string.n_file)

            }
            "contact" -> {
                message = BaseApp.getInstance().baseContext.getString(R.string.n_contact)

            }
            "location" -> {
                message = BaseApp.getInstance().baseContext.getString(R.string.n_location)

            }
            else -> {
                if (body != null) {
                    message = body
                }

            }

        }

        val inputDataNotification =
            Data.Builder().putString("name", name)
                .putString("image",image)
                .putString("body", message)
                .putString("channel", channel)
                .putString("blockedFor", null)
                .putString("special", "")
                .putString("fcm_token", "")
                .build()

        val notificationWork1 = OneTimeWorkRequest.Builder(
            NotificationWorker::class.java
        )
            .setInputData(inputDataNotification)
            .addTag(FirebaseMessageReceiver.workTag)
            .build()
        WorkManager.getInstance().enqueue(notificationWork1)

    }


