package com.minotawr.storyapp.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.minotawr.storyapp.R
import com.minotawr.storyapp.data.remote.network.Resource
import com.minotawr.storyapp.databinding.ActivityMapsStoryBinding
import com.minotawr.storyapp.domain.model.Story
import com.minotawr.storyapp.ui.base.BaseToolbarActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MapsStoryActivity : BaseToolbarActivity<ActivityMapsStoryBinding>(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    override val viewModel: MapsStoryViewModel by viewModel()

    private val boundsBuilder = LatLngBounds.Builder()

    override val toolbarInflater: (LayoutInflater) -> ActivityMapsStoryBinding
        get() = ActivityMapsStoryBinding::inflate

    private val requestLocationLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted)
                getCurrentLocation()
        }

    override fun setupPopup() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.apply {
            isZoomGesturesEnabled = true
            isZoomControlsEnabled = true
        }

        getCurrentLocation()

        setMapStyle()

        loadData()
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        }
        else requestLocationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun setMapStyle() {
        try {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
        } catch (exception: Resources.NotFoundException) {
            exception.printStackTrace()
        }
    }

    private fun loadData() {
        getStories()
    }

    private fun getStories() {
        viewModel.getStories().observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {  }

                is Resource.Failed -> {
                    if (resource.message != null)
                        showError(resource.message)
                }

                is Resource.Unauthorized -> {
                    showError("Unauthorized access, logging out")
                    logout()
                }

                is Resource.Success -> {
                    val data = resource.data
                    if (data != null) {
                        setStoriesMarker(data)
                    }
                }
            }
        }
    }

    private fun setStoriesMarker(storyList: List<Story>) {
        storyList.forEach { story ->
            if (story.latitude != null && story.longitude != null) {
                val latLng = LatLng(story.latitude, story.longitude)

                mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(story.name)
                        .snippet(story.description)
                )
                boundsBuilder.include(latLng)
            }

            val bounds = boundsBuilder.build()
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    160
                )
            )
        }

    }
}