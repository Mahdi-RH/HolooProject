package com.roohandeh.holoomapproject.domain.usecase

import android.database.sqlite.SQLiteException
import com.roohandeh.holoomapproject.data.database.toLocation
import com.roohandeh.holoomapproject.domain.repository.MapRepository
import com.roohandeh.holoomapproject.utils.IO_DISPATCHER
import com.roohandeh.holoomapproject.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Named

class GetLocationsUseCase @Inject constructor(
    private val repository: MapRepository,
    @Named(IO_DISPATCHER) private val dispatcher: CoroutineDispatcher
) {
    operator fun invoke() = flow {
        try {
            emit(Resource.Loading())
            val locationEntities = repository.getLocations()
            val locations = locationEntities.map {
                it.toLocation()
            }
            emit(Resource.Success(locations))
        } catch (e: SQLiteException) {
            emit(Resource.Error(e.localizedMessage))
        }
    }.flowOn(dispatcher)
}