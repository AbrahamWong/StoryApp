package com.minotawr.storyapp.domain.usecase

import com.minotawr.storyapp.domain.repository.IStoryRepository
import java.io.File

class StoryInteractor(
    private val repository: IStoryRepository
): StoryUseCase {

    override fun getStories(page: Int?, size: Int?, isLocationRequired: Int?) =
        repository.getStories(page, size, isLocationRequired)

    override fun getPagedStories() = repository.getPagedStories()

    override fun getStoryDetail(id: String) = repository.getStoryDetail(id)

    override fun upload(image: File, description: String, latitude: Float?, longitude: Float?) = repository.upload(image, description, latitude, longitude)
}