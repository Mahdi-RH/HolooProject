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
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.carto.graphics.Color
import com.carto.styles.AnimationStyle
import com.carto.styles.AnimationStyleBuilder
import com.carto.styles.AnimationType
import com.carto.styles.LineStyle
import com.carto.styles.LineStyleBuilder
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.roohandeh.holoomapproject.R
import com.roohandeh.holoomapproject.databinding.FragmentMapBinding
import com.roohandeh.holoomapproject.databinding.SearachBottomSheetLayoutBinding
import com.roohandeh.holoomapproject.domain.model.LocationAddress
import com.roohandeh.holoomapproject.presentation.base.BaseBindingFragment
import com.roohandeh.holoomapproject.presentation.custom_views.CustomRoutingView
import com.roohandeh.holoomapproject.utils.ConnectivityObserver
import com.roohandeh.holoomapproject.utils.GpsStatus
import com.roohandeh.holoomapproject.utils.MAP_ZOOM_DEGREE
import com.roohandeh.holoomapproject.utils.NetworkConnectivityManager
import com.roohandeh.holoomapproject.utils.REQUEST_CODE
import com.roohandeh.holoomapproject.utils.TEHRAN_LATITUDE
import com.roohandeh.holoomapproject.utils.TEHRAN_LONGITUDE
import com.roohandeh.holoomapproject.utils.UPDATE_INTERVAL_IN_MILLISECONDS
import com.roohandeh.holoomapproject.utils.fadeIn
import com.roohandeh.holoomapproject.utils.fadeOut
import com.roohandeh.holoomapproject.utils.isGpsEnabled
import com.roohandeh.holoomapproject.utils.showToast
import com.roohandeh.holoomapproject.utils.slideDown
import com.roohandeh.holoomapproject.utils.slideUp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.neshan.common.model.LatLng
import org.neshan.common.utils.PolylineEncoding
import org.neshan.mapsdk.MapView
import org.neshan.mapsdk.MapView.LogoType
import org.neshan.mapsdk.internal.utils.BitmapUtils
import org.neshan.mapsdk.model.Marker
import org.neshan.mapsdk.model.Polyline
import org.neshan.servicessdk.direction.model.Route
import java.text.DateFormat
import java.util.Date

private const val TAG = "UserLocationUpdater"
@AndroidEntryPoint
class MapFragment : BaseBindingFragment<FragmentMapBinding>() {

    private var isRouteFetched = false
    private var isFirstTime: Boolean = true
    private var userLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var settingsClient: SettingsClient
    private lateinit var locationRequest: LocationRequest
    private var locationSettingsRequest: LocationSettingsRequest? = null
    private var locationCallback: LocationCallback? = null
    private var lastUpdateTime: String? = null
    private var myLocationMarker: Marker? = null
    private var currentMarker: Marker? = null
    private lateinit var mainActivity: MainActivity
    private lateinit var connectivityManager: NetworkConnectivityManager
    private lateinit var progressView: ProgressBar
    private lateinit var routingViewContainer: FrameLayout
    private lateinit var animSt: AnimationStyle
    private lateinit var mapView: MapView
    private var onMapPolyline: Polyline? = null
    private lateinit var editAddressSearch: EditText
    private lateinit var focusButton: FloatingActionButton
    private var searchBottomSheetDialog: BottomSheetDialog? = null

    private val viewModel: MapViewModel by viewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentMapBinding =
        { layoutInflater, viewGroup, attachedToParent ->
            FragmentMapBinding.inflate(layoutInflater, viewGroup, attachedToParent)
        }

    override fun onStart() {
        super.onStart()
        initLayoutReferences()
        initLocation()
        startReceivingLocationUpdates()
    }

    override fun initView() {
        mainActivity = activity as MainActivity
        connectivityManager = mainActivity.networkConnectivityManager
        mapView = binding.map
        mapView.setLogoType(LogoType.TRANSPARENT)
        editAddressSearch = binding.editAddressSearch
        routingViewContainer = binding.routingViewContainer
        focusButton = binding.focusButton
        checkInternetAvailability()
        checkGpsAvailability()
        focusButton.setOnClickListener {
            focusOnUserLocation()
        }
        initAddressSearchView()
        observeAddress()
        observeRouting()
    }

