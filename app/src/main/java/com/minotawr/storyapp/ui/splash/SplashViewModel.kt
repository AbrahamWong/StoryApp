package com.minotawr.storyapp.ui.splash

import androidx.lifecycle.ViewModel
import com.minotawr.storyapp.domain.usecase.AuthUseCase

class SplashViewModel(
    private val authUseCase: AuthUseCase
): ViewModel() {

    fun checkLogin() = authUseCase.checkLogin()

}