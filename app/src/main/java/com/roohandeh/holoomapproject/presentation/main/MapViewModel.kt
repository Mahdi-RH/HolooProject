package com.roohandeh.holoomapproject.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roohandeh.holoomapproject.data.network.RoutingCallback
import com.roohandeh.holoomapproject.domain.model.SavedLocation
import com.roohandeh.holoomapproject.domain.usecase.GetAddressUseCase
import com.roohandeh.holoomapproject.domain.usecase.GetLocationsUseCase
import com.roohandeh.holoomapproject.domain.usecase.GetRoutesUseCase
import com.roohandeh.holoomapproject.domain.usecase.SaveLocationUseCase
import com.roohandeh.holoomapproject.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.neshan.common.model.LatLng
import org.neshan.servicessdk.direction.model.Route
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getAddressUseCase: GetAddressUseCase,
    private val getRoutesUseCase: GetRoutesUseCase,
    private val saveLocationUseCase: SaveLocationUseCase,
    private val getLocationsUseCase: GetLocationsUseCase):ViewModel() {

    private val _address = MutableLiveData<LocationAddressViewState>()
    val address: LiveData<LocationAddressViewState> = _address

    private val _routing = MutableLiveData<RoutingViewState>()
    val routing: LiveData<RoutingViewState> = _routing

    private val _savedLocation = MutableLiveData<SavedLocationState>()
    val savedLocation: LiveData<SavedLocationState> = _savedLocation

    private val _savedLocations = MutableLiveData<SavedLocationState>()
    val savedLocations: LiveData<SavedLocationState> = _savedLocations

    fun getAddress(lat: Double, lng: Double) {
        getAddressUseCase(lat, lng).onEach {result->
            when (result) {
                is Resource.Loading -> {
                    _address.value = LocationAddressViewState(loading = true)
                }

                is Resource.Success -> {
                    _address.value = LocationAddressViewState(address = result.data)
                }

                is Resource.Error -> {
                    _address.value = LocationAddressViewState(errorMessage = result.message)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getRoutes(sourceLocation: LatLng, destinationLocation: LatLng) {
        viewModelScope.launch {
            getRoutesUseCase.invoke(sourceLocation, destinationLocation, object : RoutingCallback {
                override fun response(resource: Resource<List<Route>>) {
                    when (resource) {
                        is Resource.Loading -> {
                            _routing.value = RoutingViewState(loading = true)
                        }

                        is Resource.Success -> {
                            _routing.value = RoutingViewState(routes = resource.data ?: emptyList())
                        }

                        is Resource.Error -> {
                            _routing.value = RoutingViewState(errorMessage = resource.message)
                        }
                    }
                }
            })
        }
    }

    fun saveLocation(location: SavedLocation) {
        viewModelScope.launch {
            saveLocationUseCase.invoke(location).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _savedLocation.value = SavedLocationState(loading = true)
                    }

                    is Resource.Success -> {
                        _savedLocation.value = SavedLocationState(savedLocationId = result.data)
                    }

                    is Resource.Error -> {
                        _savedLocation.value = SavedLocationState(errorMessage = result.message)
                    }
                }

            }.launchIn(viewModelScope)
        }
    }

    fun getLocations() {
        viewModelScope.launch {
            getLocationsUseCase.invoke().onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _savedLocations.value = SavedLocationState(loading = true)
                    }

                    is Resource.Success -> {
                        _savedLocations.value =
                            SavedLocationState(savedLocations = result.data ?: emptyList())
                    }

                    is Resource.Error -> {
                        _savedLocations.value =
                            SavedLocationState(errorMessage = result.message)
                    }
                }

            }.launchIn(viewModelScope)
        }
    }

}