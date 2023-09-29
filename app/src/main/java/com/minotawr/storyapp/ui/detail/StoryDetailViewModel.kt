package com.minotawr.storyapp.ui.detail

import androidx.lifecycle.asLiveData
import com.minotawr.storyapp.domain.usecase.AuthUseCase
import com.minotawr.storyapp.domain.usecase.StoryUseCase
import com.minotawr.storyapp.ui.base.BaseToolbarViewModel

class StoryDetailViewModel(
    authUseCase: AuthUseCase,
    private val storyUseCase: StoryUseCase
): BaseToolbarViewModel(authUseCase) {

    fun getStoryDetail(id: String) = storyUseCase.getStoryDetail(id).asLiveData()

}