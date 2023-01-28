package com.yawar.memo.ui.CallHistoryPage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yawar.memo.Api.ChatApi
import com.yawar.memo.BaseApp
//import com.yawar.memo.Api.GdgApi
import com.yawar.memo.domain.model.CallHistoryModel
import com.yawar.memo.network.networkModel.callHistoryModel.CallHistoryDto
import com.yawar.memo.repositry.CallHistoryRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class CallHistoryModelView @Inject constructor(val calllHistoryRepo: CallHistoryRepo): ViewModel() {
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )
    init {
        coroutineScope.launch {
            calllHistoryRepo.loadData(BaseApp.instance?.classSharedPreferences?.user?.userId.toString())
        }
    }
    val loadingMutableLiveData : LiveData<Boolean>
        get() = calllHistoryRepo.loadingMutableLiveData

//    var date: Date? = null








    fun loadData( ): LiveData<List<CallHistoryModel>>  {

        return calllHistoryRepo.callModelListMutableLiveData

    }


    override fun onCleared() {
        viewModelJob.cancel()
        super.onCleared()
    }

}