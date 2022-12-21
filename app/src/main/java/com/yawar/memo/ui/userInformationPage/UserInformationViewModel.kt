package com.yawar.memo.ui.userInformationPage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.yawar.memo.model.MediaModel
import com.yawar.memo.model.UserModel
import com.yawar.memo.repositry.BlockUserRepo
import com.yawar.memo.repositry.UserInformationRepo
import com.yawar.memo.utils.BaseApp
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class UserInformationViewModel @Inject constructor (val repository:UserInformationRepo,
                                                     val savedStateHandle: SavedStateHandle,
                                                    val blockUserRepo: BlockUserRepo
): ViewModel() {
    var baseApp = BaseApp.getInstance()
    private val _mute =  MutableLiveData<Boolean>(false)
    val loadingMutableLiveData : LiveData<Boolean>
        get() = _mute

    private val _isBlocked =  MutableLiveData<Boolean>(false)
    val isBlocked : LiveData<Boolean>
        get() = _isBlocked

    private val _state =  MutableLiveData<String>()
    val state : LiveData<String>
        get() = _state

    private var lastSeen: String? = null


    init {
            blockUserRepo.setBlockedForRepo(savedStateHandle.get<String>("blockedFor").toString())


    }


    fun setBlockedFor(blockedFor: String) {
        blockUserRepo.setBlockedForRepo(blockedFor)
    }

    fun blockedFor(): LiveData<String> {
        return blockUserRepo.blockedForRepo
    }

    fun isBlockedd(): LiveData<Boolean> {
        return blockUserRepo.isBlocked
    }

    fun setBlocked(blocked: Boolean) {
        blockUserRepo.setBlockedRepo(blocked)
    }

    fun isUnBlocked(): LiveData<Boolean> {
        return blockUserRepo.isUnBlocked
    }

    fun setUnBlocked(unBlocked: Boolean?) {
        blockUserRepo.setUnBlockedRepo(unBlocked!!)
    }

    fun sendBlockRequest(my_id: String, another_user_id: String) {
        blockUserRepo.sendBlockRequest(my_id, another_user_id)
    }

    fun sendUnBlockRequest(my_id: String, another_user_id: String) {
        blockUserRepo.sendUnbBlockUser(my_id, another_user_id)
    }
    fun mediaRequest(user_id: String, anthor_user_id: String) {
        repository.getMedia(user_id, anthor_user_id)
    }

    fun getMedia(): LiveData<ArrayList<MediaModel>> {
        return repository.mediaModelsMutableLiveData
    }

    fun getUserInfo(): LiveData<UserModel>{
        Log.d("getUserInfo", "getUserInfo: ")
        return repository.userInformation
    }
    fun userInfoRequest(anthor_user_id: String){
        Log.d("getUserInfo", "getUserInfo: ")
        repository.getUserInformation(anthor_user_id)
    }

    fun set_state(state: String) {
        _state.value = state
    }
    fun getLastSeen(): String? {
        return lastSeen
    }

    fun setLastSeen(_lastSeen: String?) {
        lastSeen = _lastSeen
    }
    fun setMute(state: Boolean) {
        _mute.value = state
    }
}

