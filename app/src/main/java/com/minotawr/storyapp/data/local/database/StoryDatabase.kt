package com.minotawr.storyapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.minotawr.storyapp.data.local.dao.RemoteKeysDao
import com.minotawr.storyapp.data.local.dao.StoryDao
import com.minotawr.storyapp.data.local.entity.RemoteKeys
import com.minotawr.storyapp.data.local.entity.StoryEntity

@Database(
    entities = [StoryEntity::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class StoryDatabase: RoomDatabase() {

    abstract fun getStoryDao(): StoryDao
    abstract fun getRemoteKeysDao(): RemoteKeysDao

}