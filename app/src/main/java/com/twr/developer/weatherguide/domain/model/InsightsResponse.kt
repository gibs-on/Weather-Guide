package com.twr.developer.weatherguide.domain.model

data class InsightsResponse(
    val location: InsightsLocation,
    val current: InsightsCurrent,
    val daily: List<InsightsDaily>? = null,
    val hourly: List<InsightsHourly>? = null,
    val insights: InsightsSummary
)

data class InsightsLocation(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val country: String
)

data class InsightsCurrent(
    val time: String,
    val temperature: Double,
    val wind_speed: Double,
    val wind_direction: Int,
    val condition_code: String,
    val icon: String,
    val humidity: Int? = null,
    val feels_like: Double? = null
)

data class InsightsDaily(
    val date: String,
    val temp_min: Double,
    val temp_max: Double,
    val precipitation_sum: Double,
    val precipitation_probability: Int,
    val wind_max: Double,
    val condition_code: String,
    val icon: String,
    val sunrise: String? = null,
    val sunset: String? = null
)

data class InsightsHourly(
    val time: String,
    val temperature: Double,
    val precipitation_probability: Int,
    val wind_speed: Double,
    val condition_code: String,
    val icon: String,
    val humidity: Int? = null,
    val feels_like: Double? = null,
    val wind_gust: Double? = null,
    val uv_index: Double? = null
)

data class InsightsSummary(
    val summary: String,                    // AI-generated summary
    val agronomic_context: AgronomicContext? = null, // For farming insights
    val risk_flags: List<RiskFlag>? = null,
    val recommendations: List<String>? = null
)

data class AgronomicContext(
    val crop_friendliness: String? = null,  // e.g., "Good for planting"
    val soil_moisture_trend: String? = null,
    val pest_risk: String? = null
)

data class RiskFlag(
    val type: String,                       // e.g., "frost", "drought"
    val severity: String,                   // e.g., "moderate", "high"
    val description: String
)