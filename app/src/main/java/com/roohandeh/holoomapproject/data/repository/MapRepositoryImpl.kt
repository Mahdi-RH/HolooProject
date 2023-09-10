package com.roohandeh.holoomapproject.data.repository

import com.roohandeh.holoomapproject.data.network.MapApiService
import com.roohandeh.holoomapproject.data.network.NeshanApiService
import com.roohandeh.holoomapproject.data.network.RoutingCallback
import com.roohandeh.holoomapproject.data.network.model.LocationAddressResponse
import com.roohandeh.holoomapproject.domain.repository.MapRepository
import org.neshan.common.model.LatLng
import javax.inject.Inject


class MapRepositoryImpl @Inject constructor(private val apiService: MapApiService,private val neshanApiService: NeshanApiService):MapRepository {
    override suspend fun getAddress(lat:Double,lng:Double): LocationAddressResponse {
        return apiService.convertLatLongToAddress(lat, lng)
    }
    override fun getRoutes(
        sourceLocation: LatLng,
        destinationLocation: LatLng,
        callback: RoutingCallback
    ) = neshanApiService.getRoutes(sourceLocation, destinationLocation,callback)



}