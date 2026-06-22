package com.twr.developer.weatherguide.domain.model

/**
 * Maps WeatherAI condition codes to human-readable text and appropriate icons
 * Based on WMO (World Meteorological Organization) weather codes
 */
object WeatherConditionMapper {
    
    // Map condition code to display text
    fun getConditionText(code: String): String {
        return when (code) {
            // Clear/ Sunny
            "0" -> "Clear Sky"
            "1" -> "Mainly Clear"
            
            // Partly cloudy
            "2" -> "Partly Cloudy"
            "3" -> "Overcast"
            
            // Fog/Mist
            "45" -> "Fog"
            "48" -> "Depositing Rime Fog"
            
            // Drizzle
            "51" -> "Light Drizzle"
            "53" -> "Moderate Drizzle"
            "55" -> "Dense Drizzle"
            "56" -> "Light Freezing Drizzle"
            "57" -> "Dense Freezing Drizzle"
            
            // Rain
            "61" -> "Light Rain"
            "63" -> "Moderate Rain"
            "65" -> "Heavy Rain"
            "66" -> "Light Freezing Rain"
            "67" -> "Heavy Freezing Rain"
            
            // Snow
            "71" -> "Light Snow"
            "73" -> "Moderate Snow"
            "75" -> "Heavy Snow"
            "77" -> "Snow Grains"
            
            // Rain showers
            "80" -> "Light Rain Showers"
            "81" -> "Moderate Rain Showers"
            "82" -> "Heavy Rain Showers"
            
            // Snow showers
            "85" -> "Light Snow Showers"
            "86" -> "Heavy Snow Showers"
            
            // Thunderstorms
            "95" -> "Thunderstorm"
            "96" -> "Thunderstorm with Hail"
            "99" -> "Heavy Thunderstorm with Hail"
            
            else -> "Unknown"
        }
    }
    
    // Map condition code to emoji
    fun getConditionEmoji(code: String): String {
        return when (code) {
            "0", "1" -> "☀️"
            "2" -> "⛅"
            "3" -> "☁️"
            "45", "48" -> "🌫️"
            "51", "53", "55" -> "🌦️"
            "56", "57" -> "🌧️"
            "61", "63", "65" -> "🌧️"
            "66", "67" -> "🌧️❄️"
            "71", "73", "75" -> "❄️"
            "77" -> "🌨️"
            "80", "81", "82" -> "🌧️"
            "85", "86" -> "🌨️"
            "95", "96", "99" -> "⛈️"
            else -> "🌤️"
        }
    }
    
    // Get weather type category
    fun getWeatherCategory(code: String): WeatherCategory {
        return when (code) {
            "0", "1" -> WeatherCategory.CLEAR
            "2", "3" -> WeatherCategory.CLOUDY
            "45", "48" -> WeatherCategory.FOGGY
            "51", "53", "55", "56", "57" -> WeatherCategory.DRIZZLE
            "61", "63", "65", "66", "67" -> WeatherCategory.RAINY
            "71", "73", "75", "77", "85", "86" -> WeatherCategory.SNOWY
            "80", "81", "82" -> WeatherCategory.RAINY
            "95", "96", "99" -> WeatherCategory.STORMY
            else -> WeatherCategory.UNKNOWN
        }
    }
    
    // Get color for weather condition (for UI theming)
    fun getConditionColor(code: String): androidx.compose.ui.graphics.Color {
        val category = getWeatherCategory(code)
        return when (category) {
            WeatherCategory.CLEAR -> androidx.compose.ui.graphics.Color(0xFFFF9800) // Orange
            WeatherCategory.CLOUDY -> androidx.compose.ui.graphics.Color(0xFF78909C) // Blue Grey
            WeatherCategory.FOGGY -> androidx.compose.ui.graphics.Color(0xFFBDBDBD) // Light Grey
            WeatherCategory.DRIZZLE -> androidx.compose.ui.graphics.Color(0xFF4FC3F7) // Light Blue
            WeatherCategory.RAINY -> androidx.compose.ui.graphics.Color(0xFF1976D2) // Blue
            WeatherCategory.SNOWY -> androidx.compose.ui.graphics.Color(0xFFE3F2FD) // Very Light Blue
            WeatherCategory.STORMY -> androidx.compose.ui.graphics.Color(0xFFD32F2F) // Red
            WeatherCategory.UNKNOWN -> androidx.compose.ui.graphics.Color(0xFF9E9E9E) // Grey
        }
    }
}

enum class WeatherCategory {
    CLEAR,
    CLOUDY,
    FOGGY,
    DRIZZLE,
    RAINY,
    SNOWY,
    STORMY,
    UNKNOWN
}