package com.minotawr.storyapp.di

import com.minotawr.storyapp.ui.add.AddStoryViewModel
import com.minotawr.storyapp.ui.detail.StoryDetailViewModel
import com.minotawr.storyapp.ui.home.HomeViewModel
import com.minotawr.storyapp.ui.login.LoginViewModel
import com.minotawr.storyapp.ui.map.MapsStoryViewModel
import com.minotawr.storyapp.ui.register.RegisterViewModel
import com.minotawr.storyapp.ui.splash.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SplashViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { MapsStoryViewModel(get(), get()) }
    viewModel { StoryDetailViewModel(get(), get()) }
    viewModel { AddStoryViewModel(get(), get()) }
}