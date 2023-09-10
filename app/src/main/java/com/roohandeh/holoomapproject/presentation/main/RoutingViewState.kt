package com.roohandeh.holoomapproject.presentation.main

import org.neshan.servicessdk.direction.model.Route

class RoutingViewState(val routes: List<Route> = emptyList(),
                       val loading: Boolean = false,
                       val errorMessage: String? = null)