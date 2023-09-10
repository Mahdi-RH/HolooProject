package com.roohandeh.holoomapproject.domain.usecase

import android.database.sqlite.SQLiteException
import com.roohandeh.holoomapproject.domain.model.SavedLocation
import com.roohandeh.holoomapproject.domain.model.toLocationEntity
import com.roohandeh.holoomapproject.domain.repository.MapRepository
import com.roohandeh.holoomapproject.utils.IO_DISPATCHER
import com.roohandeh.holoomapproject.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Named

class SaveLocationUseCase @Inject constructor(
    private val repository: MapRepository,
    @Named(IO_DISPATCHER) private val dispatcher: CoroutineDispatcher
) {
    operator fun invoke(location: SavedLocation) = flow {
        try {
            emit(Resource.Loading())
            val savedLocation = repository.insertLocation(location.toLocationEntity())
            emit(Resource.Success(savedLocation))
        } catch (e: SQLiteException) {
            emit(Resource.Error(e.localizedMessage))
        }
    }.flowOn(dispatcher)
}