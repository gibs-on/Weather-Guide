package com.twr.developer.weatherguide.domain.model

enum class PremiumFeature {
    HARVEST_ADVISOR,
    ADVENTURE_SCOUT,
    EVENT_ADVISOR,
    AI_STRATEGY
}

data class UnlockedFeatures(
    val harvestAdvisor: Boolean = false,
    val adventureScout: Boolean = false,
    val eventAdvisor: Boolean = false,
    val aiStrategy: Boolean = false
) {
    fun isUnlocked(feature: PremiumFeature): Boolean {
        return when (feature) {
            PremiumFeature.HARVEST_ADVISOR -> harvestAdvisor
            PremiumFeature.ADVENTURE_SCOUT -> adventureScout
            PremiumFeature.EVENT_ADVISOR -> eventAdvisor
            PremiumFeature.AI_STRATEGY -> aiStrategy
        }
    }
}