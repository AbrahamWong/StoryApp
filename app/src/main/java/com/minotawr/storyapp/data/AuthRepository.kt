package com.minotawr.storyapp.data

import com.minotawr.storyapp.data.local.AuthLocalDataSource
import com.minotawr.storyapp.data.remote.AuthRemoteDataSource
import com.minotawr.storyapp.data.remote.network.BaseStoryResponse
import com.minotawr.storyapp.data.remote.network.NetworkBoundProcessResource
import com.minotawr.storyapp.data.remote.network.NetworkBoundResource
import com.minotawr.storyapp.data.remote.network.Resource
import com.minotawr.storyapp.data.remote.response.LoginInfoResponse
import com.minotawr.storyapp.domain.model.LoginInfo
import com.minotawr.storyapp.domain.repository.IAuthRepository
import com.minotawr.storyapp.utils.AuthMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class AuthRepository(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val authLocalDataSource: AuthLocalDataSource,
) : IAuthRepository {

    override fun login(email: String, password: String): Flow<Resource<LoginInfo?>> =
        object: NetworkBoundResource<LoginInfo?, LoginInfoResponse>() {
            override fun getCached(): Flow<LoginInfo?> =
                authLocalDataSource.get().map { AuthMapper.loginEntityToModel(it) }

            override fun shouldUseRemoteData(cachedData: LoginInfo?): Boolean = true

            override suspend fun createCall(): Resource<LoginInfoResponse> =
                authRemoteDataSource.login(email, password)

            override suspend fun saveCallResult(data: LoginInfoResponse) {
                val entity = AuthMapper.loginResponseToEntity(data)
                authLocalDataSource.save(entity)
            }

        }.asFlow()

    override fun register(name: String, email: String, password: String): Flow<Resource<Any?>> =
        object: NetworkBoundProcessResource<Any?, BaseStoryResponse>() {
            override suspend fun createCall(): Resource<BaseStoryResponse> =
                authRemoteDataSource.register(name, email, password)

            override suspend fun processCallResult(data: BaseStoryResponse): Any = data
        }.asFlow()

    override fun logout() = flow {
        authLocalDataSource.clear()
        emit(true)
    }

    override fun checkLogin(): Flow<Boolean> = flow {
        emitAll(
            authLocalDataSource.get().map { it != null }
        )
    }
}