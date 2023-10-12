package com.minotawr.storyapp

import android.app.Application
import com.minotawr.storyapp.di.appModule
import com.minotawr.storyapp.di.dataStoreModule
import com.minotawr.storyapp.di.databaseModule
import com.minotawr.storyapp.di.networkingModule
import com.minotawr.storyapp.di.repositoryModule
import com.minotawr.storyapp.di.useCaseModule
import com.minotawr.storyapp.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class StoryApp: Application() {

    companion object {
        const val baseUrl = "https://story-api.dicoding.dev/v1/"
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@StoryApp)
            modules(
                listOf(
                    appModule,
                    databaseModule,
                    dataStoreModule,
                    networkingModule,
                    repositoryModule,
                    useCaseModule,
                    viewModelModule
                )
            )
        }
    }
}