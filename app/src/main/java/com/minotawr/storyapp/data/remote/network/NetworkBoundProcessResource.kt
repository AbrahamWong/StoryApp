package com.minotawr.storyapp.data.remote.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

abstract class NetworkBoundProcessResource<ResultType, RequestType> {

    protected abstract suspend fun createCall(): Resource<RequestType>
    protected abstract suspend fun processCallResult(data: RequestType): ResultType

    private val result: Flow<Resource<ResultType>> = flow {
        emit(Resource.Loading())
        val response = createCall()
        when (response) {
            is Resource.Success -> {
                if (response.data != null) {
                    val result = processCallResult(response.data)
                    emit(Resource.Success(result))
                } else {
                    emit(Resource.Failed(response.message))
                }
            }
            // is Resource.Unauthorized -> TODO()

            else -> {
                emit(Resource.Failed(response.message))
            }
        }
    }

    fun asFlow(): Flow<Resource<ResultType>> = result

}