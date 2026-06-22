package com.twr.developer.weatherguide.domain.scorer

import com.twr.developer.weatherguide.domain.model.Daily
import com.twr.developer.weatherguide.domain.model.ForecastDay
import com.twr.developer.weatherguide.domain.model.WeatherResponse
import java.text.SimpleDateFormat
import java.util.*

enum class AdventureType {
    HIKING,
    SKIING,
    SURFING
}

class AdventureScorer : WeatherScorer {

    override fun calculate(weather: WeatherResponse): ScoreResult {
        // ✅ Use your Daily class directly from weather.daily
        val dailyData = weather.daily ?: emptyList()
        if (dailyData.isEmpty()) {
            return ScoreResult(
                overall = 0,
                subScores = emptyMap(),
                insights = listOf("No forecast data available"),
                action = "Unable to provide adventure recommendations"
            )
        }

        // Calculate scores for each day and each activity
        val hikingScores = dailyData.map { day ->
            day.date to calculateHikingScore(day)
        }.toMap()

        val skiingScores = dailyData.map { day ->
            day.date to calculateSkiingScore(day)
        }.toMap()

        val surfingScores = dailyData.map { day ->
            day.date to calculateSurfingScore(day)
        }.toMap()

        // Find best days
        val bestHikingDay = hikingScores.maxByOrNull { it.value }
        val bestSkiingDay = skiingScores.maxByOrNull { it.value }
        val bestSurfingDay = surfingScores.maxByOrNull { it.value }

        // Overall best day (average of all activities)
        val overallScores = dailyData.map { forecastDay ->
            val date = forecastDay.date
            (hikingScores[date] ?: (0 + (skiingScores[date] ?: 0) + (surfingScores[date] ?: 0))) / 3
        }
        val overallAvg = if (overallScores.isNotEmpty()) overallScores.average().toInt() else 0
        val bestOverallDay = dailyData.zip(overallScores).maxByOrNull { it.second }?.first?.date

        // Generate insights
        val insights = mutableListOf<String>()
        insights.add("🥾 Hiking: ${bestHikingDay?.value ?: 0}% (${formatDate(bestHikingDay?.key)})")
        insights.add("⛷️ Skiing: ${bestSkiingDay?.value ?: 0}% (${formatDate(bestSkiingDay?.key)})")
        insights.add("🏄 Surfing: ${bestSurfingDay?.value ?: 0}% (${formatDate(bestSurfingDay?.key)})")

        // Determine action
        val action = when {
            overallAvg >= 80 -> "🏆 Perfect conditions for all adventures!"
            overallAvg >= 60 -> "👍 Good conditions. Choose your activity wisely."
            overallAvg >= 40 -> "⚠️ Moderate conditions. Check details before heading out."
            else -> "❌ Poor conditions. Best to stay indoors today."
        }

        return ScoreResult(
            overall = overallAvg,
            subScores = mapOf(
                "Hiking" to (bestHikingDay?.value ?: 0),
                "Skiing" to (bestSkiingDay?.value ?: 0),
                "Surfing" to (bestSurfingDay?.value ?: 0)
            ),
            insights = insights,
            action = action,
            bestDay = bestOverallDay
        )
    }

    private fun calculateHikingScore(day: Daily): Int {
        // Ideal: 15-22°C, < 10% rain, < 15 km/h wind
        var score = 0

        // ✅ Temperature (40 points)
        val maxTemp = day.temp_max
        score += when {
            maxTemp in 15.0..22.0 -> 40
            maxTemp in 10.0..28.0 -> 20
            else -> 5
        }

        // ✅ Rain (40 points)
        val rainProb = day.precipitation_probability ?: 0
        score += when {
            rainProb <= 10 -> 40
            rainProb <= 30 -> 25
            rainProb <= 50 -> 10
            else -> 0
        }

        // ✅ Wind (20 points)
        val maxWind = day.wind_max
        score += when {
            maxWind < 15 -> 20
            maxWind < 25 -> 10
            else -> 0
        }

        return score
    }

    private fun calculateSkiingScore(day: Daily): Int {
        // Ideal: < -2°C, heavy snow, overcast
        var score = 0

        // ✅ Temperature (50 points)
        val maxTemp = day.temp_max
        score += when {
            maxTemp < -2 -> 50
            maxTemp < 2 -> 30
            maxTemp < 8 -> 15
            else -> 0
        }

        // ✅ Snow/Precipitation (50 points)
        val rainProb = day.precipitation_probability ?: 0
        val snowIndicator = if (maxTemp < 2) rainProb else 0
        score += when {
            snowIndicator >= 70 -> 50
            snowIndicator >= 40 -> 30
            snowIndicator >= 20 -> 15
            else -> 0
        }

        return score
    }

    private fun calculateSurfingScore(day: Daily): Int {
        // Ideal: 15-25 km/h wind, no rain, moderate temp
        var score = 0

        // ✅ Wind (50 points)
        val maxWind = day.wind_max
        score += when {
            maxWind in 15.0..25.0 -> 50
            maxWind in 10.0..30.0 -> 30
            maxWind in 5.0..35.0 -> 15
            else -> 0
        }

        // ✅ Rain (50 points)
        val rainProb = day.precipitation_probability ?: 0
        score += when {
            rainProb <= 10 -> 50
            rainProb <= 30 -> 30
            rainProb <= 50 -> 15
            else -> 0
        }

        return score
    }

    private fun formatDate(dateStr: String?): String {
        if (dateStr.isNullOrEmpty()) return "Unknown"
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
            val date = inputFormat.parse(dateStr)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateStr
        }
    }
}