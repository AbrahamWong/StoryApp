package com.minotawr.storyapp.ui.add

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.minotawr.storyapp.domain.usecase.AuthUseCase
import com.minotawr.storyapp.domain.usecase.StoryUseCase
import com.minotawr.storyapp.ui.base.BaseToolbarViewModel
import java.io.File

class AddStoryViewModel(
    authUseCase: AuthUseCase,
    private val storyUseCase: StoryUseCase
): BaseToolbarViewModel(authUseCase) {
    private val _imageUri = MutableLiveData<Uri?>().apply { postValue(null) }
    private val _description = MutableLiveData<String?>().apply { postValue(null) }

    val imageUri: LiveData<Uri?> get() = _imageUri
    val description: LiveData<String?> get() = _description

    fun setImageUri(uri: Uri) {
        _imageUri.postValue(uri)
    }

    fun setDescription(value: String) {
        _description.postValue(value)
    }

    fun upload(file: File, description: String) =
        storyUseCase.upload(file, description).asLiveData()
}