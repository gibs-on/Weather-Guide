package com.twr.developer.weatherguide.domain.scorer

import com.twr.developer.weatherguide.domain.model.Daily
import com.twr.developer.weatherguide.domain.model.ForecastDay
import com.twr.developer.weatherguide.domain.model.WeatherResponse
import java.text.SimpleDateFormat
import java.util.*

class AgroScorer : WeatherScorer {

    override fun calculate(weather: WeatherResponse): ScoreResult {
        // ✅ Use your Daily class directly from weather.daily
        val dailyData = weather.daily ?: emptyList()
        if (dailyData.isEmpty()) {
            return ScoreResult(
                overall = 0,
                subScores = emptyMap(),
                insights = listOf("No forecast data available"),
                action = "Unable to provide farming recommendations"
            )
        }

        // Calculate scores for each day
        val dailyScores = dailyData.map { day ->
            val plantingScore = calculatePlantingScore(day)
            val sprayingScore = calculateSprayingScore(day)
            val harvestingScore = calculateHarvestingScore(day)

            DailyAgroScore(day.date, plantingScore, sprayingScore, harvestingScore)
        }

        // Find best days for each activity
        val bestPlantingDay = dailyScores.maxByOrNull { it.planting }
        val bestSprayingDay = dailyScores.maxByOrNull { it.spraying }
        val bestHarvestingDay = dailyScores.maxByOrNull { it.harvesting }

        // Calculate overall score (average of all three activities)
        val overallAvg = if (dailyScores.isNotEmpty()) {
            dailyScores.map { (it.planting + it.spraying + it.harvesting) / 3 }.average().toInt()
        } else 0

        // Generate insights
        val insights = mutableListOf<String>()
        insights.add("🌱 Planting: ${bestPlantingDay?.planting ?: 0}% (${formatDate(bestPlantingDay?.date)})")
        insights.add("🚜 Spraying: ${bestSprayingDay?.spraying ?: 0}% (${formatDate(bestSprayingDay?.date)})")
        insights.add("🌾 Harvesting: ${bestHarvestingDay?.harvesting ?: 0}% (${formatDate(bestHarvestingDay?.date)})")

        // Determine action
        val action = when {
            overallAvg >= 80 -> "✅ Great conditions! Perfect time for farming activities."
            overallAvg >= 60 -> "👍 Good conditions. Plan your tasks carefully."
            overallAvg >= 40 -> "⚠️ Moderate conditions. Consider waiting for better weather."
            else -> "❌ Poor conditions. Best to stay indoors today."
        }

        return ScoreResult(
            overall = overallAvg,
            subScores = mapOf(
                "Planting" to (bestPlantingDay?.planting ?: 0),
                "Spraying" to (bestSprayingDay?.spraying ?: 0),
                "Harvesting" to (bestHarvestingDay?.harvesting ?: 0)
            ),
            insights = insights,
            action = action,
            bestDay = bestPlantingDay?.date
        )
    }

    private fun calculatePlantingScore(day: Daily): Int {
        // Ideal conditions: 15-22°C, < 10% rain, < 15 km/h wind
        var score = 0

        // ✅ Temperature (40 points max) - use temp_max
        val maxTemp = day.temp_max
        score += when {
            maxTemp in 15.0..22.0 -> 40
            maxTemp in 12.0..28.0 -> 20
            else -> 5
        }

        // ✅ Precipitation (40 points max)
        val rainProb = day.precipitation_probability ?: 0
        score += when {
            rainProb <= 10 -> 40
            rainProb <= 30 -> 25
            rainProb <= 50 -> 10
            else -> 0
        }

        // ✅ Wind (20 points max)
        val maxWind = day.wind_max
        score += when {
            maxWind < 15 -> 20
            maxWind < 25 -> 10
            else -> 0
        }

        return score
    }

    private fun calculateSprayingScore(day: Daily): Int {
        // Ideal conditions: < 15 km/h wind, < 20% rain, moderate temp
        var score = 0

        // ✅ Wind (50 points max)
        val maxWind = day.wind_max
        score += when {
            maxWind < 10 -> 50
            maxWind < 15 -> 35
            maxWind < 25 -> 15
            else -> 0
        }

        // ✅ Rain (50 points max)
        val rainProb = day.precipitation_probability ?: 0
        score += when {
            rainProb <= 10 -> 50
            rainProb <= 30 -> 35
            rainProb <= 50 -> 15
            else -> 0
        }

        return score
    }

    private fun calculateHarvestingScore(day: Daily): Int {
        // Ideal conditions: 3+ dry days, < 70% humidity, moderate temp
        var score = 0

        // ✅ Dryness (60 points max)
        val rainProb = day.precipitation_probability ?: 0
        score += when {
            rainProb <= 10 -> 60
            rainProb <= 30 -> 40
            rainProb <= 50 -> 20
            else -> 0
        }

        // ✅ Humidity (40 points max) - using precipitation_sum as proxy
        val precip = day.precipitation_sum
        score += when {
            precip < 0.5 -> 40
            precip < 1.0 -> 25
            precip < 2.0 -> 10
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

    // ✅ Data class for internal use
    private data class DailyAgroScore(
        val date: String,
        val planting: Int,
        val spraying: Int,
        val harvesting: Int
    )
}