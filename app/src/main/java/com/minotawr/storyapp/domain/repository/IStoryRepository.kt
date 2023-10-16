package com.minotawr.storyapp.domain.repository

import androidx.paging.PagingData
import com.minotawr.storyapp.data.remote.network.Resource
import com.minotawr.storyapp.domain.model.Story
import kotlinx.coroutines.flow.Flow
import java.io.File

interface IStoryRepository {

    fun getStories(page: Int?, size: Int?, isLocationRequired: Int?): Flow<Resource<List<Story>?>>

    fun getPagedStories(): Flow<PagingData<Story>>

    fun getStoryDetail(id: String): Flow<Resource<Story?>>

    fun upload(image: File, description: String, latitude: Float?, longitude: Float?): Flow<Resource<Any?>>

}