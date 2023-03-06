package com.yawar.memo.ui.registerPage



import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yawar.memo.Api.ChatApi
//import com.yawar.memo.Api.GdgApi
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.domain.model.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
import javax.inject.Inject
@HiltViewModel
class RegisterViewModel  @Inject constructor(val chatApi: ChatApi): ViewModel() {

    private val _userModelRespone = MutableLiveData<UserModel>()
    val userModelRespone: LiveData<UserModel>
        get() = _userModelRespone

    private val _showErrorMessage =  MutableLiveData<Boolean>(false)
    val showErrorMessage : LiveData<Boolean>
        get() = _showErrorMessage

    private val _loadingMutableLiveData =  MutableLiveData<Boolean>(false)
    val loadingMutableLiveData : LiveData<Boolean>
        get() = _loadingMutableLiveData


    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )

    fun register( email : String,  img :String,  firstName :String,
                  lastName : String,  sn :String,  phone :String,
                  uuid: String ) {
        Log.d("register", "register")
            _loadingMutableLiveData.value = true

        coroutineScope.launch {

//            val getResponeDeferred = GdgApi(AllConstants.base_node_url).apiService
//                .register(email,img,firstName,lastName,sn,phone,uuid)
//            val getResponeDeferred = GdgApi.apiService
//                .register(email,img,firstName,lastName,sn,phone,uuid)
            val getResponeDeferred = chatApi
                .register(email,img,firstName,lastName,sn,phone,uuid)
            try {
                Log.d("register", "reg")
                val listResult = getResponeDeferred?.await()
                Log.d("register", listResult.toString())
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
                Log.d("register", "reg${secret_number}")

                _userModelRespone.value = userModel
                _loadingMutableLiveData.value = false

            } catch (e: Exception) {
                Log.d("register",  e.toString())
                _loadingMutableLiveData.value = false
                _showErrorMessage.value = true



            }
        }
    }
    fun setLoading(s : Boolean){
        _loadingMutableLiveData.value = s
    }
    fun setErrorMessage(s : Boolean){
        _showErrorMessage.value = s
    }
    override fun onCleared() {
        viewModelJob.cancel()
        super.onCleared()
    }
}