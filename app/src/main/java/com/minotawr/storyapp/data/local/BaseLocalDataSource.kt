package com.minotawr.storyapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "story")

abstract class BaseLocalDataSource<T>(protected val dataStore: DataStore<Preferences>) {
    abstract fun key(): String
    abstract fun value(json: String): T?

    protected open val preferenceKey = stringPreferencesKey(key())

    open suspend fun save(data: T?) {
        dataStore.edit { preferences ->
            preferences[preferenceKey] = Gson().toJson(data)
        }
    }

    open fun get(): Flow<T?> = flow {
        emit(getCached())
    }

    open fun getCached(): T? = runBlocking {
        dataStore.data.first().get(preferenceKey)?.let { value(it) }
    }

    open suspend fun clear() = dataStore.edit { preference ->
        preference.clear()
    }
}