package com.roohandeh.holoomapproject.domain.model

import com.roohandeh.holoomapproject.data.database.SavedLocationEntity

data class SavedLocation(
    val id: Int? = null,
    val lat: Double,
    val lng: Double,
    val caption: String
)

fun SavedLocation.toLocationEntity() = SavedLocationEntity(lat, lng, caption)