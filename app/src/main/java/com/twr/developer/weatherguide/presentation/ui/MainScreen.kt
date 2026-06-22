package com.twr.developer.weatherguide.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twr.developer.weatherguide.domain.model.*
import com.twr.developer.weatherguide.presentation.ui.components.PremiumFeaturesSection
import com.twr.developer.weatherguide.presentation.ui.components.WeatherSummaryCard
import com.twr.developer.weatherguide.ui.theme.WeatherGuideTheme

import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.twr.developer.weatherguide.presentation.ui.components.PaywallDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(
        checkNotNull(
            LocalViewModelStoreOwner.current
        ) {
                "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
            }, null
    )
) {
    val weatherState by viewModel.weatherState.collectAsState()
    val unlockedFeatures by viewModel.unlockedFeatures.collectAsState()
    val selectedLocations by viewModel.selectedLocations.collectAsState()
    val activeLocation by viewModel.activeLocation.collectAsState()
    
    var showPaywall by remember { mutableStateOf(false) }
    var pendingFeature by remember { mutableStateOf<PremiumFeature?>(null) }
    var showSettings by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "WeatherGuide", 
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                actions = {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        MainScreenContent(
            weatherState = weatherState,
            unlockedFeatures = unlockedFeatures,
            selectedLocations = selectedLocations,
            activeLocation = activeLocation,
            onLocationSelected = { viewModel.switchLocation(it) },
            onAddLocation = { viewModel.addNewLocation(it) },
            onRemoveLocation = { viewModel.removeLocation(it) },
            onUnlockFeature = { 
                pendingFeature = it
                showPaywall = true 
            },
            onUnlockAll = { 
                pendingFeature = null
                showPaywall = true 
            },
            modifier = Modifier.padding(innerPadding)
        )
    }

    if (showPaywall) {
        PaywallDialog(
            onDismiss = { showPaywall = false },
            onConfirm = {
                if (pendingFeature != null) {
                    viewModel.unlockFeature(pendingFeature!!)
                } else {
                    viewModel.unlockAllFeatures()
                }
                showPaywall = false
            },
            featureTitle = pendingFeature?.let { 
                when(it) {
                    PremiumFeature.HARVEST_ADVISOR -> "Harvest Advisor"
                    PremiumFeature.ADVENTURE_SCOUT -> "Adventure Scout"
                    PremiumFeature.EVENT_ADVISOR -> "Event Advisor"
                    PremiumFeature.AI_STRATEGY -> "AI Insights"
                }
            } ?: "Premium Plus"
        )
    }

    if (showSettings) {
        SettingsDialog(
            onDismiss = { showSettings = false },
            onResetData = {
                viewModel.resetAllData()
                showSettings = false
            },
            onUnsubscribe = {
                viewModel.unsubscribe()
                showSettings = false
            }
        )
    }
}

