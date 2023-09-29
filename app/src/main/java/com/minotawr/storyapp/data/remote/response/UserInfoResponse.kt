package com.minotawr.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class UserInfoResponse(
    @field:SerializedName("userId")
    val userId: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("token")
    val token: String? = null,
)
