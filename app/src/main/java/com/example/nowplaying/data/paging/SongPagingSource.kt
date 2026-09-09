package com.example.nowplaying.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.nowplaying.data.model.Song
import com.example.nowplaying.data.network.ApiInterface

class SongPagingSource (
    private val apiService: ApiInterface,
    private val device_id: String
) : PagingSource<Int, Song>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Song> {
        try {
            val currentOffset = params.key ?:0
            val response = apiService.getHistory(
                device_id = device_id,
                limit = params.loadSize,
                offset = currentOffset
                )

            return LoadResult.Page(
                data = response,
                prevKey = if(currentOffset == 0) null else currentOffset - params.loadSize,
                nextKey = if(response.isEmpty()) null else currentOffset + params.loadSize
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)

        }
    }

    override fun getRefreshKey(state: PagingState<Int, Song>): Int? {
        return  state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(state.config.pageSize)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(state.config.pageSize)
        }
    }
}