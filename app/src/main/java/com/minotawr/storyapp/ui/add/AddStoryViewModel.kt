package com.minotawr.storyapp.ui.add

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.google.android.gms.maps.model.LatLng
import com.minotawr.storyapp.domain.usecase.AuthUseCase
import com.minotawr.storyapp.domain.usecase.StoryUseCase
import com.minotawr.storyapp.ui.base.BaseToolbarViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File

class AddStoryViewModel(
    authUseCase: AuthUseCase,
    private val storyUseCase: StoryUseCase
): BaseToolbarViewModel(authUseCase) {
    private val _imageUri = MutableLiveData<Uri?>().apply { postValue(null) }
    private val _description = MutableLiveData<String?>().apply { postValue(null) }
    private val _latLng = MutableLiveData<LatLng?>().apply { postValue(null) }

    val isLocationRequired = MutableStateFlow(false)

    val imageUri: LiveData<Uri?> get() = _imageUri
    val description: LiveData<String?> get() = _description
    val latLng: LiveData<LatLng?> get() = _latLng

    fun setImageUri(uri: Uri) {
        _imageUri.postValue(uri)
    }

    fun setDescription(value: String) {
        _description.postValue(value)
    }

    fun setLatLng(latLng: LatLng) {
        _latLng.postValue(latLng)
    }

    fun upload(file: File, description: String, latitude: Float?, longitude: Float?) =
        storyUseCase.upload(file, description, latitude, longitude).asLiveData()
}