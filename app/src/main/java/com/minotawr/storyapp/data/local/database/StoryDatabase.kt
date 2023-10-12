package com.minotawr.storyapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.minotawr.storyapp.data.local.dao.StoryDao
import com.minotawr.storyapp.data.local.entity.StoryEntity

@Database(
    entities = [StoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class StoryDatabase: RoomDatabase() {

    abstract fun getStoryDao(): StoryDao

}