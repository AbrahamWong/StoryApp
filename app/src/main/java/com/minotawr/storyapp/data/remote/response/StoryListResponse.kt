package com.minotawr.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName
import com.minotawr.storyapp.data.remote.network.BaseStoryResponse

class StoryListResponse(
    error: Boolean? = null,
    message: String? = null,

    @field:SerializedName("listStory")
    val storyList: List<StoryResponse>? = null,
) : BaseStoryResponse(error, message)

