package com.minotawr.storyapp.data

import com.minotawr.storyapp.data.local.StoryDetailLocalDataSource
import com.minotawr.storyapp.data.local.StoryLocalDataSource
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
    private val storyLocalDataSource: StoryLocalDataSource,
    private val storyDetailLocalDataSource: StoryDetailLocalDataSource,
) : IStoryRepository {

    override fun getStories(): Flow<Resource<List<Story>?>> =
        object: NetworkBoundResource<List<Story>?, StoryListResponse>() {
            override fun getCached(): Flow<List<Story>?> =
                storyLocalDataSource.get().map { StoryMapper.storyListEntityToModel(it) }

            override fun shouldUseRemoteData(cachedData: List<Story>?): Boolean =
                cachedData == null

            override suspend fun createCall(): Resource<StoryListResponse> =
                storyRemoteDataSource.getStories()

            override suspend fun saveCallResult(data: StoryListResponse) {
                val result = StoryMapper.storyListResponseToEntity(data)
                storyLocalDataSource.save(result)
            }
        }.asFlow()

    override fun getStoryDetail(id: String): Flow<Resource<Story?>> =
        object : NetworkBoundResource<Story?, StoryDetailResponse>() {
            override fun getCached(): Flow<Story?> =
                storyDetailLocalDataSource.get(id).map { StoryMapper.storyEntityToModel(it) }

            override fun shouldUseRemoteData(cachedData: Story?): Boolean = cachedData == null

            override suspend fun createCall(): Resource<StoryDetailResponse> =
                storyRemoteDataSource.getStoryDetail(id)

            override suspend fun saveCallResult(data: StoryDetailResponse) {
                val result = StoryMapper.storyResponseToEntity(data)
                storyDetailLocalDataSource.save(id, result)
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