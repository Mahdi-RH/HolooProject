package com.roohandeh.holoomapproject.domain.usecase

import com.roohandeh.holoomapproject.data.network.RoutingCallback
import com.roohandeh.holoomapproject.domain.repository.MapRepository
import org.neshan.common.model.LatLng
import javax.inject.Inject

class GetRoutesUseCase @Inject constructor(
    private val repository: MapRepository
) {
    operator fun invoke(
        sourceLocation: LatLng,
        destinationLocation: LatLng,
        callback: RoutingCallback
    ) {
        repository.getRoutes(sourceLocation, destinationLocation, callback)
    }
}

