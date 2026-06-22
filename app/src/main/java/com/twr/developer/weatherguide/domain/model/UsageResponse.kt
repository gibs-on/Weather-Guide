package com.twr.developer.weatherguide.domain.model

data class UsageResponse(
    val plan: String,           // "free", "pro", "scale"
    val requests_used: Int,
    val requests_limit: Int,
    val ai_requests_used: Int,
    val ai_requests_limit: Int,
    val remaining: Int,
    val resets_at: String       // ISO timestamp
)