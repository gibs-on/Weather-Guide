package com.twr.developer.weatherguide.domain.model

import com.google.gson.annotations.SerializedName

data class Daily(
    val date: String,
    @SerializedName("temp_min")
    val temp_min: Double = 0.0,
    @SerializedName("temp_max")
    val temp_max: Double = 0.0,
    @SerializedName("precipitation_sum")
    val precipitation_sum: Double = 0.0,
    val sunrise: String? = null,
    val sunset: String? = null,
    @SerializedName("condition_code")
    val condition_code: String = "0",
    val icon: String = "",
    @SerializedName("precipitation_probability")
    val precipitation_probability: Int? = null,
    @SerializedName("wind_max")
    val wind_max: Double = 0.0,
    @SerializedName("icon_path")
    val icon_path: String? = null
)