package com.yawar.memo.ui.searchPage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yawar.memo.Api.GdgApi
import com.yawar.memo.model.SearchRespone
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class SearchModelView : ViewModel() {
    private val _searchResponeArrayList = MutableLiveData<ArrayList<SearchRespone?>?>()
    val searchResponeArrayList: LiveData<ArrayList<SearchRespone?>?>
        get() = _searchResponeArrayList


    private val _loadingMutableLiveData =  MutableLiveData<Boolean>(false)
    val loadingMutableLiveData : LiveData<Boolean>
        get() = _loadingMutableLiveData


    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )

    fun search( searchParameter : String,  page :String,  myId :String ): MutableLiveData<ArrayList<SearchRespone?>?>  {
        if (searchParameter.isEmpty()) {
            _loadingMutableLiveData.postValue(true)
        }
        val searchResponeArrayList =  ArrayList<SearchRespone?>()

        coroutineScope.launch {

//            val getResponeDeferred = GdgApi(AllConstants.base_url).apiService
//                .search(searchParameter,page,myId)
            val getResponeDeferred = GdgApi.apiService
                .search(searchParameter,page,myId)

            try {
                val listResult = getResponeDeferred?.await()
                _loadingMutableLiveData.value = false

                var respObj: JSONObject? = null

                println("search$searchParameter")

                    searchResponeArrayList.clear()

                    respObj = JSONObject(listResult)
                    val jsonArray = respObj["data"] as JSONArray
                    println(jsonArray.length().toString() + "jsonArray.length()")

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val id = jsonObject.getString("id")
                        val phone = jsonObject.getString("phone")
                        val name =
                            jsonObject.getString("first_name") + " " + jsonObject.getString("last_name")
                        val secretNumber = jsonObject.getString("sn")
                        val image = jsonObject.getString("image")
                        val token = jsonObject.getString("token")
                        val blockedFor = jsonObject.getString("blocked_for")
                        searchResponeArrayList.add(
                            SearchRespone(
                                id,
                                name,
                                secretNumber,
                                image,
                                phone,
                                token,
                                blockedFor,
                                true
                            )
                        )
                    }

                    _searchResponeArrayList.value = searchResponeArrayList


            } catch (e: Exception) {
                _loadingMutableLiveData.value = false


                Log.d("getMarsRealEstateProperties: ","Failure: ${e.message}")

            }
        }
        return _searchResponeArrayList

    }

    override fun onCleared() {
        viewModelJob.cancel()
        super.onCleared()
    }


}