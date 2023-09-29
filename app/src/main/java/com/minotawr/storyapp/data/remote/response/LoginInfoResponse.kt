package com.minotawr.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName
import com.minotawr.storyapp.data.remote.network.BaseStoryResponse

class LoginInfoResponse(
    error: Boolean?,
    message: String?,

    @SerializedName("loginResult")
    val loginResult: UserInfoResponse? = null
): BaseStoryResponse(error, message)