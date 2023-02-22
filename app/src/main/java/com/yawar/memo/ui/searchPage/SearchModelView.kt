package com.yawar.memo.ui.searchPage

//import com.yawar.memo.Api.GdgApi
import androidx.lifecycle.*
import androidx.paging.*
import com.yawar.memo.domain.model.SearchModel
import com.yawar.memo.repositry.SearchRepoo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject


@HiltViewModel
class SearchModelView  @Inject constructor(val searchRepoo: SearchRepoo): ViewModel() {

    private val _searchModelArrayList = MutableLiveData<ArrayList<SearchModel?>?>()
    val searchResponeArrayList: LiveData<ArrayList<SearchModel?>?>
        get() = _searchModelArrayList
    ////////////
    private val currentQuery = MutableLiveData(DEFAULT_QUERY)

    val items = currentQuery.switchMap { queryString ->
        searchRepoo.search(queryString).asLiveData()
    }.asFlow()
        .cachedIn(viewModelScope)
    //////////////

    private val _loadingMutableLiveData =  MutableLiveData<Boolean>(false)
    val loadingMutableLiveData : LiveData<Boolean>
        get() = _loadingMutableLiveData


    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )
    init {
//        search("","","")
    }



    override fun onCleared() {
        viewModelJob.cancel()
        super.onCleared()
    }
    fun searchQuery(query: String){
        currentQuery.postValue(query)
    }

    companion object{
        private const val DEFAULT_QUERY = ""
    }


}