package com.yawar.memo.ui.splashPage

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.yawar.memo.BaseApp
import org.json.JSONObject

class SplachActViewModel : ViewModel() {

    var baseApp = BaseApp.instance!!
    var authRepo = baseApp.authRepo


    fun getSpecialNumber(uuid: String): LiveData<JSONObject?>{
        return authRepo.getspecialNumbers(uuid)
    }
    fun getLoading(): LiveData<Boolean> {
        return authRepo.loadingMutableLiveData
    }
    fun getErrorMessage(): LiveData<Boolean> {
        return authRepo.showErrorMessage
    }

    fun setLoading( check : Boolean){
        authRepo.setLoading(check);
    }
    fun setErrorMessage( check : Boolean){
        authRepo.setShowErrMessage(check);
    }

}