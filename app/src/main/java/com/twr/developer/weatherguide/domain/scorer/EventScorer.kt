package com.twr.developer.weatherguide.domain.scorer

import com.twr.developer.weatherguide.domain.model.Hourly
import com.twr.developer.weatherguide.domain.model.WeatherResponse
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventScorer : WeatherScorer {

    override fun calculate(weather: WeatherResponse): ScoreResult {
        // ✅ Use your Hourly class directly from weather.hourly
        val hourlyData = weather.hourly?: emptyList()
        if (hourlyData.isEmpty()) {
            return ScoreResult(
                overall = 0,
                subScores = emptyMap(),
                insights = listOf("No hourly data available"),
                action = "Unable to provide event recommendations"
            )
        }

        // Score each hour
        val scoredHours = hourlyData.map { hour ->
            hour.time to calculateEventScore(hour)
        }.toMap()

        // Find best hour
        val bestHour = scoredHours.maxByOrNull { it.value }
        val bestTime = bestHour?.key?.let { parseTime(it) }

        // Calculate overall average
        val avgScore = if (scoredHours.isNotEmpty()) scoredHours.values.average().toInt() else 0

        // Generate insights
        val insights = mutableListOf<String>()
        insights.add("Best time: ${bestTime?.time ?: "Unknown"} (Score: ${bestHour?.value ?: 0}%)")
        insights.add("Total hours evaluated: ${scoredHours.size}")

        // Determine action
        val action = when {
            (bestHour?.value ?: 0) >= 80 -> "✅ Perfect hour for your event!"
            (bestHour?.value ?: 0) >= 60 -> "👍 Good conditions. Consider this time."
            (bestHour?.value ?: 0) >= 40 -> "⚠️ Acceptable but have a backup plan."
            else -> "❌ Poor conditions. Consider indoor alternatives."
        }

        return ScoreResult(
            overall = avgScore,
            subScores = mapOf(
                "Best Hour Score" to (bestHour?.value ?: 0)
            ),
            insights = insights,
            action = action,
            bestDay = bestHour?.key
        )
    }

    fun calculateEventScore(hour: Hourly): Int {
        // Ideal: 18-25°C, 0% rain, < 15 km/h wind
        var score = 0

        // ✅ Temperature (40 points)
        val temp = hour.temperature
        score += when {
            temp in 18.0..25.0 -> 40
            temp in 15.0..28.0 -> 20
            else -> 5
        }

        // ✅ Rain (40 points)
        val rainProb = hour.precipitation_probability
        score += when {
            rainProb <= 10 -> 40
            rainProb <= 30 -> 25
            rainProb <= 50 -> 10
            else -> 0
        }

        // ✅ Wind (20 points)
        val windSpeed = hour.wind_speed
        score += when {
            windSpeed < 15 -> 20
            windSpeed < 25 -> 10
            else -> 0
        }

        return score
    }

    private fun parseTime(timeStr: String?): ParsedTime? {
        if (timeStr.isNullOrEmpty()) return null
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
            val date = inputFormat.parse(timeStr) ?: return null
            val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

            ParsedTime(
                date = date,
                dayOfWeek = dayFormat.format(date),
                time = timeFormat.format(date)
            )
        } catch (e: Exception) {
            null
        }
    }

    data class ParsedTime(
        val date: Date,
        val dayOfWeek: String,
        val time: String
    )
}