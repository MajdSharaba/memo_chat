package com.yawar.memo.repositry

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.yawar.memo.Api.ChatApi
import com.yawar.memo.database.dao.ChatRoomDatabase
import com.yawar.memo.database.entity.callHistoryEntity.CallHistoryEntityMapper
import com.yawar.memo.database.entity.chatRoomEntity.ChatRoomEntityMapper
import com.yawar.memo.domain.model.CallHistoryModel
import com.yawar.memo.domain.model.ChatRoomModel
import com.yawar.memo.network.networkModel.callHistoryModel.CallHistoryDto
import com.yawar.memo.network.networkModel.callHistoryModel.CallHistoryDtoMapper
import com.yawar.memo.network.networkModel.chatRoomModel.ChatRoomDtoMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CallHistoryRepo @Inject  constructor(
    private val chatApi: ChatApi,
    private val database : ChatRoomDatabase,
    private val callHistoryDtoMapper: CallHistoryDtoMapper,
    private val callHistoryEntityMapper : CallHistoryEntityMapper,

){
    val callModelListMutableLiveData: LiveData<List<CallHistoryModel>>
        = database.chatRoomDao.getCallHistory().map {
        callHistoryEntityMapper.toDomainList(it) as List<CallHistoryModel>
    }

    private val _loadingMutableLiveData =  MutableLiveData<Boolean>(false)
    val loadingMutableLiveData : LiveData<Boolean>
        get() = _loadingMutableLiveData


    suspend fun loadData(my_id : String) {
//        _loadingMutableLiveData.value = true

        withContext(Dispatchers.IO) {

//            val getResponeDeferred =GdgApi(AllConstants.base_node_url).apiService
//                .getMyCalls(my_id)
//            val getResponeDeferred =GdgApi.apiService
//                .getMyCalls(my_id)
            val getResponeDeferred = chatApi
                .getMyCalls(my_id)
            try {
                val listResult = getResponeDeferred?.await()
                val jsonArray = listResult as List<CallHistoryDto>
                Log.d("getMarsRealEstateProperties: ", jsonArray.toString())

                database.chatRoomDao.insertAllCalls(*(callHistoryDtoMapper.toEntityList(listResult)))
                Log.d("getMarsRealEstateProperties: ", jsonArray.size.toString())
            } catch (e: Exception) {


                Log.d("getMarsRealEstateProperties: ", "Failure: ${e.message}")

            }
        }
//        _loadingMutableLiveData.value = false


    }

    }

