package com.yawar.memo.repositry

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.yawar.memo.Api.ChatApi
import com.yawar.memo.domain.model.MediaModel
import com.yawar.memo.domain.model.SearchModel
import com.yawar.memo.domain.model.UserModel
import com.yawar.memo.network.SearchPagingSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

class SearchRepoo @Inject constructor(private val chatApi: ChatApi)  {
    fun search( searchParameter : String ): Flow<PagingData<SearchModel>> {
       return Pager(
            config = PagingConfig(pageSize = 10,
                enablePlaceholders = false,
                initialLoadSize = 10),
            pagingSourceFactory = { SearchPagingSource(chatApi,searchParameter)
            }
        ).flow
    }


}