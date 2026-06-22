package com.twr.developer.weatherguide.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class CachedWeather(
    @PrimaryKey val location: String,
    val jsonResponse: String, // Store full JSON to keep it simple
    val timestamp: Long = System.currentTimeMillis()
)