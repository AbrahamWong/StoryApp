package com.minotawr.storyapp.data

import android.util.Log
import androidx.lifecycle.asFlow
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.minotawr.storyapp.data.local.dao.StoryDao
import com.minotawr.storyapp.data.remote.StoryRemoteDataSource
import com.minotawr.storyapp.data.remote.network.BaseStoryResponse
import com.minotawr.storyapp.data.remote.network.NetworkBoundProcessResource
import com.minotawr.storyapp.data.remote.network.NetworkBoundResource
import com.minotawr.storyapp.data.remote.network.Resource
import com.minotawr.storyapp.data.remote.response.StoryDetailResponse
import com.minotawr.storyapp.data.remote.response.StoryListResponse
import com.minotawr.storyapp.domain.model.Story
import com.minotawr.storyapp.domain.repository.IStoryRepository
import com.minotawr.storyapp.utils.StoryMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryRepository(
    private val storyRemoteDataSource: StoryRemoteDataSource,
    private val storyRemoteMediator: StoryRemoteMediator,

    private val storyDao: StoryDao
) : IStoryRepository {

    override fun getStories(page: Int?, size: Int?, isLocationRequired: Int?): Flow<Resource<List<Story>?>> =
        object: NetworkBoundResource<List<Story>?, StoryListResponse>() {
            override fun getCached(): Flow<List<Story>?> =
                storyDao.getAllStory().asFlow().map { StoryMapper.storyListEntityToModel(it) }

            override fun shouldUseRemoteData(cachedData: List<Story>?): Boolean =
                true

            override suspend fun createCall(): Resource<StoryListResponse> =
                storyRemoteDataSource.getStories(page, size, isLocationRequired)

            override suspend fun saveCallResult(data: StoryListResponse) {
                val result = StoryMapper.storyListResponseToEntity(data)

                if (result != null) {
                    storyDao.deleteStoryList()
                    storyDao.insertStoryList(result)
                }
            }
        }.asFlow()

    @OptIn(ExperimentalPagingApi::class)
    override fun getPagedStories(): Flow<PagingData<Story>> =
        Pager(
            config = PagingConfig(pageSize = 5, initialLoadSize = 10),
            remoteMediator = storyRemoteMediator,
            pagingSourceFactory = {
                storyDao.getAllPagingStory()
            }
        ).flow.map { pagingData ->
            pagingData.map{ StoryMapper.storyEntityToModel(it) as Story }
        }

    override fun getStoryDetail(id: String): Flow<Resource<Story?>> =
        object : NetworkBoundResource<Story?, StoryDetailResponse>() {
            override fun getCached(): Flow<Story?> =
                storyDao.getStoryDetail(id).asFlow().map { StoryMapper.storyEntityToModel(it) }

            override fun shouldUseRemoteData(cachedData: Story?): Boolean = cachedData == null

            override suspend fun createCall(): Resource<StoryDetailResponse> =
                storyRemoteDataSource.getStoryDetail(id)

            override suspend fun saveCallResult(data: StoryDetailResponse) {
                val result = StoryMapper.storyResponseToEntity(data)

                if (result != null) {
                    storyDao.insertStory(result)
                    Log.d(StoryRepository::class.java.simpleName, "saveCallResult: success")
                }
            }

        }.asFlow()

    override fun upload(image: File, description: String): Flow<Resource<Any?>> =
        object: NetworkBoundProcessResource<Any?, BaseStoryResponse>() {
            override suspend fun createCall(): Resource<BaseStoryResponse> {
                val descriptionRequestBody = description.toRequestBody("text/plain".toMediaType())

                val fileRequestBody = image.asRequestBody("image/jpeg".toMediaType())
                val fileMultipartBody = MultipartBody.Part.createFormData(
                    "photo",
                    image.name,
                    fileRequestBody
                )

                return storyRemoteDataSource.upload(fileMultipartBody, descriptionRequestBody)
            }

            override suspend fun processCallResult(data: BaseStoryResponse): Any = data
        }.asFlow()
}