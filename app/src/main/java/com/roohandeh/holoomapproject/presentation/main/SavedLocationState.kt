package com.roohandeh.holoomapproject.presentation.main

import com.roohandeh.holoomapproject.domain.model.SavedLocation

data class SavedLocationState(
    val savedLocations: List<SavedLocation>? = null,
    val savedLocationId: Long? = null,
    val loading: Boolean = false,
    val errorMessage: String? = null
)