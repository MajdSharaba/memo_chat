package com.yawar.memo.modelView

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Build
import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.yawar.memo.Api.GdgApi
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.model.ContactModel
import com.yawar.memo.model.SendContactNumberResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class ContactNumberViewModel : ViewModel() {
    var date: Date? = null


    private val _contactModelListMutableLiveData = MutableLiveData<ArrayList<SendContactNumberResponse?>?>()
    val contactModelListMutableLiveData: LiveData<ArrayList<SendContactNumberResponse?>?>
        get() = _contactModelListMutableLiveData

    private val _loadingMutableLiveData =  MutableLiveData<Boolean>(false)
    val loadingMutableLiveData : LiveData<Boolean>
        get() = _loadingMutableLiveData


    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )


    fun loadData( arrayList : ArrayList<ContactModel>, my_id : String): MutableLiveData<ArrayList<SendContactNumberResponse?>?> {
        _loadingMutableLiveData.value = true
        val callModelsList =  ArrayList<SendContactNumberResponse?>()

        // on below line we are passing our key
        // and value pair to our parameters.
        val data = Gson().toJson(arrayList)


        coroutineScope.launch {
//            val getResponeDeferred = GdgApi.apiService.sendContactNumber(data,my_id)
//            val getResponeDeferred = GdgApi(AllConstants.base_url_final).apiService
//                .sendContactNumber(data,my_id)
            val getResponeDeferred = GdgApi(AllConstants.base_url).apiService
                .sendContactNumber(data,my_id)


            try {
                val listResult = getResponeDeferred?.await()
                _loadingMutableLiveData.value = false
                val  respObj = JSONObject(listResult)

                val jsonArray =  respObj.get("data") as JSONArray

                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    println(jsonObject.getString("name"))
                    val id = jsonObject.getString("id")
                    val name = jsonObject.getString("name")
                    val number = jsonObject.getString("number")
                    val image = jsonObject.getString("image")
                    val chat_id = jsonObject.getString("chat_id")
                    val fcm_token = jsonObject.getString("user_token")
                    val state = jsonObject.getString("state")
                    val app_path = jsonObject.getString("app_path")
                    val blockedFor = jsonObject.getString("blocked_for")
                    callModelsList.add(SendContactNumberResponse(id, name, number, image, state,chat_id,fcm_token,blockedFor,app_path));
                }

                _contactModelListMutableLiveData.value = callModelsList


            } catch (e: Exception) {
                _loadingMutableLiveData.value = false



            }
        }
        return _contactModelListMutableLiveData

    }

    override fun onCleared() {
        viewModelJob.cancel()
        super.onCleared()
    }


}