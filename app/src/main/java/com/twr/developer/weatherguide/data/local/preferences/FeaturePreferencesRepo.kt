package com.twr.developer.weatherguide.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.twr.developer.weatherguide.domain.model.PremiumFeature
import com.twr.developer.weatherguide.domain.model.UnlockedFeatures
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeaturePreferencesRepo @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val HARVEST_KEY = booleanPreferencesKey("harvest_unlocked")
        private val ADVENTURE_KEY = booleanPreferencesKey("adventure_unlocked")
        private val EVENT_KEY = booleanPreferencesKey("event_unlocked")      // NEW
        private val AI_KEY = booleanPreferencesKey("ai_unlocked")
    }

    val unlockedFeatures: Flow<UnlockedFeatures> = dataStore.data.map { prefs ->
        UnlockedFeatures(
            harvestAdvisor = prefs[HARVEST_KEY] ?: false,
            adventureScout = prefs[ADVENTURE_KEY] ?: false,
            eventAdvisor = prefs[EVENT_KEY] ?: false,      // NEW
            aiStrategy = prefs[AI_KEY] ?: false
        )
    }

    suspend fun unlockFeature(feature: PremiumFeature) {
        dataStore.edit { prefs ->
            when (feature) {
                PremiumFeature.HARVEST_ADVISOR -> prefs[HARVEST_KEY] = true
                PremiumFeature.ADVENTURE_SCOUT -> prefs[ADVENTURE_KEY] = true
                PremiumFeature.EVENT_ADVISOR -> prefs[EVENT_KEY] = true      // NEW
                PremiumFeature.AI_STRATEGY -> prefs[AI_KEY] = true
            }
        }
    }

    suspend fun unlockAllFeatures() {
        dataStore.edit { prefs ->
            prefs[HARVEST_KEY] = true
            prefs[ADVENTURE_KEY] = true
            prefs[EVENT_KEY] = true      // NEW
            prefs[AI_KEY] = true
        }
    }

    suspend fun lockAllFeatures() {
        dataStore.edit { prefs ->
            prefs[HARVEST_KEY] = false
            prefs[ADVENTURE_KEY] = false
            prefs[EVENT_KEY] = false
            prefs[AI_KEY] = false
        }
    }
}