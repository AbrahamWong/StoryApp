package com.minotawr.storyapp.di

import com.minotawr.storyapp.data.local.dataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataStoreModule = module {
    single { androidContext().dataStore }
}
