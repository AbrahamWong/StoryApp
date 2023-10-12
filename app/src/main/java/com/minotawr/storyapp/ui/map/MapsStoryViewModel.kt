package com.minotawr.storyapp.ui.map

import androidx.lifecycle.asLiveData
import com.minotawr.storyapp.domain.usecase.AuthUseCase
import com.minotawr.storyapp.domain.usecase.StoryUseCase
import com.minotawr.storyapp.ui.base.BaseToolbarViewModel

class MapsStoryViewModel(
    authUseCase: AuthUseCase,
    private val storyUseCase: StoryUseCase
) : BaseToolbarViewModel(authUseCase) {

    fun getStories() = storyUseCase.getStories(isLocationRequired = 1).asLiveData()

}