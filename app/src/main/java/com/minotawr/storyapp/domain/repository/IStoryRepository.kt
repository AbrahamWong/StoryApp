package com.minotawr.storyapp.domain.repository

import com.minotawr.storyapp.data.remote.network.Resource
import com.minotawr.storyapp.domain.model.Story
import kotlinx.coroutines.flow.Flow
import java.io.File

interface IStoryRepository {

    fun getStories(): Flow<Resource<List<Story>?>>

    fun getStoryDetail(id: String): Flow<Resource<Story?>>

    fun upload(image: File, description: String): Flow<Resource<Any?>>

}