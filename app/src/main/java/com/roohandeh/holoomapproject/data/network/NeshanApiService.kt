package com.roohandeh.holoomapproject.data.network

import com.roohandeh.holoomapproject.utils.ROUTING_API_KEY_VALUE
import com.roohandeh.holoomapproject.utils.Resource
import org.neshan.common.model.LatLng
import org.neshan.servicessdk.direction.NeshanDirection
import org.neshan.servicessdk.direction.model.NeshanDirectionResult
import org.neshan.servicessdk.direction.model.Route
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NeshanApiService {

    fun getRoutes(sourceLocation: LatLng, destinationLocation: LatLng, callback: RoutingCallback) {
        NeshanDirection.Builder(
            ROUTING_API_KEY_VALUE, sourceLocation, destinationLocation
        ).build().call(object : Callback<NeshanDirectionResult?> {
            override fun onResponse(
                call: Call<NeshanDirectionResult?>, response: Response<NeshanDirectionResult?>
            ) {
                if (response.body() != null && response.body()!!.routes != null) {
                    if (response.body()!!.routes.isEmpty().not()) {
                        callback.response(Resource.Success(response.body()!!.routes))
                    } else {
                        callback.response(Resource.Error(message = "مسیری پیدا نشد"))
                    }
                } else {

                }
            }

            override fun onFailure(call: Call<NeshanDirectionResult?>, t: Throwable) {
                callback.response(Resource.Error(message = t.message))
            }
        })
    }
}

interface RoutingCallback {
    fun response(resource: Resource<List<Route>>)
}