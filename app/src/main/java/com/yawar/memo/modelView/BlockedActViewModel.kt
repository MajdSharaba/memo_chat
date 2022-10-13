package com.yawar.memo.modelView

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.yawar.memo.model.UserModel
import com.yawar.memo.utils.BaseApp

class BlockedActViewModel : ViewModel() {
    var baseApp = BaseApp.getInstance()
    private val blockUserRepo = baseApp.blockUserRepo

    init {
        blockUserRepo.getUserBlock(baseApp.classSharedPreferences.user.userId.toString())
    }

    fun setBlockedFor(blockedFor: String?) {
        blockUserRepo.setBlockedForRepo(blockedFor!!)
    }

    fun blockedFor(): LiveData<String> {
//        return blockUserRepo.blockedForRepo;
        return blockUserRepo.blockedForRepo
    }

    fun setBlocked(blocked: Boolean?) {
        blockUserRepo.setBlockedRepo(blocked!!)
    }
    fun  isBlocked() : LiveData<Boolean>{
        return blockUserRepo.isBlocked;
    }
        fun  isUnBlocked() : LiveData<Boolean>{
        return blockUserRepo.isUnBlocked;
    }

    fun setUnBlocked(unBlocked: Boolean?) {
        blockUserRepo.setUnBlockedRepo(unBlocked!!)
    }

    fun sendBlockRequest(my_id: String?, another_user_id: String?) {
        blockUserRepo.sendBlockRequest(my_id!!, another_user_id!!)
    }

    fun sendUnBlockRequest(my_id: String?, another_user_id: String?) {
        blockUserRepo.sendUnbBlockUser(my_id!!, another_user_id!!)
    }

    fun loadData(): LiveData<ArrayList<UserModel?>?> {
        return blockUserRepo.userBlockListMutableLiveData
    }
}
