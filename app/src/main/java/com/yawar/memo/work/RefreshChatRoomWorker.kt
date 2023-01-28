package com.yawar.memo.work

import android.bluetooth.BluetoothStatusCodes.SUCCESS
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.nearby.connection.Payload
import com.yawar.memo.BaseApp
import com.yawar.memo.repositry.ChatRoomRepoo
import retrofit2.HttpException

class RefreshChatRoomWorker ( val chatRoomRepoo: ChatRoomRepoo, appContext: Context, params: WorkerParameters):
    CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        return try {
            chatRoomRepoo.loadChatRoom(BaseApp.instance?.classSharedPreferences?.user?.userId!!)
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }


    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

}