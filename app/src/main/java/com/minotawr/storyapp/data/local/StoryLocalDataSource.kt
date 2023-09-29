package com.minotawr.storyapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.minotawr.storyapp.data.local.entity.StoryEntity

class StoryLocalDataSource(dataStore: DataStore<Preferences>) :
    BaseLocalDataSource<List<StoryEntity>>(dataStore) {
    override fun key(): String = "stories"
    override fun value(json: String): List<StoryEntity> =
        Gson().fromJson(json, object: TypeToken<List<StoryEntity>>() {}.type)
}