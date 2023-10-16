package com.minotawr.storyapp.data

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.minotawr.storyapp.domain.model.Story

class StoryTestPagingSource: PagingSource<Int, LiveData<List<Story>>>() {
    companion object {
        fun snapshot(items: List<Story>): PagingData<Story> =
            PagingData.from(items)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<Story>>> {
        return LoadResult.Page(
            data = emptyList(),
            prevKey = 0,
            nextKey = 1
        )
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<Story>>>) = 0
}