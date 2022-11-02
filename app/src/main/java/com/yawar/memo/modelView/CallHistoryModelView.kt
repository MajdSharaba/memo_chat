package com.yawar.memo.modelView

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yawar.memo.Api.GdgApi
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.model.CallModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

class CallHistoryModelView : ViewModel() {

    var date: Date? = null


    private val _callModelListMutableLiveData = MutableLiveData<ArrayList<CallModel?>?>()
    val callModelListMutableLiveData: LiveData<ArrayList<CallModel?>?>
        get() = _callModelListMutableLiveData

    private val _loadingMutableLiveData =  MutableLiveData<Boolean>(false)
    val loadingMutableLiveData : LiveData<Boolean>
        get() = _loadingMutableLiveData
    var format: SimpleDateFormat = SimpleDateFormat("mm:ss")


    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )


    fun loadData( my_id : String): MutableLiveData<ArrayList<CallModel?>?>  {
        _loadingMutableLiveData.value = true
        val callModelsList =  ArrayList<CallModel?>()

        coroutineScope.launch {

//            val getResponeDeferred =GdgApi(AllConstants.base_node_url).apiService
//                .getMyCalls(my_id)
            val getResponeDeferred =GdgApi.apiService
                .getMyCalls(my_id)
            try {
                val listResult = getResponeDeferred?.await()
                _loadingMutableLiveData.value = false
                Log.d("ViewModel",listResult.toString() )

                val jsonArray = JSONArray(listResult)

                for (i in 0 until jsonArray.length()) {
                    val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                    val userName =
                        jsonObject.getString("first_name") + " " + jsonObject.getString("last_name")
                    val id = jsonObject.getString("id")
                    val caller_id = jsonObject.getString("caller")
                    val call_type = jsonObject.getString("call_type")
                    val call_status = jsonObject.getString("call_state")
                    val image = jsonObject.getString("profile_image")
                    val answer_id = jsonObject.getString("answer")
                    val duration = jsonObject.getString("duration")
                    val createdAt = jsonObject.getString("call_time")
                    callModelsList.add(
                        CallModel(
                            id,
                            userName,
                            caller_id,
                            image,
                            call_type,
                            answer_id,
                            call_status,
                            duration,
                            createdAt
                        )
                    )
                }

                _callModelListMutableLiveData.value = callModelsList


            } catch (e: Exception) {
                _loadingMutableLiveData.value = false


                Log.d("getMarsRealEstateProperties: ","Failure: ${e.message}")

            }
        }
        return _callModelListMutableLiveData

    }

    override fun onCleared() {
        viewModelJob.cancel()
        super.onCleared()
    }

}