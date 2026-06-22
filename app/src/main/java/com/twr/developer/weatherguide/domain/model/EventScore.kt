package com.twr.developer.weatherguide.domain.model

data class EventScore(
    val total: Int,
    val temperatureScore: Int,
    val rainScore: Int,
    val windScore: Int,
    val recommendation: String
)

data class EventRecommendation(
    val score: EventScore,
    val aiSummary: String,
    val tips: List<String>
)