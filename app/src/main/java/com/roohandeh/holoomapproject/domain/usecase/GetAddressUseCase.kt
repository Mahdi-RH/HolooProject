package com.roohandeh.holoomapproject.domain.usecase

import com.roohandeh.holoomapproject.domain.model.LocationAddress
import com.roohandeh.holoomapproject.domain.repository.MapRepository
import com.roohandeh.holoomapproject.utils.IO_DISPATCHER
import com.roohandeh.holoomapproject.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

class GetAddressUseCase @Inject constructor(
    private val repository: MapRepository,
    @Named(IO_DISPATCHER) private val dispatcher: CoroutineDispatcher
) {
    operator fun invoke(lat: Double, lng: Double) = flow {
        try {
            emit(Resource.Loading())
            val addressResponse = repository.getAddress(lat, lng)
            emit(
                Resource.Success(
                    LocationAddress(
                        addressResponse.formattedAddress ?: "آدرس انتخاب شده",
                        addressResponse.routeName ?: "مکان انتخاب شده"
                    )
                )
            )
        } catch (ioException: IOException) {
            emit(Resource.Error(ioException.message))
        } catch (httpException: HttpException) {
            emit(Resource.Error(httpException.message))
        }
    }.flowOn(dispatcher)
}