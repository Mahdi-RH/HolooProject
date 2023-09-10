package com.roohandeh.holoomapproject.domain.repository

import com.roohandeh.holoomapproject.data.network.RoutingCallback
import com.roohandeh.holoomapproject.data.network.model.LocationAddressResponse
import org.neshan.common.model.LatLng

interface MapRepository {
    suspend fun getAddress(lat: Double, lng: Double): LocationAddressResponse

     fun getRoutes(
        sourceLocation: LatLng,
        destinationLocation: LatLng,
        callback: RoutingCallback
    )

}