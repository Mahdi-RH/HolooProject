package com.roohandeh.holoomapproject.utils

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    fun observe(): Flow<Status>
    enum class Status {
        Available, UnAvailable, Lost
    }
}