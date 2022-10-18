package com.yawar.memo.modelView

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.yawar.memo.utils.BaseApp
import org.json.JSONObject

class VerficationViewModel : ViewModel() {
    var baseApp = BaseApp.getInstance()
    var authRepo = baseApp.authRepo

    fun getSpecialNumber(): LiveData<JSONObject?> {
        return authRepo.jsonObjectMutableLiveData
    }

        fun getLoading(): LiveData<Boolean> {
            return authRepo.loadingMutableLiveData
        }

        fun getErrorMessage(): LiveData<Boolean> {
            return authRepo.showErrorMessage
        }
    fun setLoading(check: Boolean) {
        authRepo.setLoading(check)
    }
    fun setErrorMessage(check: Boolean) {
        authRepo.setShowErrMessage(check)
    }


}