package com.minotawr.storyapp.ui.add

import android.net.Uri
import android.view.LayoutInflater
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.minotawr.storyapp.data.remote.network.Resource
import com.minotawr.storyapp.databinding.ActivityAddStoryBinding
import com.minotawr.storyapp.ui.base.BaseToolbarActivity
import com.minotawr.storyapp.utils.ImageUtils
import com.minotawr.storyapp.utils.ImageUtils.reduceFileImage
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class AddStoryActivity : BaseToolbarActivity<ActivityAddStoryBinding>() {
    override val toolbarInflater: (LayoutInflater) -> ActivityAddStoryBinding
        get() = ActivityAddStoryBinding::inflate

    override fun backButtonShown(): Boolean = true

    override val viewModel: AddStoryViewModel by viewModel()

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && viewModel.imageUri.value != null)
            showImageFromUri(viewModel.imageUri.value!!)
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            viewModel.setImageUri(uri)
            showImageFromUri(uri)
        }
    }

    override fun setupPopup() {
        setupObserver()
        setupListener()
    }

    private fun setupObserver() {
        viewModel.imageUri.observe(this) {
            setSubmit()
        }

        viewModel.description.observe(this) {
            setSubmit()
        }
    }

    private fun setupListener() {
        toolbarBinding.btnCamera.setOnClickListener {
            val imageUri = ImageUtils.getImageUri(this)
            viewModel.setImageUri(imageUri)
            cameraLauncher.launch(imageUri)
        }

        toolbarBinding.btnGallery.setOnClickListener {
            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        toolbarBinding.edAddDescription.addTextChangedListener { editable ->
            viewModel.setDescription(editable.toString())
        }

        toolbarBinding.buttonAdd.setOnClickListener {
            uploadStory()
        }
    }

    private fun setSubmit() {
        toolbarBinding.buttonAdd.isEnabled =
            viewModel.imageUri.value != null &&
                    viewModel.description.value != null &&
                    viewModel.description.value!!.length > 6
    }

    private fun showImageFromUri(uri: Uri) {
        toolbarBinding.tvPhotoLabel.isVisible = false

        Glide.with(this)
            .load(uri)
            .into(toolbarBinding.ivItemPhoto)
    }

    private fun uploadStory() {
        val imageUri = viewModel.imageUri.value
        imageUri?.let { uri ->
            val file = ImageUtils.uriToFile(uri, this).reduceFileImage()
            val description = viewModel.description.value
            if (description == null || description.length < 6) {
                showError("Description should be at least 6 characters long")
                return@let
            }

            showLoading()
            upload(file, description)
        } ?: showError("Image is empty or unavailable, please use another image")
    }

    private fun upload(file: File, description: String) {
        viewModel.upload(file, description).observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {  }

                is Resource.Unauthorized -> {
                    hideLoading()

                    if (resource.message != null) {
                        showError(resource.message)
                        logout()
                    }
                }

                is Resource.Success -> {
                    hideLoading()

                    setResult(RESULT_OK)
                    finish()
                }

                is Resource.Failed -> {
                    hideLoading()

                    if (resource.message != null)
                        showError(resource.message)
                }
            }
        }
    }

}