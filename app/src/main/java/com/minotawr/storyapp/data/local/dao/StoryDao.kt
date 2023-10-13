package com.minotawr.storyapp.data.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.minotawr.storyapp.data.local.entity.StoryEntity

@Dao
interface StoryDao {

    @Query("SELECT * FROM story")
    fun getAllPagingStory(): PagingSource<Int, StoryEntity>

    @Query("SELECT * FROM story")
    fun getAllStory(): LiveData<List<StoryEntity>>

    @Query("SELECT * FROM story WHERE id = :id")
    fun getStoryDetail(id: String): LiveData<StoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStoryList(data: List<StoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(data: StoryEntity)

    @Query("DELETE FROM story")
    suspend fun deleteStoryList()
}