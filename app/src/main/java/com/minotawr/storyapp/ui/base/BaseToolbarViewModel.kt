package com.minotawr.storyapp.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.minotawr.storyapp.domain.usecase.AuthUseCase

open class BaseToolbarViewModel(
    private val authUseCase: AuthUseCase
): ViewModel() {
    fun logout() = authUseCase.logout().asLiveData()
}