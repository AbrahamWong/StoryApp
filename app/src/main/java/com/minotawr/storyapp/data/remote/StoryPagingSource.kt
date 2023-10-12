package com.minotawr.storyapp.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.minotawr.storyapp.data.remote.network.ApiService
import com.minotawr.storyapp.domain.model.Story
import com.minotawr.storyapp.utils.StoryMapper

class StoryPagingSource(private val apiService: ApiService): PagingSource<Int, Story>() {
    private companion object {
        const val INITIAL_PAGE_INDEX = 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val response = apiService.getStories(page = position, params.loadSize, isLocationRequired = 0).body()
            val entity = StoryMapper.storyListResponseToEntity(response)
            val data = StoryMapper.storyListEntityToModel(entity)

            LoadResult.Page(
                data = data ?: throw Exception(),
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (data.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}