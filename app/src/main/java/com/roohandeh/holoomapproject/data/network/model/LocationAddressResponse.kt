package com.roohandeh.holoomapproject.data.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocationAddressResponse(
    @Json(name = "status") val status: String? = null,
    @Json(name = "formatted_address") val formattedAddress: String? = null,
    @Json(name = "route_name") val routeName: String? = null,
    @Json(name = "route_type") val routeType: String? = null,
    @Json(name = "neighbourhood") val neighbourhood: String? = null,
    @Json(name = "city") val city: String? = null,
    @Json(name = "state") val state: String? = null,
    @Json(name = "place") var place: String? = null,
    @Json(name = "municipality_zone") val municipalityZone: String? = null,
    @Json(name = "in_traffic_zone") val inTrafficZone: Boolean? = null,
    @Json(name = "in_odd_even_zone") val inOddEvenZone: Boolean? = null,
    @Json(name = "village") var village: String? = null,
    @Json(name = "county") val county: String? = null,
    @Json(name = "district") val district: String? = null
)