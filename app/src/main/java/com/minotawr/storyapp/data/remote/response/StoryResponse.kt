package com.minotawr.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class StoryResponse(
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("photoUrl")
    val photoUrl: String? = null,

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("lat")
    val latitude: Double? = null,

    @field:SerializedName("lon")
    val longitude: Double? = null,
)