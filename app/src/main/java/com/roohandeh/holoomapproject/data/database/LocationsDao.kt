package com.roohandeh.holoomapproject.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
@Dao
interface LocationsDao {
    @Query("SELECT * FROM map_locations")
    suspend fun getAllLocations(): List<SavedLocationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: SavedLocationEntity): Long

}