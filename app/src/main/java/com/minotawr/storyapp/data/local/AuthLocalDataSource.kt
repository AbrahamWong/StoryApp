package com.minotawr.storyapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.gson.Gson
import com.minotawr.storyapp.data.local.entity.LoginInfoEntity

class AuthLocalDataSource(dataStore: DataStore<Preferences>)
    : BaseLocalDataSource<LoginInfoEntity>(dataStore) {
    override fun key(): String = "auth"
    override fun value(json: String): LoginInfoEntity? =
        Gson().fromJson(json, LoginInfoEntity::class.java)
}