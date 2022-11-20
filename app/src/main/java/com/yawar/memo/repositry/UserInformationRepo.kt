package com.yawar.memo.repositry

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yawar.memo.Api.GdgApi
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.model.MediaModel
import com.yawar.memo.model.UserModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class UserInformationRepo {
    private val _mediaModelsMutableLiveData = MutableLiveData<ArrayList<MediaModel>>()
    val mediaModelsMutableLiveData: LiveData<ArrayList<MediaModel>>
        get() = _mediaModelsMutableLiveData

    private val _blockedFor =  MutableLiveData<String>()
    val blockedFor : LiveData<String>
        get() = _blockedFor

    private val _userInformation = MutableLiveData<UserModel>()
    val userInformation: LiveData<UserModel>
        get() = _userInformation

    private val _blocked =  MutableLiveData<Boolean>()
    val blocked : LiveData<Boolean>
        get() = _blocked

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )

    fun  getMedia( user_id : String ,  anthor_user_id :String){
        var mediaModels = ArrayList<MediaModel>()
        _mediaModelsMutableLiveData.value = ArrayList()
        coroutineScope.launch {

//            val getMediaDeferred = GdgApi(AllConstants.base_node_url).apiService
//                .getMedia(user_id,anthor_user_id)
            val getMediaDeferred = GdgApi.apiService
                .getMedia(user_id,anthor_user_id)

            try {
                val listResult = getMediaDeferred?.await()

                val jsonArray = JSONArray(listResult)

                for (i in 0 until jsonArray.length()) {
                    val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                    val image = jsonObject.getString("message")
                    mediaModels.add(MediaModel(image))
                }
                _mediaModelsMutableLiveData.value = mediaModels


            } catch (e: Exception) {

                Log.d("getMarsRealEstateProperties: ","Failure: ${e.message}")

            }
        }

    }

    fun  getUserInformation( anthor_user_id :String) {
        var mediaModels = ArrayList<MediaModel>()
        _userInformation.value = UserModel()

        coroutineScope.launch {

//            val getMediaDeferred =  GdgApi(AllConstants.base_node_url).apiService
//                .getUserInformation(anthor_user_id)
            val getMediaDeferred =  GdgApi.apiService
                .getUserInformation(anthor_user_id)
            Log.d("getUserInformation", "getUserInformation: ")

            try {
                val listResult = getMediaDeferred?.await()

                Log.d("getUserInformation", listResult.toString())

                val jsonArray = JSONArray(listResult)
                val jsonObject: JSONObject = jsonArray.getJSONObject(0)
                val userModel = UserModel(
                    jsonObject.getString("id"),
                    jsonObject.getString("first_name"),
                    jsonObject.getString("last_name"),
                    jsonObject.getString("email"),
                    jsonObject.getString("phone"),
                    jsonObject.getString("sn"),
                    jsonObject.getString("profile_image"),
                    jsonObject.getString("status")
                )
                _userInformation.value = userModel


            } catch (e: Exception) {

                Log.d("getMarsRealEstateProperties: ", "Failure: ${e.message}")

            }
        }

    }

}