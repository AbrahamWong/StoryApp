package com.minotawr.storyapp.data.remote.response

import com.google.gson.Gson
import com.minotawr.storyapp.data.remote.network.BaseStoryResponse
import com.minotawr.storyapp.data.remote.network.Resource
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

abstract class BaseRemoteDataSource {

    suspend fun <T> getResource(call: suspend () -> Response<T>): Resource<T> {
        try {
            val response = call()
            val code = response.code()

            if (response.isSuccessful) {
                val body = response.body()
                return if (body == null) Resource.Failed("Response body returns null")
                else {
                    if (body is BaseStoryResponse) {
                        if (body.error == false)
                            Resource.Success(body)
                        else
                            Resource.Failed(body.message, body)
                    } else Resource.Success(body)
                }
            } else {
               return when (code) {
                    401 -> {
                        val jsonError = response.errorBody()?.string()
                        val body: BaseStoryResponse =
                            Gson().fromJson(jsonError, BaseStoryResponse::class.java)

                        Resource.Unauthorized(body.message)
                    }

                    400, 500 -> {
                        val jsonError = response.errorBody()?.string()
                        val body: BaseStoryResponse =
                            Gson().fromJson(jsonError, BaseStoryResponse::class.java)

                        if (code == 500)
                            Resource.Failed("Request failed")
                        else Resource.Failed(body.message)
                    }

                    503 -> Resource.Failed("System Error")

                    else ->  Resource.Failed("System Error")
                }
            }

        } catch (e: Exception) {
            return if (e is UnknownHostException || e is ConnectException || e is SocketTimeoutException)
                Resource.Failed("Connection failed")
            else Resource.Failed(e.message ?: "Request failed")
        }
    }

}