package com.twr.developer.weatherguide.domain.model

data class PremiumWeatherResponse(
    val forecast: WeatherResponse,          // 14-day forecast
    val insights: InsightsResponse? = null  // AI insights (optional)
)