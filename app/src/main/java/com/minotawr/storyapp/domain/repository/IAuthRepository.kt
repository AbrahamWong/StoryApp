package com.minotawr.storyapp.domain.repository

import com.minotawr.storyapp.data.remote.network.Resource
import com.minotawr.storyapp.domain.model.LoginInfo
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
    fun login(email: String, password: String): Flow<Resource<LoginInfo?>>

    fun register(name: String, email: String, password: String): Flow<Resource<Any?>>

    fun logout(): Flow<Boolean>

    fun checkLogin(): Flow<Boolean>
}