package com.roohandeh.holoomapproject.presentation.main

import android.Manifest
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.carto.styles.MarkerStyleBuilder
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.OnSuccessListener
import com.roohandeh.holoomapproject.R
import com.roohandeh.holoomapproject.databinding.FragmentMapBinding
import com.roohandeh.holoomapproject.presentation.base.BaseBindingFragment
import com.roohandeh.holoomapproject.utils.AlertCreator
import com.roohandeh.holoomapproject.utils.ConnectivityObserver
import com.roohandeh.holoomapproject.utils.GpsStatus
import com.roohandeh.holoomapproject.utils.NetworkConnectivityManager
import com.roohandeh.holoomapproject.utils.REQUEST_CODE
import com.roohandeh.holoomapproject.utils.UPDATE_INTERVAL_IN_MILLISECONDS
import com.roohandeh.holoomapproject.utils.isGpsEnabled
import com.roohandeh.holoomapproject.utils.showToast
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.neshan.common.model.LatLng
import org.neshan.mapsdk.internal.utils.BitmapUtils
import org.neshan.mapsdk.model.Marker
import java.text.DateFormat
import java.util.Date

private const val TAG = "UserLocationUpdater"
class MapFragment : BaseBindingFragment<FragmentMapBinding>() {

    private var isFirstTime: Boolean = true
    private var userLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var settingsClient: SettingsClient
    private lateinit var locationRequest: LocationRequest
    private var locationSettingsRequest: LocationSettingsRequest? = null
    private var locationCallback: LocationCallback? = null
    private var lastUpdateTime: String? = null
    private var marker: Marker? = null
    private lateinit var mainActivity: MainActivity
    private lateinit var connectivityManager: NetworkConnectivityManager

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentMapBinding =
        { layoutInflater, viewGroup, attachedToParent ->
            FragmentMapBinding.inflate(layoutInflater, viewGroup, attachedToParent)
        }

    override fun onStart() {
        super.onStart()
        initLocation()
        startReceivingLocationUpdates()
    }

    override fun initView() {
        mainActivity = activity as MainActivity
        connectivityManager = mainActivity.networkConnectivityManager
        checkInternetAvailability()
        checkGpsAvailability()
        binding.focusButton.setOnClickListener {
            focusOnUserLocation()
        }
    }

    private fun checkGpsAvailability() {
        mainActivity.gpsSwitchStateReceiver.gpsStatus.observe(this) {
            if (it == GpsStatus.Available) {
                if (connectivityManager.isNetworkAvailable()) {
                    mainActivity.hideTopViewMessage()
                    startLocationUpdates()
                    initMapForFirstTime()
                } else {
                    mainActivity.showTopViewMessage(resources.getString(R.string.turn_on_internet_message))
                }
            } else if (it == GpsStatus.UnAvailable) {
                mainActivity.showTopViewMessage(resources.getString(R.string.turn_on_gps_message))
            }
        }
    }

    private fun checkInternetAvailability() {
        connectivityManager.observe().onEach {
            when (it) {
                ConnectivityObserver.Status.Available -> {
                    if (isGpsEnabled(mainActivity)) {
                        mainActivity.hideTopViewMessage()
                        startLocationUpdates()
                        initMapForFirstTime()
                    } else {
                        mainActivity.showTopViewMessage(resources.getString(R.string.turn_on_gps_message))
                    }
                }

                ConnectivityObserver.Status.UnAvailable -> {
                    mainActivity.showTopViewMessage(resources.getString(R.string.turn_on_internet_message))
                }

                ConnectivityObserver.Status.Lost -> {
                    mainActivity.showTopViewMessage(resources.getString(R.string.turn_on_internet_message))
                }
            }
        }.launchIn(lifecycleScope)
    }

    private fun initMapForFirstTime() {
        if (isFirstTime) {
            binding.map.moveCamera(LatLng(35.715298, 51.404343), 0f)
            binding.map.setZoom(11.5f, 0f)
            isFirstTime = false
        }
    }

