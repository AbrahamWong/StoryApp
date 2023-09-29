package com.minotawr.storyapp.di

import com.minotawr.storyapp.domain.usecase.AuthInteractor
import com.minotawr.storyapp.domain.usecase.AuthUseCase
import com.minotawr.storyapp.domain.usecase.StoryInteractor
import com.minotawr.storyapp.domain.usecase.StoryUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory<AuthUseCase> {
        AuthInteractor(get())
    }

    factory<StoryUseCase> {
        StoryInteractor(get())
    }
}