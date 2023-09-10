package com.roohandeh.holoomapproject.data.database

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [SavedLocationEntity::class], version = 1)
abstract class LocationsDataBase : RoomDatabase(){
    abstract fun getDao(): LocationsDao
}