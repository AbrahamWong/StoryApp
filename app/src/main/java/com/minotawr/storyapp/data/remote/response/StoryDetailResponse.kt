package com.minotawr.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName
import com.minotawr.storyapp.data.remote.network.BaseStoryResponse

class StoryDetailResponse(
    error: Boolean? = null,
    message: String? = null,

    @field:SerializedName("story")
    val story: StoryResponse? = null,
) : BaseStoryResponse(error, message)

