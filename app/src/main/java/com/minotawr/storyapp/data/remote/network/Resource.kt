package com.minotawr.storyapp.data.remote.network

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Loading<T> : Resource<T>()
    class Unauthorized<T>(message: String?): Resource<T>(message = message)
    class Success<T>(data: T?): Resource<T>(data)
    class Failed<T>(message: String?, data: T? = null): Resource<T>(data, message)
}
