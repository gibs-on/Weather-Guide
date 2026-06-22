package com.twr.developer.weatherguide.domain.repository

import com.twr.developer.weatherguide.domain.model.InsightsResponse
import com.twr.developer.weatherguide.domain.model.Location
import com.twr.developer.weatherguide.domain.model.UsageResponse
import com.twr.developer.weatherguide.domain.model.WeatherResponse

interface WeatherRepository {
    // Free tier
    suspend fun getWeather(location: Location): WeatherResponse
    suspend fun getForecast(location: Location): WeatherResponse

    // Pro tier
    suspend fun getPremiumForecast(location: Location): WeatherResponse
    suspend fun getAIInsights(location: Location): InsightsResponse

    // Usage
    suspend fun getUsage(): UsageResponse
}