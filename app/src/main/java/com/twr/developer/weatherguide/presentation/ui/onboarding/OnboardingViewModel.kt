package com.twr.developer.weatherguide.presentation.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twr.developer.weatherguide.data.local.preferences.UserPreferencesRepo
import com.twr.developer.weatherguide.domain.model.PopularLocations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferencesRepo: UserPreferencesRepo
) : ViewModel() {

    val popularLocations = PopularLocations.locations.map { it.displayName }

    fun completeOnboarding(selectedLocation: String) {
        viewModelScope.launch {
            userPreferencesRepo.addLocation(selectedLocation)
            userPreferencesRepo.setActiveLocation(selectedLocation)
            userPreferencesRepo.setOnboardingCompleted(true)
        }
    }
}
