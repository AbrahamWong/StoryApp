package com.minotawr.storyapp.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.minotawr.storyapp.domain.usecase.AuthUseCase

class RegisterViewModel(
    private val useCase: AuthUseCase
): ViewModel() {
    private val _name = MutableLiveData<String?>().apply { postValue(null) }
    private val _email = MutableLiveData<String?>().apply { postValue(null) }
    private val _password = MutableLiveData<String?>().apply { postValue(null) }

    val name: LiveData<String?> get() = _name
    val email: LiveData<String?> get() = _email
    val password: LiveData<String?> get() = _password

    fun setName(value: String) {
        _name.postValue(value)
    }

    fun setEmail(value: String) {
        _email.postValue(value)
    }

    fun setPassword(value: String) {
        _password.postValue(value)
    }

    fun register(name: String, email: String, password: String) =
        useCase.register(name, email, password).asLiveData()

}