    private fun initAddressSearchView() {
        editAddressSearch.setOnClickListener {
            showSearchBottomSheetDialog { lat, lng ->
                if (lat.isNullOrEmpty() or lng.isNullOrEmpty()) {
                    showToast(resources.getString(R.string.enter_lat_long_error_message))
                } else {
                    val latLng = createLatLngObject(lat!!.toDouble(), lng!!.toDouble())
                    fetchAddress(latLng)
                }
            }
        }
    }

    private fun fetchAddress(latLng: LatLng) {
        addNewMarkerToMap(latLng)
        dismissSearchBottomSheetDialog()
        viewModel.getAddress(latLng.latitude, latLng.longitude)
    }

    private fun createLatLngObject(lat: Double, lng: Double) =
        LatLng(lat, lng)

    private fun observeAddress() {
        viewModel.address.observe(viewLifecycleOwner) { addressState ->
            when {
                addressState.loading -> {
                    mainActivity.setProgressbarVisibility(true)
                }

                addressState.address != null -> {
                    mainActivity.setProgressbarVisibility(false)
                    showRoutingView(addressState.address)
                }

                addressState.errorMessage.isNullOrEmpty().not() -> {
                    mainActivity.setProgressbarVisibility(false)
                    showToast(addressState.errorMessage!!)
                }
            }
        }
    }

    private fun initLayoutReferences() {
        mapView.setOnMapLongClickListener {
            addNewMarkerToMap(it)
            fetchAddress(it)
        }
    }

    private fun addNewMarkerToMap(it: LatLng) {
        val marker = createMarker(it)
        removeCurrentMarker()
        currentMarker = marker
        mapView.addMarker(marker)
        mapView.moveCamera(it, 0f)
    }

    private fun checkGpsAvailability() {
        mainActivity.gpsSwitchStateReceiver.gpsStatus.observe(this) { gpsStatus ->
            if (gpsStatus == GpsStatus.Available) {
                if (connectivityManager.isNetworkAvailable()) {
                    mainActivity.hideTopViewMessage()
                    startLocationUpdates()
                    initMapForFirstTime()
                } else {
                    mainActivity.showTopViewMessage(resources.getString(R.string.turn_on_internet_message))
                }
            } else if (gpsStatus == GpsStatus.UnAvailable) {
                mainActivity.showTopViewMessage(resources.getString(R.string.turn_on_gps_message))
            }
        }
    }

