package com.twr.developer.weatherguide.data.local.database

import com.twr.developer.weatherguide.data.local.dao.WeatherDao
import androidx.room.Database
import androidx.room.RoomDatabase
import com.twr.developer.weatherguide.data.local.entity.CachedWeather

@Database(entities = [CachedWeather::class], version = 1, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}