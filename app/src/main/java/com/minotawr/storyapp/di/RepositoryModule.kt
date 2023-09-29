package com.minotawr.storyapp.di

import com.minotawr.storyapp.data.AuthRepository
import com.minotawr.storyapp.data.StoryRepository
import com.minotawr.storyapp.data.local.AuthLocalDataSource
import com.minotawr.storyapp.data.local.StoryDetailLocalDataSource
import com.minotawr.storyapp.data.local.StoryLocalDataSource
import com.minotawr.storyapp.data.remote.AuthRemoteDataSource
import com.minotawr.storyapp.data.remote.StoryRemoteDataSource
import com.minotawr.storyapp.domain.repository.IAuthRepository
import com.minotawr.storyapp.domain.repository.IStoryRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { AuthLocalDataSource(get()) }
    single { StoryLocalDataSource(get()) }
    single { StoryDetailLocalDataSource(get()) }

    single { AuthRemoteDataSource(get()) }
    single { StoryRemoteDataSource(get()) }

    single<IAuthRepository> {
        AuthRepository(get(), get())
    }

    single<IStoryRepository> {
        StoryRepository(get(), get(), get())
    }
}