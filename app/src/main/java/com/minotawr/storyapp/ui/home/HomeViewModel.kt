package com.minotawr.storyapp.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.minotawr.storyapp.domain.model.Story
import com.minotawr.storyapp.domain.usecase.AuthUseCase
import com.minotawr.storyapp.domain.usecase.StoryUseCase
import com.minotawr.storyapp.ui.base.BaseToolbarViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class HomeViewModel(
    authUseCase: AuthUseCase,
    private val storyUseCase: StoryUseCase
): BaseToolbarViewModel(authUseCase) {

    val hasNotScrolled = MutableStateFlow(true)

    val pagedStories = MutableLiveData<PagingData<Story>>().apply { postValue(null) }

    fun getPagedStories() = storyUseCase.getPagedStories().asLiveData().cachedIn(viewModelScope)
}