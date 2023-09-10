package com.roohandeh.holoomapproject.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.roohandeh.holoomapproject.domain.model.SavedLocation

@Entity(tableName = "map_locations")
data class SavedLocationEntity(
    @ColumnInfo(name = "latitude") val lat: Double,
    @ColumnInfo(name = "longitude") val lng: Double,
    @ColumnInfo(name = "caption") val caption: String
) {
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

fun SavedLocationEntity.toLocation() = SavedLocation( lat, lng, caption)
