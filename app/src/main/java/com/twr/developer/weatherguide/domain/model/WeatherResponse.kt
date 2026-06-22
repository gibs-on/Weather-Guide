package com.twr.developer.weatherguide.domain.model

import com.google.gson.annotations.SerializedName


data class WeatherResponse(
    val location: Location,
    val current: Current,
    val daily: List<Daily>,
    val hourly: List<Hourly>,
//    @SerializedName("client_geo") val clientGeo: ClientGeo? = null
)

data class Current(
    val time: String,
    val temperature: Double,
    val wind_speed: Double,
    val wind_direction: Int,
    val condition_code: String,
    val icon: String,                    // ✅ Direct field, not nested
    val icon_path: String? = null,       // Optional
    val humidity: Int? = null,
    val feels_like: Double? = null,
    val wind_gust: Double? = null,
    val uv_index: Double? = null,
)

data class Condition(
    val text: String,
    val icon: String,
    val code: Int
)

data class Forecast(
    val forecastday: List<ForecastDay>
)

data class ForecastDay(
    val date: String,
    val day: Daily,
    val hour: List<Hourly>
)