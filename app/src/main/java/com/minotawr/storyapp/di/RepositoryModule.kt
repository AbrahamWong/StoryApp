package com.minotawr.storyapp.di

import com.minotawr.storyapp.data.AuthRepository
import com.minotawr.storyapp.data.StoryRemoteMediator
import com.minotawr.storyapp.data.StoryRepository
import com.minotawr.storyapp.data.local.AuthLocalDataSource
import com.minotawr.storyapp.data.remote.AuthRemoteDataSource
import com.minotawr.storyapp.data.remote.StoryPagingSource
import com.minotawr.storyapp.data.remote.StoryRemoteDataSource
import com.minotawr.storyapp.domain.repository.IAuthRepository
import com.minotawr.storyapp.domain.repository.IStoryRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { AuthLocalDataSource(get()) }

    single { AuthRemoteDataSource(get()) }
    single { StoryRemoteDataSource(get()) }

    single { StoryPagingSource(get()) }
    single { StoryRemoteMediator(get(), get()) }

    single<IAuthRepository> {
        AuthRepository(get(), get())
    }

    single<IStoryRepository> {
        StoryRepository(get(), get(), get())
    }
}