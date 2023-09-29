package com.minotawr.storyapp.domain.usecase

import com.minotawr.storyapp.domain.repository.IStoryRepository
import java.io.File

class StoryInteractor(
    private val repository: IStoryRepository
): StoryUseCase {

    override fun getStories() = repository.getStories()

    override fun getStoryDetail(id: String) = repository.getStoryDetail(id)

    override fun upload(image: File, description: String) = repository.upload(image, description)
}