package com.minotawr.storyapp.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.google.gson.Gson
import com.minotawr.storyapp.data.local.database.StoryDatabase
import com.minotawr.storyapp.data.local.entity.RemoteKeys
import com.minotawr.storyapp.data.local.entity.StoryEntity
import com.minotawr.storyapp.data.remote.network.ApiService
import com.minotawr.storyapp.data.remote.network.BaseStoryResponse
import com.minotawr.storyapp.data.remote.network.Resource
import com.minotawr.storyapp.utils.StoryMapper
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database: StoryDatabase,
    private val apiService: ApiService
): RemoteMediator<Int, StoryEntity>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 0
    }

    override suspend fun initialize(): InitializeAction = InitializeAction.LAUNCH_INITIAL_REFRESH

    override suspend fun load(loadType: LoadType, state: PagingState<Int, StoryEntity>): MediatorResult {
        // val page = INITIAL_PAGE_INDEX
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)

                prevKey
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)

                nextKey
            }

        }

        try {
            val response = apiService.getStories(page, state.config.pageSize, 0)
            if (!response.isSuccessful) throw Exception("Request failed")

            val responseData = apiService.getStories(page, state.config.pageSize, 0).body()

            val endOfPaginationReached = responseData?.storyList?.isEmpty() == true

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.getRemoteKeysDao().deleteRemoteKeys()
                    database.getStoryDao().deleteStoryList()
                }

                val prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = responseData?.storyList?.map {
                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }

                if (keys != null)
                    database.getRemoteKeysDao().insertAll(keys)

                val entity = StoryMapper.storyListResponseToEntity(responseData)
                if (entity != null)
                    database.getStoryDao().insertStoryList(entity)
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryEntity>): RemoteKeys? =
        state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.getRemoteKeysDao().getRemoteKeysId(data.id)
        }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryEntity>): RemoteKeys? =
        state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.getRemoteKeysDao().getRemoteKeysId(data.id)
        }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryEntity>): RemoteKeys? =
        state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.getRemoteKeysDao().getRemoteKeysId(id)
            }
        }

    private suspend fun <T> getResource(call: suspend () -> Response<T>): Resource<T> {
        try {
            val response = call()
            val code = response.code()

            if (response.isSuccessful) {
                val body = response.body()
                return if (body == null) Resource.Failed("Response body returns null")
                else {
                    if (body is BaseStoryResponse) {
                        if (body.error == false)
                            Resource.Success(body)
                        else
                            Resource.Failed(body.message, body)
                    } else Resource.Success(body)
                }
            } else {
                return when (code) {
                    401 -> {
                        val jsonError = response.errorBody()?.string()
                        val body: BaseStoryResponse =
                            Gson().fromJson(jsonError, BaseStoryResponse::class.java)

                        Resource.Unauthorized(body.message)
                    }

                    400, 500 -> {
                        val jsonError = response.errorBody()?.string()
                        val body: BaseStoryResponse =
                            Gson().fromJson(jsonError, BaseStoryResponse::class.java)

                        if (code == 500)
                            Resource.Failed("Request failed")
                        else Resource.Failed(body.message)
                    }

                    503 -> Resource.Failed("System Error")

                    else ->  Resource.Failed("System Error")
                }
            }

        } catch (e: Exception) {
            return if (e is UnknownHostException || e is ConnectException || e is SocketTimeoutException)
                Resource.Failed("Connection failed")
            else Resource.Failed(e.message ?: "Request failed")
        }
    }
}