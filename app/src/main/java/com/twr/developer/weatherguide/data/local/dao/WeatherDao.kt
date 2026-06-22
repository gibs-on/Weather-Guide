package com.twr.developer.weatherguide.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.twr.developer.weatherguide.data.local.entity.CachedWeather

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather_cache WHERE location = :location")
    suspend fun get(location: String): CachedWeather?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cached: CachedWeather)

    @Query("DELETE FROM weather_cache WHERE timestamp < :expiryTime")
    suspend fun deleteOldEntries(expiryTime: Long)
}