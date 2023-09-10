package com.roohandeh.holoomapproject.presentation.main


data class SavedLocationState(
    val savedLocationId: Long? = null,
    val loading: Boolean = false,
    val errorMessage: String? = null
)