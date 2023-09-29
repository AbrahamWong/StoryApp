package com.minotawr.storyapp.data.remote

import com.minotawr.storyapp.data.remote.network.ApiService
import com.minotawr.storyapp.data.remote.request.LoginRequest
import com.minotawr.storyapp.data.remote.request.RegisterRequest
import com.minotawr.storyapp.data.remote.response.BaseRemoteDataSource

class AuthRemoteDataSource(
    private val apiService: ApiService,
) : BaseRemoteDataSource() {

    suspend fun register(name: String, email: String, password: String) =
        getResource {
            apiService.register(
                RegisterRequest(
                    name = name,
                    email = email,
                    password = password
                )
            )
        }

    suspend fun login(email: String, password: String) =
        getResource {
            apiService.login(
                LoginRequest(
                    email = email,
                    password = password
                )
            )
        }
}