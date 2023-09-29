package com.minotawr.storyapp.domain.usecase

import com.minotawr.storyapp.data.remote.network.Resource
import com.minotawr.storyapp.domain.model.LoginInfo
import com.minotawr.storyapp.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow

class AuthInteractor(
    private val repository: IAuthRepository
): AuthUseCase {
    override fun login(email: String, password: String): Flow<Resource<LoginInfo?>> =
        repository.login(email, password)

    override fun register(name: String, email: String, password: String): Flow<Resource<Any?>> =
        repository.register(name, email, password)

    override fun logout(): Flow<Boolean> =
        repository.logout()

    override fun checkLogin(): Flow<Boolean> = repository.checkLogin()
}