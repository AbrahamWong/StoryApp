package com.minotawr.storyapp.ui.add

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Looper
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.model.LatLng
import com.minotawr.storyapp.data.remote.network.Resource
import com.minotawr.storyapp.databinding.ActivityAddStoryBinding
import com.minotawr.storyapp.ui.base.BaseToolbarActivity
import com.minotawr.storyapp.utils.ImageUtils
import com.minotawr.storyapp.utils.ImageUtils.reduceFileImage
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.util.concurrent.TimeUnit

class AddStoryActivity : BaseToolbarActivity<ActivityAddStoryBinding>() {
    override val toolbarInflater: (LayoutInflater) -> ActivityAddStoryBinding
        get() = ActivityAddStoryBinding::inflate

    override fun backButtonShown(): Boolean = true

    override val viewModel: AddStoryViewModel by viewModel()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && viewModel.imageUri.value != null)
                showImageFromUri(viewModel.imageUri.value!!)
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                viewModel.setImageUri(uri)
                showImageFromUri(uri)
            }
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getLastLocation()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getLastLocation()
                }
            }
        }

    private val resolutionLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_CANCELED) {
                Toast.makeText(
                    this@AddStoryActivity,
                    "Activate GPS to use this feature.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun setupPopup() {
        setupView()
        setupObserver()
        setupListener()
    }

    private fun setupView() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun setupObserver() {
        viewModel.imageUri.observe(this) {
            setSubmit()
        }

        viewModel.description.observe(this) {
            setSubmit()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLocationRequired.collect { isLocationRequired ->
                    if (isLocationRequired) {
                        createLocationRequest()
                        createLocationCallback()

                        startLocationUpdates()
                    } else {
                        stopLocationUpdates()
                    }
                }
            }
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
            AlertDialog.Builder(this)
                .setTitle("Upload story")
                .setMessage("Please confirm that the added image & description is correct.")
                .setPositiveButton("Upload") { dialog, _ ->
                    dialog.dismiss()
                    uploadStory()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }.create().show()
        }

        toolbarBinding.cbUseLocation.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isLocationRequired.value = isChecked
        }
    }

    private fun createLocationRequest() {
        val request = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(1)
            maxWaitTime = TimeUnit.SECONDS.toMillis(1)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationRequest = request
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(request)

        val client = LocationServices.getSettingsClient(this)
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                getLastLocation()
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        resolutionLauncher.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    } catch (sendIntentException: IntentSender.SendIntentException) {
                        Toast.makeText(
                            this@AddStoryActivity,
                            sendIntentException.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                for (location in p0.locations) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    viewModel.setLatLng(latLng)
                }
            }
        }
    }

    private fun getLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val latLng = LatLng(location.latitude, location.longitude)
                        viewModel.setLatLng(latLng)
                    } else {
                        Toast.makeText(
                            this@AddStoryActivity,
                            "Location not found. Try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
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
            val latLng = viewModel.latLng.value

            showLoading()
            upload(file, description, latLng)
        } ?: showError("Image is empty or unavailable, please use another image")
    }

    private fun upload(file: File, description: String, latLng: LatLng?) {
        viewModel.upload(file, description, latLng?.latitude?.toFloat(), latLng?.longitude?.toFloat()).observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {}

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

    private fun checkPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    private fun startLocationUpdates() {
        try {
            if (locationRequest != null && locationCallback != null) {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest!!,
                    locationCallback!!,
                    Looper.getMainLooper()
                )
            }
        } catch (exception: SecurityException) {
            exception.printStackTrace()
        }
    }

    private fun stopLocationUpdates() {
        if (locationCallback != null)
            fusedLocationClient.removeLocationUpdates(locationCallback!!)
    }

    override fun onResume() {
        super.onResume()

        if (viewModel.isLocationRequired.value)
            startLocationUpdates()
    }

    override fun onPause() {
        stopLocationUpdates()
        super.onPause()
    }
}