package com.twr.developer.weatherguide.domain.model

import com.google.gson.annotations.SerializedName

data class Hourly(
    val time: String,
    val temperature: Double,
    @SerializedName("precipitation_probability")
    val precipitation_probability: Int = 0,
    @SerializedName("wind_speed")
    val wind_speed: Double = 0.0,
    @SerializedName("condition_code")
    val condition_code: String = "0",
    val icon: String = "",
    val humidity: Int? = null,
    @SerializedName("feels_like")
    val feels_like: Double? = null,
    @SerializedName("wind_gust")
    val wind_gust: Double? = null,
    @SerializedName("uv_index")
    val uv_index: Double? = null,
    @SerializedName("icon_path")
    val icon_path: String? = null
) {
    // Helper to get precipitation in mm (if available)
    fun getPrecipitation(): Double = 0.0 // Placeholder - actual field may vary
}