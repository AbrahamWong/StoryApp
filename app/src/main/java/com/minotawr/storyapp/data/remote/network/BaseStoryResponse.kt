package com.minotawr.storyapp.data.remote.network

import com.google.gson.annotations.SerializedName

open class BaseStoryResponse(
    @field:SerializedName("error")
    val error: Boolean? = null,
    @field:SerializedName("message")
    val message: String? = null,
)
