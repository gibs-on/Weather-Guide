package com.twr.developer.weatherguide.data.remote.api

import com.twr.developer.weatherguide.domain.model.InsightsResponse
import com.twr.developer.weatherguide.domain.model.UsageResponse
import com.twr.developer.weatherguide.domain.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface WeatherApiService {

    // Free Tier: 7-day forecast
    @GET("weather")
    suspend fun getWeather(
        @Header("Authorization") auth: String,  // Bearer token
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric"
    ): WeatherResponse

    // Free Tier: 7-day forecast
    @GET("forecast")
    suspend fun getForecast(
        @Header("Authorization") auth: String,  // Bearer token
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("days") days: Int = 7,
        @Query("units") units: String = "metric"
    ): WeatherResponse

    // Free Tier: Current weather only
    @GET("current")
    suspend fun getCurrent(
        @Header("Authorization") auth: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric"
    ): WeatherResponse

    // Pro Tier: 14-day forecast
    @GET("forecast14")
    suspend fun getForecast14(
        @Header("Authorization") auth: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("days") days: Int = 14,
        @Query("units") units: String = "metric"
    ): WeatherResponse

    // Pro Tier: AI Insights
    @GET("insights")
    suspend fun getInsights(
        @Header("Authorization") auth: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("days") days: Int = 7,
        @Query("units") units: String = "metric"
    ): InsightsResponse

    // Usage tracking
    @GET("usage")
    suspend fun getUsage(
        @Header("Authorization") auth: String
    ): UsageResponse
}