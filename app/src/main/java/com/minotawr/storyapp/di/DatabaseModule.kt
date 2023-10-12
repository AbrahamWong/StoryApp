package com.minotawr.storyapp.di

import android.content.Context
import androidx.room.Room
import com.minotawr.storyapp.BuildConfig
import com.minotawr.storyapp.data.local.database.StoryDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single { provideDatabase(androidContext()) }

    single {
        val database = get<StoryDatabase>()
        database.getStoryDao()
    }
}

fun provideDatabase(context: Context): StoryDatabase =
    Room.databaseBuilder(
        context.applicationContext,
        StoryDatabase::class.java,
        BuildConfig.DATABASE_NAME
    )
        .fallbackToDestructiveMigration()
        .build()