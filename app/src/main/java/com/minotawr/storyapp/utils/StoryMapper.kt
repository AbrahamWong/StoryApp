package com.minotawr.storyapp.utils

import com.minotawr.storyapp.data.local.entity.StoryEntity
import com.minotawr.storyapp.data.remote.response.StoryDetailResponse
import com.minotawr.storyapp.data.remote.response.StoryListResponse
import com.minotawr.storyapp.domain.model.Story

object StoryMapper {

    fun storyListEntityToModel(entities: List<StoryEntity>?) =
        entities?.map {
            Story(
                id = it.id,
                name = it.name,
                description = it.description,
                photoUrl = it.photoUrl,
                createdAt = it.createdAt,
                latitude = it.latitude,
                longitude = it.longitude,
            )
        }

    fun storyListResponseToEntity(response: StoryListResponse?) =
        response?.storyList?.map {
            StoryEntity(
                id = it.id,
                name = it.name,
                description = it.description,
                photoUrl = it.photoUrl,
                createdAt = it.createdAt,
                latitude = it.latitude,
                longitude = it.longitude,
            )
        }

    fun storyEntityToModel(entity: StoryEntity?) =
        entity?.let {
            Story(
                id = it.id,
                name = it.name,
                description = it.description,
                photoUrl = it.photoUrl,
                createdAt = it.createdAt,
                latitude = it.latitude,
                longitude = it.longitude,
            )
        }

    fun storyResponseToEntity(response: StoryDetailResponse?) =
        response?.story?.let {
            StoryEntity(
                id = it.id,
                name = it.name,
                description = it.description,
                photoUrl = it.photoUrl,
                createdAt = it.createdAt,
                latitude = it.latitude,
                longitude = it.longitude,
            )
        }

}