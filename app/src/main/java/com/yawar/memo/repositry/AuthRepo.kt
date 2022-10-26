package com.yawar.memo.repositry

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yawar.memo.Api.GdgApi
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.utils.BaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.UUID

class AuthRepo {

    private val _jsonObjectMutableLiveData = MutableLiveData<JSONObject?>()
    val jsonObjectMutableLiveData: LiveData<JSONObject?>
        get() = _jsonObjectMutableLiveData

    private val _loadingMutableLiveData =  MutableLiveData<Boolean>()
    val loadingMutableLiveData : LiveData<Boolean>
        get() = _loadingMutableLiveData


    private val _showErrorMessage =  MutableLiveData<Boolean>(false)
    val showErrorMessage : LiveData<Boolean>
        get() = _showErrorMessage

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )


    fun getspecialNumbers(uuid :String ): LiveData<JSONObject?> {
        _loadingMutableLiveData.value = true
        coroutineScope.launch {
//            val getChatRoomsDeferred = GdgApi(AllConstants.base_url_final).apiService.getSpecialNumbers(phoneNumber)
            val getChatRoomsDeferred = GdgApi(AllConstants.base_url).apiService.getSpecialNumbers(uuid)

            try {
                val listResult = getChatRoomsDeferred?.await()
                val respObj = JSONObject(listResult)
                val data: JSONObject = respObj.getJSONObject("data")
                _jsonObjectMutableLiveData.value = data
                _loadingMutableLiveData.value =  false
                _showErrorMessage.value = false

            } catch (e: Exception) {
                _loadingMutableLiveData.value =  false
                _showErrorMessage.value = true
                Log.d("getMarsRealEstateProperties: ","Failure: ${e.message}")

            }
        }
        return jsonObjectMutableLiveData

    }

    fun sendFcmToken( user_id : String,  token: String ) {
        coroutineScope.launch {
//            val getChatRoomsDeferred = GdgApi(AllConstants.base_url_final).apiService.sendFcmToken(user_id,token)
            val getChatRoomsDeferred = GdgApi(AllConstants.base_url).apiService.sendFcmToken(user_id,token)

            try {
                val listResult = getChatRoomsDeferred?.await()
                BaseApp.getInstance().classSharedPreferences.fcmToken = token


            } catch (e: Exception) {

                Log.d("getMarsRealEstateProperties: ","Failure: ${e.message}")

            }
        }

    }
    fun setLoading (state : Boolean){
        _loadingMutableLiveData.value = state
    }
    fun setShowErrMessage (state : Boolean){
        _showErrorMessage.value = state
    }
    fun setjsonObjectMutableLiveData(  data:JSONObject?){
        _jsonObjectMutableLiveData.value = data
    }


}