    private fun checkInternetAvailability() {
        connectivityManager.observe().onEach { connectivityStatus ->
            when (connectivityStatus) {
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
            mapView.moveCamera(createLatLngObject(TEHRAN_LATITUDE, TEHRAN_LONGITUDE), 0f)
            mapView.setZoom(MAP_ZOOM_DEGREE, 0f)
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
            addUserMarker(createLatLngObject(it.latitude, it.longitude))
        }
    }

    private fun addUserMarker(loc: LatLng) {
        myLocationMarker?.let { marker ->
            mapView.removeMarker(marker)
        }
        val markStCr = MarkerStyleBuilder()
        markStCr.size = 14f
        markStCr.bitmap = BitmapUtils.createBitmapFromAndroidBitmap(
            BitmapFactory.decodeResource(
                resources, org.neshan.mapsdk.R.drawable.ic_marker
            )
        )
        val markSt = markStCr.buildStyle()
        myLocationMarker = Marker(loc, markSt)
        mapView.addMarker(myLocationMarker)
    }

    private fun focusOnUserLocation() {
        if (connectivityManager.isNetworkAvailable()) {
            if (isGpsEnabled(mainActivity)) {
                if (userLocation != null) {
                    mapView.moveCamera(
                        createLatLngObject(userLocation!!.latitude, userLocation!!.longitude), 0.25f
                    )
                    mapView.setZoom(15f, 0.25f)
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
                showLocationPermissionDialog(mainActivity)
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

    private fun createMarker(loc: LatLng): Marker {
        val animStBl = AnimationStyleBuilder()
        animStBl.fadeAnimationType = AnimationType.ANIMATION_TYPE_SMOOTHSTEP
        animStBl.sizeAnimationType = AnimationType.ANIMATION_TYPE_SPRING
        animStBl.phaseInDuration = 0.5f
        animStBl.phaseOutDuration = 0.5f
        animSt = animStBl.buildStyle()

        val markStCr = MarkerStyleBuilder()
        markStCr.size = 30f
        markStCr.bitmap = BitmapUtils.createBitmapFromAndroidBitmap(
            BitmapFactory.decodeResource(
                resources, org.neshan.mapsdk.R.drawable.ic_cluster_marker_blue
            )
        )
        markStCr.animationStyle = animSt
        val markSt = markStCr.buildStyle()
        return Marker(loc, markSt)
    }

    private fun stopLocationUpdates() {
        locationCallback?.let { locationCallback ->
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    private fun showSearchBottomSheetDialog(
        performSearch: (lat: String?, lng: String?) -> Unit
    ) {
        context?.let { context ->
            searchBottomSheetDialog = BottomSheetDialog(context)
            val view = SearachBottomSheetLayoutBinding.inflate(layoutInflater)
            searchBottomSheetDialog?.setContentView(view.root)
            view.btnSearch.setOnClickListener {
                val lat = view.editLat.text.toString()
                val lng = view.editLong.text.toString()
                performSearch(lat, lng)
            }
        }
        searchBottomSheetDialog?.show()
    }

    private fun dismissSearchBottomSheetDialog() {
        searchBottomSheetDialog?.let { dialog ->
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }
    }

    private fun showRoutingView(locationAddress: LocationAddress) {
        context?.let { context ->
            val view =
                CustomRoutingView(context, locationAddress)
            isRouteFetched = false
            removeRouteFromMap()
            focusButton.fadeOut()
            editAddressSearch.fadeOut()
            routingViewContainer.removeAllViews()
            routingViewContainer.addView(view)
            routingViewContainer.slideUp()
            view.closeButton.setOnClickListener {
                removeCurrentMarker()
                isRouteFetched = false
                removeRouteFromMap()
                routingViewContainer.slideDown()
                focusButton.fadeIn()
                editAddressSearch.fadeIn()
            }
            view.routingButton.setOnClickListener {
                if (!isRouteFetched) {
                    fetchRoute()
                    isRouteFetched = true
                }
            }
        }
    }
    private fun removeCurrentMarker() {
        if (currentMarker != null) {
            mapView.removeMarker(currentMarker)
            currentMarker = null
        }
    }

    private fun fetchRoute() {
        viewModel.getRoutes(
            createLatLngObject(
                userLocation!!.latitude,
                userLocation!!.longitude
            ), currentMarker!!.latLng
        )
    }

    private fun removeRouteFromMap() {
        if (onMapPolyline != null) {
            mapView.removePolyline(onMapPolyline)
            onMapPolyline = null
        }
    }

    private fun observeRouting() {
        viewModel.routing.observe(viewLifecycleOwner) { routingState ->
            when {
                routingState.loading -> {
                    mainActivity.setProgressbarVisibility(true)
                    isRouteFetched = false
                }

                routingState.routes.isEmpty().not() -> {
                    val bestRoute = 0
                    val route = routingState.routes[bestRoute]
                    drawRouteOnMap(route)
                }

                routingState.errorMessage.isNullOrEmpty().not() -> {
                    isRouteFetched = false
                    showToast(routingState.errorMessage!!)
                }
            }
        }
    }

    private fun drawRouteOnMap(route: Route) {
        val routeOverviewPolylinePoints = ArrayList(
            PolylineEncoding.decode(
                route.overviewPolyline.encodedPolyline
            )
        )
        val decodedStepByStepPath = ArrayList<LatLng>()
        for (step in route.legs[0].directionSteps) {
            decodedStepByStepPath.addAll(PolylineEncoding.decode(step.encodedPolyline))
        }
        onMapPolyline = Polyline(routeOverviewPolylinePoints, getLineStyle())
        addRouteToMap()
    }

    private fun addRouteToMap() {
        if (onMapPolyline != null) {
            mapView.addPolyline(onMapPolyline)
        }
    }

    private fun getLineStyle(): LineStyle {
        val lineStyleBuilder = LineStyleBuilder()
        lineStyleBuilder.color = Color(2, 119, 189, 190)
        lineStyleBuilder.width = 4f
        return lineStyleBuilder.buildStyle()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }
}