    private fun initLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity)
        settingsClient = LocationServices.getSettingsClient(mainActivity)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                // location is received
                userLocation = locationResult.lastLocation
                lastUpdateTime = DateFormat.getTimeInstance().format(Date())
                onLocationChange()
            }
        }
        locationRequest = LocationRequest.Builder(
            PRIORITY_HIGH_ACCURACY,
            UPDATE_INTERVAL_IN_MILLISECONDS
        ).build()
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        locationSettingsRequest = builder.build()
    }

    private fun onLocationChange() {
        userLocation?.let {
            addUserMarker(LatLng(it.latitude, it.longitude))
        }
    }

    private fun addUserMarker(loc: LatLng) {
        marker?.let { marker ->
            binding.map.removeMarker(marker)
        }
        val markStCr = MarkerStyleBuilder()
        markStCr.size = 14f
        markStCr.bitmap = BitmapUtils.createBitmapFromAndroidBitmap(
            BitmapFactory.decodeResource(
                resources, org.neshan.mapsdk.R.drawable.ic_marker
            )
        )
        val markSt = markStCr.buildStyle()
        marker = Marker(loc, markSt)
        binding.map.addMarker(marker)
    }

    private fun focusOnUserLocation() {
        if (connectivityManager.isNetworkAvailable()) {
            if (isGpsEnabled(mainActivity)) {
                if (userLocation != null) {
                    binding.map.moveCamera(
                        LatLng(userLocation!!.latitude, userLocation!!.longitude), 0.25f
                    )
                    binding.map.setZoom(15f, 0.25f)
                } else {
                    showToast(resources.getString(R.string.try_again))
                    startReceivingLocationUpdates()
                }
            } else {
                showToast(resources.getString(R.string.turn_on_gps_message))
            }
        } else {
            showToast(resources.getString(R.string.turn_on_internet_message))
        }
    }

    private fun startReceivingLocationUpdates() {
        if (isLocationPermissionsGranted()
        ) {
            if (connectivityManager.isNetworkAvailable()) {
                if (isGpsEnabled(mainActivity)) {
                    mainActivity.hideTopViewMessage()
                    startLocationUpdates()
                } else {
                    mainActivity.showTopViewMessage(resources.getString(R.string.turn_on_gps_message))
                }
            } else {
                mainActivity.showTopViewMessage(resources.getString(R.string.turn_on_internet_message))
            }
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (isLocationPermissionsGranted()) {
                if (connectivityManager.isNetworkAvailable()) {
                    if (isGpsEnabled(mainActivity)) {
                        startLocationUpdates()
                        mainActivity.hideTopViewMessage()
                    } else {
                        mainActivity.showTopViewMessage(resources.getString(R.string.turn_on_gps_message))
                    }
                } else {
                    mainActivity.showTopViewMessage(resources.getString(R.string.turn_on_internet_message))
                }
            } else {
                AlertCreator.showLocationPermissionDialog(mainActivity)
            }
        }
    }

    private fun isLocationPermissionsGranted() = (ContextCompat.checkSelfPermission(
        mainActivity,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
        mainActivity,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED)

    private fun startLocationUpdates() {
        locationSettingsRequest?.let { locationSettingsRequest ->
            settingsClient
                .checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(OnSuccessListener {
                    if (ContextCompat.checkSelfPermission(
                            mainActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                            mainActivity,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        showToast(resources.getString(R.string.access_location_permission_message))
                        return@OnSuccessListener
                    }
                    locationCallback?.let { locationCallback ->
                        fusedLocationClient.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.myLooper()
                        )
                    }
                })
                .addOnFailureListener { e ->
                    when ((e as ApiException).statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                            // Show the dialog by calling startResolutionForResult(), and check the
                            // result in onActivityResult().
                            val rae = e as ResolvableApiException
                            rae.startResolutionForResult(mainActivity, REQUEST_CODE)
                        } catch (sie: IntentSender.SendIntentException) {
                            Log.i(
                                TAG,
                                "PendingIntent unable to execute request."
                            )
                        }

                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            showToast(resources.getString(R.string.location_settings_are_inadequate))
                        }
                    }
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE -> {
                when (resultCode) {
                    RESULT_OK -> {
                        Log.e(
                            TAG,
                            "User agreed to make required location settings changes."
                        )
                        startLocationUpdates()
                    }

                    RESULT_CANCELED -> {
                        Log.e(
                            TAG,
                            "User choose not to make required location settings changes."
                        )
                    }
                }
            }
        }
    }

    private fun stopLocationUpdates() {
        locationCallback?.let { locationCallback ->
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }
}