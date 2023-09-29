package com.minotawr.storyapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.minotawr.storyapp.data.local.entity.StoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StoryDetailLocalDataSource(dataStore: DataStore<Preferences>) :
    BaseLocalDataSource<StoryEntity>(dataStore) {

    private var id: String = ""
        set(value) {
            field = value
            preferenceKey = stringPreferencesKey("story_$value")
        }

    override var preferenceKey = stringPreferencesKey("story_$id")

    override fun key(): String = "story_$id"
    override fun value(json: String): StoryEntity =
        Gson().fromJson(json, object: TypeToken<StoryEntity>() {}.type)

    suspend fun save(id: String, data: StoryEntity?) {
        this.id = id
        return super.save(data)
    }

    fun get(id: String): Flow<StoryEntity?> {
        this.id = id
        return get()
    }

    override fun get(): Flow<StoryEntity?> = dataStore.data.map { preference ->
        val json = preference[preferenceKey]
        if (json == null)
            null
        else value(preference[preferenceKey]!!)
    }

    fun getCached(id: String): StoryEntity? {
        this.id = id
        return super.getCached()
    }

    suspend fun clear(id: String): Preferences {
        this.id = id
        return super.clear()
    }
}