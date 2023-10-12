package com.minotawr.storyapp.data.remote.network

import com.minotawr.storyapp.data.remote.request.LoginRequest
import com.minotawr.storyapp.data.remote.request.RegisterRequest
import com.minotawr.storyapp.data.remote.response.LoginInfoResponse
import com.minotawr.storyapp.data.remote.response.StoryDetailResponse
import com.minotawr.storyapp.data.remote.response.StoryListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<BaseStoryResponse>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginInfoResponse>

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int?,
        @Query("size") size: Int?,
        @Query("location") isLocationRequired: Int?,
    ): Response<StoryListResponse>

    @GET("stories/{id}")
    suspend fun getStoryDetail(@Path("id") id: String): Response<StoryDetailResponse>

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Response<BaseStoryResponse>
}