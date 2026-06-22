package com.twr.developer.weatherguide.domain.scorer

import com.twr.developer.weatherguide.domain.model.WeatherResponse

/**
 * Interface for all weather scoring engines.
 * Each implementation calculates a score (0-100) for a specific use case.
 */
interface WeatherScorer {
    fun calculate(weather: WeatherResponse): ScoreResult
}

/**
 * Result of a scoring calculation
 */
data class ScoreResult(
    val overall: Int,                          // 0-100
    val subScores: Map<String, Int>,          // Named sub-scores (e.g., "Planting" -> 85)
    val insights: List<String>,               // Human-readable insights
    val action: String,                       // Actionable recommendation
    val bestDay: String? = null               // Optional best day recommendation
)