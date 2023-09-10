package com.roohandeh.holoomapproject.data.network

import com.roohandeh.holoomapproject.data.network.model.LocationAddressResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MapApiService {
    @GET("reverse")
    suspend fun convertLatLongToAddress(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double
    ): LocationAddressResponse


}