package com.yawar.memo.network

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.yawar.memo.Api.ChatApi
import com.yawar.memo.BaseApp
import com.yawar.memo.domain.model.SearchModel
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException
import java.time.LocalDateTime
import kotlin.math.max


private  const val UNSPLASH_STARTING_PAGE_INDEX = 1
class SearchPagingSource(
    private val chatApi: ChatApi,
    private val query: String,


    ) : PagingSource<Int,SearchModel>() {
    override fun getRefreshKey(state: PagingState<Int, SearchModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val article = state.closestItemToPosition(anchorPosition) ?: return null
        return ensureValidKey(key = anchorPosition - (state.config.pageSize / 2))
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchModel> {
        Log.d("searchModels", "load:$} ")
        val position = params.key ?: UNSPLASH_STARTING_PAGE_INDEX



///https://www.youtube.com/watch?v=y2M8gLBUeW4&list=RDCMUC_Fh8kvtkVPkeihBs42jGcA&index=1
       return  try {
           val respone = chatApi.search(
               query, position.toString(),
               BaseApp.instance?.classSharedPreferences?.user?.userId
           )
           val startKey = params.key ?: UNSPLASH_STARTING_PAGE_INDEX



           val searchModels = respone?.data!!
           Log.d("searchModels", "load:${searchModels} ")
           LoadResult.Page(
               data = searchModels,
               prevKey = null
               ,
               nextKey = if (searchModels.isEmpty()) null else position + 1
           )


       } catch (exception : IOException){
           Log.d("IOException", "load:${exception} ")
           LoadResult.Error(exception)
       } catch (exception : HttpException){
           Log.d("IOException", "load:${exception} ")
           LoadResult.Error(exception)

       }
    }
    private fun ensureValidKey(key: Int) = max(UNSPLASH_STARTING_PAGE_INDEX, key)

}
