package com.minotawr.storyapp.domain.usecase

import com.minotawr.storyapp.data.remote.network.Resource
import com.minotawr.storyapp.domain.model.Story
import com.minotawr.storyapp.domain.repository.IStoryRepository
import kotlinx.coroutines.flow.Flow
import java.io.File

class StoryInteractor(
    private val repository: IStoryRepository
): StoryUseCase {

    override fun getStories(page: Int?, size: Int?, isLocationRequired: Int?) =
        repository.getStories(page, size, isLocationRequired)

    override fun getStoryDetail(id: String) = repository.getStoryDetail(id)

    override fun upload(image: File, description: String) = repository.upload(image, description)
}