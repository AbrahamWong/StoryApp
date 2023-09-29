package com.minotawr.storyapp.data.remote.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

abstract class NetworkBoundResource<ResultType, RequestType> {

    protected abstract fun getCached(): Flow<ResultType>
    protected abstract fun shouldUseRemoteData(cachedData: ResultType): Boolean
    protected abstract suspend fun createCall(): Resource<RequestType>
    protected abstract suspend fun saveCallResult(data: RequestType)

    private val result: Flow<Resource<ResultType>> = flow {
        emit(Resource.Loading())
        val cachedData = getCached().first()

        if (shouldUseRemoteData(cachedData)) {
            val response = createCall()
            when (response) {
                is Resource.Success -> {
                    if (response.data != null) {
                        saveCallResult(response.data)
                        emitAll(
                            getCached().map { Resource.Success(it) }
                        )
                    } else {
                        emitAll(
                            getCached().map { Resource.Failed(response.message, it) }
                        )
                    }
                }
                // is Resource.Unauthorized -> TODO()

                else -> {
                    emitAll(
                        getCached().map { Resource.Failed(response.message, it) }
                    )
                }
            }
        } else {
            emitAll(
                getCached().map { Resource.Success(it) }
            )
        }
    }

    fun asFlow(): Flow<Resource<ResultType>> = result

}