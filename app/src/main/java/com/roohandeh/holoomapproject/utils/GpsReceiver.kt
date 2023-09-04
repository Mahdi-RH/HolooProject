package com.roohandeh.holoomapproject.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


class GpsReceiver : BroadcastReceiver() {

    private val _gpsStatus = MutableLiveData<GpsStatus>()
     val gpsStatus :LiveData<GpsStatus> = _gpsStatus

    override fun onReceive(context: Context, intent: Intent) {
        intent.action?.let {
            if (it == "android.location.PROVIDERS_CHANGED") {
                if (isGpsEnabled(context)) {
                    _gpsStatus.postValue(GpsStatus.Available)
                } else {
                    _gpsStatus.postValue(GpsStatus.UnAvailable)
                }
            }
        }
    }
}
fun isGpsEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(
        LocationManager.GPS_PROVIDER
    )
}
enum class GpsStatus {
    Available, UnAvailable
}