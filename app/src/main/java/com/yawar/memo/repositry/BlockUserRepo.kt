package com.yawar.memo.repositry

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yawar.memo.Api.ChatApi
import com.yawar.memo.BaseApp
//import com.yawar.memo.Api.GdgApi
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.domain.model.UserModel
//import com.yawar.memo.repositry.chatRoomRepo.ChatRoomRepoImp
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import javax.inject.Inject


//  class BlockUserRepo @Inject constructor(private val chatRoomRepoo: ChatRoomRepoImp) {
class BlockUserRepo  @Inject constructor(private val chatApi: ChatApi,
                                         private val chatRoomRepoo: ChatRoomRepoo) {

    var myBase = BaseApp.instance
//    var chatRoomRepoo = myBase.chatRoomRepoo

    private val _userBlockListMutableLiveData = MutableLiveData<ArrayList<UserModel?>?>()
    val userBlockListMutableLiveData: LiveData<ArrayList<UserModel?>?>
        get() = _userBlockListMutableLiveData


    private val _blockedForRepo = MutableLiveData<String>()
    val blockedForRepo: LiveData<String>
        get() = _blockedForRepo


    private val _blockedRepo =  MutableLiveData<Boolean>()
    val isBlocked : LiveData<Boolean>
        get() = _blockedRepo



      private val _unBlockedRepo =  MutableLiveData<Boolean>()
    val isUnBlocked  : LiveData<Boolean>
        get() = _unBlockedRepo


      private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )

     fun getUserBlock( user_id: String) {
         coroutineScope.launch {
             val userBlockList = ArrayList<UserModel?>()

//
//             var getBlockDeferred = GdgApi(AllConstants.base_node_url).apiService
//
//                 .getBlockKist(user_id)
//             var getBlockDeferred = GdgApi.apiService
//
//                 .getBlockKist(user_id)
             var getBlockDeferred = chatApi

                 .getBlockKist(user_id)


             try {
                 var listResult = getBlockDeferred?.await()

                 val jsonArray = JSONArray(listResult) as JSONArray
                 Log.d("getUserBlock", jsonArray.length().toString())
                 for (i in 0 .. jsonArray.length()-1) {

                     val jsonObject = jsonArray.getJSONObject(i)

                     val image = jsonObject.getString("profile_image")
                     val special_number = jsonObject.getString("sn")
                     val fName = jsonObject.getString("first_name")
                     val lName = jsonObject.getString("last_name")
                     val phone = jsonObject.getString("phone")
                     val userId = jsonObject.getString("id")
                     val email = jsonObject.getString("email")

                     userBlockList.add(
                         UserModel(userId,
                         fName,
                         lName,
                         email,
                         phone,
                         special_number,
                         image,
                         "null")
                     )


                 }
                 Log.d("_userBlockListMutableLiveData.value", userBlockList.size.toString())


                 _userBlockListMutableLiveData.value = userBlockList


             } catch (e: Exception) {


                 Log.d("getMarsRealEstateProperties: ","Failure: ${e.message}")

             }
         }
     }

    fun addBlockUser(userModel: UserModel) {
        var searchBlock = false
        var  userBlockList = _userBlockListMutableLiveData.value

        if (userBlockList != null) {
            for (user in userBlockList) {
                if (user != null) {
                    if (user.userId == userModel.userId) {
                        user.status = userModel.status
                        searchBlock = true
                        break
                    }
                }
            }
        }
        if (!searchBlock) {
            if (userBlockList != null) {
                userBlockList.add(0, userModel)
            }
        }
        _userBlockListMutableLiveData.value = userBlockList
    }


    fun sendBlockRequest( my_id: String,  anthor_user_id:String) {
        coroutineScope.launch {
            Log.d("sendBlockRequest", "sendBlockRequest: ")
//
//            val respone =   GdgApi(AllConstants.base_node_url).apiService.blockUser(my_id, anthor_user_id)

//            val respone =   GdgApi.apiService.blockUser(my_id, anthor_user_id)
            val respone =   chatApi.blockUser(my_id, anthor_user_id)




            try {
                val listResult = respone?.await()

                var blokedForRespone = ""
                var blockedRespone = false

                val jsonObject =   JSONObject(listResult.toString())
                blokedForRespone = jsonObject.getString("blocked_for")
                blockedRespone = jsonObject.getBoolean("blocked")
                _blockedForRepo.value = blokedForRespone
                Log.d("sendBlockUser", blokedForRespone)


                chatRoomRepoo.setBlockedState(anthor_user_id, blokedForRespone)
                _blockedRepo.value = true

            } catch (e: Exception) {


                Log.d("getMarsRealEstateProperties: ", "Failure: ${e.message}")

            }

        }

    }


    fun sendUnbBlockUser( my_id: String,  anthor_user_id:String) {
        coroutineScope.launch {

            Log.d("sendUnbBlockUser", "sendUnbBlockUser: ")

//            var respone = GdgApi(AllConstants.base_node_url).apiService
//                .unBlockUser(my_id, anthor_user_id)
//            var respone = GdgApi.apiService
//                .unBlockUser(my_id, anthor_user_id)
            var respone = chatApi
                .unBlockUser(my_id, anthor_user_id)
            try {
                val listResult = respone?.await()

                var blokedForRespone = ""
                val unBlockedRespone = false

                try {

                    val jsonObject = JSONObject(listResult)
                    blokedForRespone = jsonObject.getString("blocked_for")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                _blockedForRepo.value = blokedForRespone
                _unBlockedRepo.value = true
                Log.d("sendUnbBlockUser", blokedForRespone)


                chatRoomRepoo.setBlockedState(anthor_user_id, blokedForRespone)
                deleteBlockUser(anthor_user_id)


            } catch (e: Exception) {


                Log.d("getMarsRealEstateProperties: ", "Failure: ${e.message}")

            }

        }

    }
    fun deleteBlockUser(user_id: String) {
        val userBlockList = _userBlockListMutableLiveData.value

        if (userBlockList != null) {
            for (user in userBlockList) {
                if (user != null) {
                    if (user.userId == user_id) {
                        //                    user.setStatus(status);
                        userBlockList.remove(user)
                        break
                    }
                }
            }
        }
        _userBlockListMutableLiveData.value = userBlockList
    }

    fun setBlockedForRepo(blockedForRepo: String) {
        _blockedForRepo.value = blockedForRepo
    }
    fun setBlockedRepo(blockedRepo: Boolean) {
        _blockedRepo.value = blockedRepo
    }



    fun setUnBlockedRepo(blockedRepo: Boolean) {
        _unBlockedRepo.value = blockedRepo
    }
      fun setUserBlockListMutableLiveData(data : ArrayList<UserModel?>?) {
          _userBlockListMutableLiveData.value = data
      }

}