package com.minotawr.storyapp.ui.home

import androidx.lifecycle.asLiveData
import com.minotawr.storyapp.domain.usecase.AuthUseCase
import com.minotawr.storyapp.domain.usecase.StoryUseCase
import com.minotawr.storyapp.ui.base.BaseToolbarViewModel

class HomeViewModel(
    authUseCase: AuthUseCase,
    private val storyUseCase: StoryUseCase
): BaseToolbarViewModel(authUseCase) {

    fun getStories() = storyUseCase.getStories().asLiveData()
}