package com.twr.developer.weatherguide.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.twr.developer.weatherguide.domain.model.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepo @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val SELECTED_LOCATIONS = stringSetPreferencesKey("selected_locations")
        private val CURRENT_LOCATION = stringSetPreferencesKey("current_location") // Storing as a set to keep it simple with stringSet or just use string
        private val ACTIVE_LOCATION = stringPreferencesKey("active_location")
    }

    val isOnboardingCompleted: Flow<Boolean> = dataStore.data.map { it[ONBOARDING_COMPLETED] ?: false }
    val selectedLocations: Flow<Set<String>> = dataStore.data.map { it[SELECTED_LOCATIONS] ?: emptySet() }
    val activeLocation: Flow<String?> = dataStore.data.map { it[ACTIVE_LOCATION] }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { it[ONBOARDING_COMPLETED] = completed }
    }

    suspend fun addLocation(location: String) {
        dataStore.edit { prefs ->
            val current = prefs[SELECTED_LOCATIONS] ?: emptySet()
            prefs[SELECTED_LOCATIONS] = current + location
            // If it's the first location, make it active
            if (prefs[ACTIVE_LOCATION] == null) {
                prefs[ACTIVE_LOCATION] = location
            }
        }
    }

    suspend fun setActiveLocation(location: String) {
        dataStore.edit { it[ACTIVE_LOCATION] = location }
    }

    suspend fun removeLocation(location: String) {
        dataStore.edit { prefs ->
            val current = prefs[SELECTED_LOCATIONS] ?: emptySet()
            val updated = current - location
            prefs[SELECTED_LOCATIONS] = updated
            if (prefs[ACTIVE_LOCATION] == location) {
                val nextActive = updated.firstOrNull()
                if (nextActive != null) {
                    prefs[ACTIVE_LOCATION] = nextActive
                } else {
                    prefs.remove(ACTIVE_LOCATION)
                }
            }
        }
    }
}
