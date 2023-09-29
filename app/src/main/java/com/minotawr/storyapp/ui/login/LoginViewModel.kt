package com.minotawr.storyapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.minotawr.storyapp.data.remote.network.Resource
import com.minotawr.storyapp.domain.model.LoginInfo
import com.minotawr.storyapp.domain.usecase.AuthUseCase

class LoginViewModel(private val authUseCase: AuthUseCase) : ViewModel() {
    private val _email = MutableLiveData<String?>().apply { postValue(null) }
    private val _password = MutableLiveData<String?>().apply { postValue(null) }

    val email: LiveData<String?>
        get() = _email

    val password: LiveData<String?>
        get() = _password

    fun setEmail(email: String?) {
        _email.postValue(email)
    }

    fun setPassword(password: String?) {
        _password.postValue(password)
    }

    fun login(email: String, password: String): LiveData<Resource<LoginInfo?>> =
        authUseCase.login(email, password).asLiveData()

}