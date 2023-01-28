package com.yawar.memo

//import com.yawar.memo.repositry.ChatRoomRepoo;
//import com.yawar.memo.repositry.RequestCallRepo;
import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.rxjava2.RxPreferenceDataStoreBuilder
import androidx.datastore.rxjava2.RxDataStore
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.preference.PreferenceManager
import androidx.work.*
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.yawar.memo.observe.FireBaseTokenObserve
import com.yawar.memo.repositry.AuthRepo
import com.yawar.memo.repositry.BlockUserRepo
import com.yawar.memo.repositry.ChatMessageRepoo
import com.yawar.memo.repositry.ChatRoomRepoo
import com.yawar.memo.service.SocketIOService
import com.yawar.memo.sessionManager.ClassSharedPreferences
import com.yawar.memo.work.RefreshChatRoomWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
@HiltAndroidApp
class BaseApp : Application(), LifecycleObserver {
    lateinit var  instance: BaseApp
    var fireBaseTokenObserve: FireBaseTokenObserve? = null
    private var mRequestQueue: RequestQueue? = null
    val applicationScope = CoroutineScope(Dispatchers.Default)
    @Inject
    lateinit var blockUserRepo: BlockUserRepo
    @Inject
    lateinit var authRepo: AuthRepo
    @Inject
    lateinit var chatMessageRepoo: ChatMessageRepoo
    lateinit var darkModeValues: Array<String>
    var classSharedPreferences: ClassSharedPreferences? = null
    var handler = Handler()
    private val myRunnable: Runnable? = null
    private var dataStore: RxDataStore<Preferences>? = null
    @Inject
    lateinit var chatRoomRepoo: ChatRoomRepoo
    private var peerId: String = ""
    override fun onCreate() {
        super.onCreate()
        setMode()
        sInstance = this
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        if (getClassSharedPreferences().user != null) {
//            chatRoomRepoo!!.loadChatRoom(classSharedPreferences!!.user.userId!!)
            delayedInit()
            Log.d(TAG, "onCreate: ")
        }
    }

    private fun delayedInit() = applicationScope.launch {
//        setupRecurringWork()
        chatRoomRepoo!!.loadChatRoom(classSharedPreferences!!.user.userId!!)
    }
//    private fun setupRecurringWork() {
//
//        val constraints = Constraints.Builder()
//            .setRequiredNetworkType(NetworkType.UNMETERED)
//            .setRequiresBatteryNotLow(true)
//            .setRequiresCharging(true)
//            .apply {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    setRequiresDeviceIdle(true)
//                }
//            }.build()
//        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshChatRoomWorker>(1, TimeUnit.MICROSECONDS)
//            .setConstraints(constraints)
//            .build()
//
//        WorkManager.getInstance().enqueueUniquePeriodicWork(
//            RefreshChatRoomWorker.WORK_NAME,
//            ExistingPeriodicWorkPolicy.KEEP,
//            repeatingRequest)
//    }

    fun isActivityVisible(): String {
        return ProcessLifecycleOwner.get().lifecycle.currentState.name
    }

    override fun onTerminate() {
        super.onTerminate()
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
    }

    fun getForceResendingToken(): FireBaseTokenObserve {
        if (fireBaseTokenObserve == null) {
            fireBaseTokenObserve = FireBaseTokenObserve()
        }
        return fireBaseTokenObserve!!
    }

    fun getRequestQueue(): RequestQueue {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(applicationContext)
        }
        return mRequestQueue!!
    }

    @JvmName("getClassSharedPreferences1")
    fun getClassSharedPreferences(): ClassSharedPreferences {
        if (classSharedPreferences == null) {
            classSharedPreferences = ClassSharedPreferences(this)
        }
        return classSharedPreferences!!
    }

    fun getDataStore(): RxDataStore<Preferences> {
        if (dataStore == null) {
            dataStore = RxPreferenceDataStoreBuilder(this,  /*name=*/"settings").build()
        }
        return dataStore!!
    }

    fun setPeerId(peer_id: String?) {
        if (peerId == null) {
            peerId = peer_id!!
        }
    }

    fun <T> addToRequestQueue(req: Request<T>, tag: String?) {
        req.tag = if (TextUtils.isEmpty(tag)) TAG else tag
        getRequestQueue().add(req)
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        req.tag = TAG
        getRequestQueue().add(req)
    }

    fun setMode() {
        darkModeValues = resources.getStringArray(R.array.dark_mode_values)
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
            .getString(getString(R.string.dark_mode), getString(R.string.dark_mode_def_value))
        // Comparing to see which preference is selected and applying those theme settings
        if (pref == darkModeValues[0]) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else if (pref == darkModeValues[1]) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    fun cancelPendingRequests(tag: Any?) {
        if (mRequestQueue != null) {
            mRequestQueue!!.cancelAll(tag)
        }
    }

    @SuppressLint("CheckResult")
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        if (classSharedPreferences!!.user != null) {
            chatMessageRepoo!!.getUnRecivedMessages()
            val service = Intent(this, SocketIOService::class.java)
            service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_JOIN)
            startService(service)
        }
    }

    @SuppressLint("CheckResult")
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onMoveToBackground() {
        if (classSharedPreferences!!.user != null) {
            val service = Intent(this, SocketIOService::class.java)
            service.putExtra(
                SocketIOService.EXTRA_EVENT_TYPE,
                SocketIOService.EVENT_TYPE_DISCONNECT
            )
            startService(service)
        }
    }

    @SuppressLint("CheckResult")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        Log.d(TAG, "onDestroy: ")
    }

    companion object {
        const val TAG = "BaseApp"
        private var sInstance: BaseApp? = null

        @get:Synchronized
        val instance: BaseApp?
            get() {
                if (sInstance == null) {
                    sInstance = BaseApp()
                }
                return sInstance
            }
    }
}

