package com.roohandeh.holoomapproject.presentation.main

import com.roohandeh.holoomapproject.domain.model.LocationAddress

data class LocationAddressViewState(
    val address: LocationAddress? = null,
    val loading: Boolean = false,
    val errorMessage: String? = null
)