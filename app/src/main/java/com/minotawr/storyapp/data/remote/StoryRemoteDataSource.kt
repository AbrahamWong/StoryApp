package com.minotawr.storyapp.data.remote

import com.minotawr.storyapp.data.remote.network.ApiService
import com.minotawr.storyapp.data.remote.response.BaseRemoteDataSource
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRemoteDataSource(private val apiService: ApiService) : BaseRemoteDataSource() {

    suspend fun getStories() = getResource { apiService.getStories() }

    suspend fun getStoryDetail(id: String) = getResource { apiService.getStoryDetail(id) }

    suspend fun upload(file: MultipartBody.Part, description: RequestBody) = getResource { apiService.uploadStory(file, description) }
}