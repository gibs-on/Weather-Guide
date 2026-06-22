package com.twr.developer.weatherguide.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twr.developer.weatherguide.data.local.preferences.FeaturePreferencesRepo
import com.twr.developer.weatherguide.data.local.preferences.UserPreferencesRepo
import com.twr.developer.weatherguide.domain.model.InsightsResponse
import com.twr.developer.weatherguide.domain.model.Location
import com.twr.developer.weatherguide.domain.model.PopularLocations
import com.twr.developer.weatherguide.domain.model.PremiumFeature
import com.twr.developer.weatherguide.domain.model.UnlockedFeatures
import com.twr.developer.weatherguide.domain.model.WeatherResponse
import com.twr.developer.weatherguide.domain.repository.WeatherRepository
import com.twr.developer.weatherguide.domain.scorer.AdventureScorer
import com.twr.developer.weatherguide.domain.scorer.AgroScorer
import com.twr.developer.weatherguide.domain.scorer.EventScorer
import com.twr.developer.weatherguide.domain.scorer.ScoreResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val featureRepo: FeaturePreferencesRepo,
    private val userPreferencesRepo: UserPreferencesRepo
) : ViewModel() {

    // Weather State
    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Empty)
    val weatherState: StateFlow<WeatherState> = _weatherState.asStateFlow()

    // Unlocked Features State
    private val _unlockedFeatures = MutableStateFlow(UnlockedFeatures())
    val unlockedFeatures: StateFlow<UnlockedFeatures> = _unlockedFeatures.asStateFlow()

    // Locations State
    val selectedLocations: StateFlow<Set<String>> = userPreferencesRepo.selectedLocations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val activeLocation: StateFlow<Location?> = userPreferencesRepo.activeLocation
        .map { locationName ->
            locationName?.let {
                PopularLocations.locations.find { location ->
                    location.name.equals(it, ignoreCase = true) ||
                            location.displayName.equals(it, ignoreCase = true)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Scored Results State
    private val _scoredState = MutableStateFlow(ScoredState())
    val scoredState: StateFlow<ScoredState> = _scoredState.asStateFlow()

    // Loading State
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Scorers
    private val agroScorer = AgroScorer()
    private val adventureScorer = AdventureScorer()
    private val eventScorer = EventScorer()

    init {
        // Collect unlocked features
        viewModelScope.launch {
            featureRepo.unlockedFeatures.collect { features ->
                _unlockedFeatures.value = features
                val currentWeather = (_weatherState.value as? WeatherState.Success)?.weather
                currentWeather?.let { calculateScores(it, features) }
            }
        }

        // Collect active location and fetch weather
        viewModelScope.launch {
            userPreferencesRepo.activeLocation.collect { location ->
                location?.let {
                    PopularLocations.locations.find {
                        it.name == location || it.displayName == location
                    }?.let { foundLocation ->
                        searchWeather(foundLocation)
                    }
                }
            }
        }
    }

    fun switchLocation(location: String) {
        viewModelScope.launch {
            userPreferencesRepo.setActiveLocation(location)
        }
    }

    fun addNewLocation(location: String) {
        if (location.isBlank()) return
        viewModelScope.launch {
            userPreferencesRepo.addLocation(location)
            userPreferencesRepo.setActiveLocation(location)
        }
    }

    fun removeLocation(location: String) {
        viewModelScope.launch {
            userPreferencesRepo.removeLocation(location)
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            featureRepo.lockAllFeatures()
            userPreferencesRepo.setOnboardingCompleted(false)
            // Note: In a real app, you might also clear locations or other data
        }
    }

    fun unsubscribe() {
        viewModelScope.launch {
            featureRepo.lockAllFeatures()
        }
    }

    fun searchWeather(location: Location) {
        if (location.name.isBlank()) return

        android.util.Log.i("MainViewModel", "searchWeather called for: $location")
        viewModelScope.launch {
            _isLoading.value = true
            _weatherState.value = WeatherState.Loading

            try {
                val weather = repository.getWeather(location)
                android.util.Log.i("MainViewModel", "Received weather for ${weather.location.name}: ${weather.current.temperature}C")
                _weatherState.value = WeatherState.Success(weather)
                calculateScores(weather, _unlockedFeatures.value)
                
                // If AI is unlocked, refresh insights for the new location
                if (_unlockedFeatures.value.aiStrategy) {
                    fetchAIInsights()
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Error fetching weather: ${e.message}", e)
                _weatherState.value = WeatherState.Error(e.message ?: "Unknown error occurred")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Unlock a specific premium feature
     */
    fun unlockFeature(feature: PremiumFeature) {
        viewModelScope.launch {
            featureRepo.unlockFeature(feature)

            // After unlocking, refresh premium data if needed
            val currentWeather = (_weatherState.value as? WeatherState.Success)?.weather
            if (currentWeather != null) {
                when (feature) {
                    PremiumFeature.HARVEST_ADVISOR,
                    PremiumFeature.ADVENTURE_SCOUT,
                    PremiumFeature.EVENT_ADVISOR -> {
                        // Recalculate scores with the new feature unlocked
                        calculateScores(currentWeather, _unlockedFeatures.value)
                    }
                    PremiumFeature.AI_STRATEGY -> {
                        // Fetch AI insights from premium endpoint
                        fetchAIInsights()
                    }
                }
            }
        }
    }

    /**
     * Unlock all premium features at once
     */
    fun unlockAllFeatures() {
        viewModelScope.launch {
            featureRepo.unlockAllFeatures()
            // Refresh all premium data
            val currentWeather = (_weatherState.value as? WeatherState.Success)?.weather
            if (currentWeather != null) {
                calculateScores(currentWeather, _unlockedFeatures.value)
                fetchAIInsights()
            }
        }
    }

    /**
     * Calculate scores using the three scorers
     */
    private fun calculateScores(weather: WeatherResponse, unlocked: UnlockedFeatures) {
        val agroResult = if (unlocked.harvestAdvisor) {
            agroScorer.calculate(weather)
        } else null

        val adventureResult = if (unlocked.adventureScout) {
            adventureScorer.calculate(weather)
        } else null

        val eventResult = if (unlocked.eventAdvisor) {
            eventScorer.calculate(weather)
        } else null

        _scoredState.value = ScoredState(
            agro = agroResult,
            adventure = adventureResult,
            event = eventResult
        )
    }

    /**
     * Fetch AI Insights from the premium endpoint
     */
    private fun fetchAIInsights() {
        viewModelScope.launch {
            val currentWeather = (_weatherState.value as? WeatherState.Success)?.weather
            if (currentWeather != null) {
                try {
                    // This will return mock data if USE_MOCK = true
                    val insights = repository.getAIInsights(
                        currentWeather.location
                    )
                    _scoredState.value = _scoredState.value.copy(aiInsights = insights)
                } catch (e: Exception) {
                    // Handle error gracefully
                }
            }
        }
    }

    /**
     * Fetch premium 14-day forecast
     */
    fun fetchPremiumForecast() {
        viewModelScope.launch {
            val currentWeather = (_weatherState.value as? WeatherState.Success)?.weather
            if (currentWeather != null) {
                try {
                    val premiumForecast = repository.getPremiumForecast(
                        currentWeather.location
                    )
                    _weatherState.value = WeatherState.Success(premiumForecast)
                } catch (e: Exception) {
                    // Handle error gracefully
                }
            }
        }
    }
}

// --- State Classes ---

sealed class WeatherState {
    object Empty : WeatherState()
    object Loading : WeatherState()
    data class Success(val weather: WeatherResponse) : WeatherState()
    data class Error(val message: String) : WeatherState()
}

data class ScoredState(
    val agro: ScoreResult? = null,
    val adventure: ScoreResult? = null,
    val event: ScoreResult? = null,
    val aiInsights: InsightsResponse? = null
)