package com.twr.developer.weatherguide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.twr.developer.weatherguide.data.local.preferences.UserPreferencesRepo
import com.twr.developer.weatherguide.presentation.ui.MainScreen
import com.twr.developer.weatherguide.presentation.ui.onboarding.OnboardingScreen
import com.twr.developer.weatherguide.presentation.ui.onboarding.OnboardingViewModel
import com.twr.developer.weatherguide.ui.theme.WeatherGuideTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferencesRepo: UserPreferencesRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isOnboardingCompleted by userPreferencesRepo.isOnboardingCompleted.collectAsState(initial = null)

            WeatherGuideTheme {
                if (isOnboardingCompleted == null) {
                    // Show a splash or loading or nothing while waiting for datastore
                } else if (!isOnboardingCompleted!!) {
                    OnboardingScreen(
                        viewModel = hiltViewModel(
                            checkNotNull<ViewModelStoreOwner>(
                                LocalViewModelStoreOwner.current
                            ) {
                                "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
                            }, null
                        ),
                        onFinish = {
                            // OnboardingScreen handles updating the repo
                        }
                    )
                } else {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        MainScreen(
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}