@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    onResetData: () -> Unit,
    onUnsubscribe: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Settings & Privacy") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Manage your subscription and local data preferences.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                
                ListItem(
                    headlineContent = { Text("Unsubscribe") },
                    supportingContent = { Text("Cancel your premium plus plan") },
                    leadingContent = { Icon(Icons.Default.Logout, contentDescription = null) },
                    modifier = Modifier.clickable { onUnsubscribe() }
                )
                
                ListItem(
                    headlineContent = { Text("Reset Application", color = Color.Red) },
                    supportingContent = { Text("Wipe all data and restart onboarding") },
                    leadingContent = { Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = Color.Red) },
                    modifier = Modifier.clickable { onResetData() }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(
    weatherState: WeatherState,
    unlockedFeatures: UnlockedFeatures,
    selectedLocations: Set<String>,
    activeLocation: Location?,
    onLocationSelected: (String) -> Unit,
    onAddLocation: (String) -> Unit,
    onRemoveLocation: (String) -> Unit,
    onUnlockFeature: (PremiumFeature) -> Unit,
    onUnlockAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSearchDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Location Selector Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // "Add New" button
            IconButton(
                onClick = { showSearchDialog = true },
                modifier = Modifier.padding(end = 4.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Location")
            }

            // List of selected locations as chips
            selectedLocations.forEach { location ->
                val isActive = activeLocation?.name == location || activeLocation?.displayName == location 
                FilterChip(
                    selected = isActive,
                    onClick = { onLocationSelected(location) },
                    label = { Text(location) },
                    leadingIcon = if (isActive) {
                        { Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null,
                    trailingIcon = if (selectedLocations.size > 1) {
                        { 
                            Icon(
                                Icons.Default.Close, 
                                contentDescription = "Remove",
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { onRemoveLocation(location) }
                            ) 
                        }
                    } else null,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = weatherState) {
            is WeatherState.Loading -> {
                // Show skeleton with dashes while loading
                WeatherSummaryCard(
                    weather = null,
                    activeLocation = activeLocation,
                    isAiUnlocked = unlockedFeatures.aiStrategy,
                    onUnlockAi = { onUnlockFeature(PremiumFeature.AI_STRATEGY) },
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().height(2.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                PremiumFeaturesSection(
                    weather = null,
                    unlockedFeatures = unlockedFeatures,
                    onUnlockFeature = onUnlockFeature,
                    onUnlockAll = onUnlockAll
                )
            }
            is WeatherState.Error -> ErrorState(state.message)
            is WeatherState.Success -> {
                WeatherSummaryCard(
                    weather = state.weather,
                    activeLocation = activeLocation,
                    isAiUnlocked = unlockedFeatures.aiStrategy,
                    onUnlockAi = { onUnlockFeature(PremiumFeature.AI_STRATEGY) }
                )
                Spacer(modifier = Modifier.height(24.dp))
                PremiumFeaturesSection(
                    weather = state.weather,
                    unlockedFeatures = unlockedFeatures,
                    onUnlockFeature = onUnlockFeature,
                    onUnlockAll = onUnlockAll
                )
            }
            is WeatherState.Empty -> EmptyState()
        }
    }

    if (showSearchDialog) {
        LocationSearchDialog(
            onDismiss = { showSearchDialog = false },
            onConfirm = { 
                onAddLocation(it)
                showSearchDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSearchDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    val kenyaLocations = remember {
        PopularLocations.locations
    }
    
    val suggestions = remember(text) {
        if (text.length >= 2) {
            kenyaLocations.filter { it.displayName.contains(text, ignoreCase = true) }
        } else {
            emptyList()
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Location", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("Search Kenya cities...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = if (text.isNotEmpty()) {
                        { 
                            IconButton(onClick = { text = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = null)
                            }
                        }
                    } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                
                if (suggestions.isNotEmpty()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp),
                        shape = RoundedCornerShape(12.dp),
                        tonalElevation = 2.dp,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        LazyColumn {
                            items(suggestions) { suggestion ->
                                ListItem(
                                    headlineContent = { Text(suggestion.displayName) },
                                    leadingContent = { Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp)) },
                                    modifier = Modifier.clickable { 
                                        text = suggestion.displayName
                                        onConfirm(suggestion.name)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(text) },
                enabled = text.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun LoadingSpinner() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorState(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Error: $message", color = Color.Red)
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Select a location to see weather info.")
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val mockWeather = WeatherResponse(
        location = Location(
            name = "London",
            region = "Greater London",
            country = "United Kingdom",
            lat = 51.51,
            lon = -0.13,
        ),
        current = Current(
            time = "2026-06-22T03:45",
            temperature = 15.0,
            wind_speed = 11.0,
            wind_direction = 250, // Degrees (WSW ≈ 250°)
            condition_code = "2", // Partly cloudy
            icon = "https://cdn.weather-ai.co/icons/default/2_partly_cloudy_day.svg",
            icon_path = "icons/weather/png/wmo-2-day-128.png",
            humidity = 65,
            feels_like = 14.0,
            wind_gust = 16.0,
            uv_index = 0.0
        ),
        hourly = emptyList(),
        daily = emptyList(),
    )

    val mockUnlockedFeatures = UnlockedFeatures(
        harvestAdvisor = true,
        adventureScout = false,
        eventAdvisor = false,
        aiStrategy = false
    )

    WeatherGuideTheme {
        MainScreenContent(
            weatherState = WeatherState.Success(mockWeather),
            unlockedFeatures = mockUnlockedFeatures,
            selectedLocations = setOf("London", "Nairobi"),
            activeLocation = Location(
                name = "London",
                country = "UK",
                lat = 0.0,
                lon = 0.0,
                region = "",
                timezone = ""
            ),
            onLocationSelected = {},
            onAddLocation = {},
            onRemoveLocation = {},
            onUnlockFeature = {},
            onUnlockAll = {}
        )
    }
}
