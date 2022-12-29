package com.yawar.memo.ui.settingPage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yawar.memo.Api.ChatApi
//import com.yawar.memo.Api.GdgApi
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.model.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
@HiltViewModel
class SettingsFragmentViewModel  @Inject constructor(val chatApi: ChatApi): ViewModel() {

    private val _userModelRespone = MutableLiveData<UserModel>()
    val userModelRespone: LiveData<UserModel>
    get() = _userModelRespone

    private val _showErrorMessage =  MutableLiveData<Boolean>(false)
    val showErrorMessage : LiveData<Boolean>
    get() = _showErrorMessage

    private val _loadingMutableLiveData =  MutableLiveData<Boolean>(false)
    val loadingMutableLiveData : LiveData<Boolean>
    get() = _loadingMutableLiveData

    private val _isDeleteAccount =  MutableLiveData<Boolean>(false)
    val isDeleteAccount : LiveData<Boolean>
        get() = _isDeleteAccount


    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )

    fun updateImage( id : String,  img :String ) {
        Log.d("updateImage", "register")
        _loadingMutableLiveData.value = true

        coroutineScope.launch {

//            val getResponeDeferred = GdgApi(AllConstants.base_node_url).apiService
//                .updateImage(id,img)
//            val getResponeDeferred = GdgApi.apiService
//                .updateImage(id,img)
            val getResponeDeferred = chatApi
                .updateImage(id,img)

            try {
                Log.d("updateImage", "reg")
                val listResult = getResponeDeferred?.await()
                Log.d("updateImage", listResult.toString())
                val jsonArray =  JSONArray( listResult);
                val respObj = jsonArray.getJSONObject(0);
                val user_id = respObj.getString("id");
                val first_name = respObj.getString("first_name");
                val last_name = respObj.getString("last_name");
                val email = respObj.getString("email");
                val profile_image = respObj.getString("profile_image");
                val secret_number = respObj.getString("sn");
                val number = respObj.getString("phone");
                val status= respObj.getString("status");
                val userModel =  UserModel(user_id,first_name,last_name,email,number,secret_number,profile_image,status);

                _userModelRespone.value = userModel
                _loadingMutableLiveData.value = false



            } catch (e: Exception) {
                Log.d("updateImage",  e.toString())
                _loadingMutableLiveData.value = false
                _showErrorMessage.value = true



            }
        }
    }

    fun updateProfile( id : String,  firstNmae: String, lastName : String ) {
        Log.d("updateProfile", "register")
        _loadingMutableLiveData.value = true

        coroutineScope.launch {

//            val getResponeDeferred = GdgApi(AllConstants.base_url).apiService
//                .updateProfile(firstNmae,lastName,id)
//            val getResponeDeferred = GdgApi.apiService
//                .updateProfile(firstNmae,lastName,id)
            val getResponeDeferred = chatApi
                .updateProfile(firstNmae,lastName,id)
            try {
                Log.d("updateProfile", "reg")

                val listResult = getResponeDeferred?.await()
                Log.d("updateProfile", listResult.toString())


                // on below line we are passing our response
                // to json object to extract data from it.
                val respObj = JSONObject(listResult)
                println(respObj)
                val data: JSONObject = respObj.getJSONObject("data")
                val user_id = data.getString("id")
                val first_name = data.getString("first_name")
                val last_name = data.getString("last_name")
                val email = data.getString("email")
                val profile_image = data.getString("profile_image")
                val secret_number = data.getString("sn")
                val number = data.getString("phone")
                val status = data.getString("status")

                val userModel = UserModel(
                    user_id,
                    first_name,
                    last_name,
                    email,
                    number,
                    secret_number,
                    profile_image,
                    status
                )

                _userModelRespone.value = userModel
                _loadingMutableLiveData.value = false



            } catch (e: Exception) {
                Log.d("updateProfile",  e.toString())
                _loadingMutableLiveData.value = false
                _showErrorMessage.value = true



            }
        }
    }



    fun deleteAccount( id : String, sn:String) {
        Log.d("deleteAccount", "delete")
        _loadingMutableLiveData.value = true


        coroutineScope.launch {

//            val getResponeDeferred = GdgApi(AllConstants.base_url).apiService
//                .deleteAccount(sn, id)
//            val getResponeDeferred = GdgApi.apiService
//                .deleteAccount(sn, id)
            val getResponeDeferred = chatApi
                .deleteAccount(sn, id)
            try {
                Log.d("deleteAccount", "delete")

                val listResult = getResponeDeferred?.await()
                Log.d("deleteAccount", listResult.toString())



                // on below line we are passing our response
                // to json object to extract data from it.
                val respObj = JSONObject(listResult)
                _isDeleteAccount.value = respObj.getBoolean("data")
                _loadingMutableLiveData.value = false



            } catch (e: Exception) {
                Log.d("deleteAccount",  e.toString())
                _loadingMutableLiveData.value = false
                _showErrorMessage.value = true



            }
        }
    }
    fun setLoading(s : Boolean){
        _loadingMutableLiveData.value = s
    }
    fun setIsDeleted(s : Boolean){
        _isDeleteAccount.value = s
    }
    fun setErrorMessage(s : Boolean){
        _showErrorMessage.value = s
    }

    override fun onCleared() {
        viewModelJob.cancel()
        super.onCleared()
    }
}