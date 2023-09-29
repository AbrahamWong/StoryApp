package com.minotawr.storyapp.di

import com.minotawr.storyapp.data.remote.network.ApiService
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val appModule = module {
    single { provideApiService(get(named("api"))) }
}

private fun provideApiService(retrofit: Retrofit): ApiService =
    retrofit.create(ApiService::class.java)