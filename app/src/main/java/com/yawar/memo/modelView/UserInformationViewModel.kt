package com.yawar.memo.modelView

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yawar.memo.model.MediaModel
import com.yawar.memo.model.UserModel
import com.yawar.memo.utils.BaseApp

class UserInformationViewModel: ViewModel() {
    var baseApp = BaseApp.getInstance()
    private val repository = baseApp.userInformationRepo
    private val blockUserRepo = baseApp.blockUserRepo
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

    fun getUserInfo(anthor_user_id: String): LiveData<UserModel>{
        return repository.getUserInformation(anthor_user_id)
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
