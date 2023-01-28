package com.yawar.memo.ui.loginPage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yawar.memo.BaseApp
import com.yawar.memo.network.IpGeolocationService
import com.yawar.memo.domain.model.Locationn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class LoginModelView : ViewModel() {
    var baseApp = BaseApp.instance!!
    var authRepo = baseApp.authRepo
    var name: String = ""
    var image : String  = ""
    private val _country = MutableLiveData<String>()
    val country: LiveData<String>
        get() = _country


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

    fun getCountry() {
        viewModelScope.launch {
            val ipAddress = getPublicIpAddress()
            val country = getCountryFromIpAddress(ipAddress)
            _country.value = country
        }
    }

    suspend fun getCountryFromIpAddress(ipAddress: String): String {
        try {


            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.ipgeolocation.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            Log.d("getCountryFromIpAddress: ", ipAddress)
            val service = retrofit.create(IpGeolocationService::class.java)
            val location: Locationn = withContext(Dispatchers.IO) {
                service.getLocation("c305e2311182401382bef9f4472da80c", ipAddress)
            }
            return location.country_code2


    } catch (e: Exception) {
        // handle the exceptio
        return ""
    }
    }

    suspend fun getPublicIpAddress(): String {


        try {

            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.ipify.org")
                .addConverterFactory(ScalarsConverterFactory.create())

                .build()
            val service = retrofit.create(IpGeolocationService::class.java)
            val ipAddress = service.getIpAddress()
            Log.d("getPublicIpAddress", "getPublicIpAddress:${ipAddress} ")
            return ipAddress

        } catch (e: Exception) {
            // handle the exceptio
            return ""
        }

    }


}