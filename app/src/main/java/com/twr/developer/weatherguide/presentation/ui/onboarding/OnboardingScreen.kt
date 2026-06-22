package com.twr.developer.weatherguide.presentation.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onFinish: () -> Unit
) {
    val pagerState = rememberPagerState { 5 }
    val scope = rememberCoroutineScope()
    var selectedLocation by remember { mutableStateOf<String?>(null) }

    val pages = listOf(
        OnboardingPageData(
            title = "Welcome to WeatherGuide",
            description = "Your personal AI-powered weather companion designed to help you plan your life around the sky.",
            icon = Icons.Default.CloudQueue,
            color = MaterialTheme.colorScheme.primary,
            details = listOf(
                "Hyper-local precision",
                "Personalized AI insights",
                "Advanced planning tools"
            )
        ),
        OnboardingPageData(
            title = "Precision Weather",
            description = "Go beyond simple forecasts with hyper-local data powered by advanced AI models.",
            icon = Icons.Default.WbSunny,
            color = Color(0xFFFFA000),
            details = listOf(
                "Minute-by-minute rain alerts",
                "High-resolution temperature mapping",
                "Severe weather warnings"
            )
        ),
        OnboardingPageData(
            title = "AI Agronomic Advisor",
            description = "Maximize your yields with intelligence tailored to your soil and crops.",
            icon = Icons.Default.Agriculture,
            color = Color(0xFF4CAF50),
            details = listOf(
                "Optimal planting windows",
                "Pest & disease risk analysis",
                "Harvest timing optimization"
            )
        ),
        OnboardingPageData(
            title = "Smart Life Planner",
            description = "Perfect timing for everything from trail runs to outdoor weddings.",
            icon = Icons.Default.EventAvailable,
            color = Color(0xFF2196F3),
            details = listOf(
                "Hourly event suitability scores",
                "Adventure scouting for athletes",
                "Energy consumption strategies"
            )
        ),
        OnboardingPageData(
            title = "Set Your Foundation",
            description = "Choose your primary location in Kenya to begin your personalized journey.",
            icon = Icons.Default.LocationOn,
            color = MaterialTheme.colorScheme.secondary,
            details = emptyList()
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                userScrollEnabled = true
            ) { pageIndex ->
                if (pageIndex < 4) {
                    OnboardingContentPage(pages[pageIndex])
                } else {
                    LocationSelectionPage(
                        locations = viewModel.popularLocations,
                        selectedLocation = selectedLocation,
                        onLocationSelected = { selectedLocation = it }
                    )
                }
            }

            // Bottom Navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Page Indicator
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(pages.size) { iteration ->
                        val color = if (pagerState.currentPage == iteration) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                        val width = if (pagerState.currentPage == iteration) 24.dp else 8.dp
                        
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(width)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }

                // Action Button
                Button(
                    onClick = {
                        if (pagerState.currentPage < 4) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            selectedLocation?.let {
                                viewModel.completeOnboarding(it)
                                onFinish()
                            }
                        }
                    },
                    enabled = (pagerState.currentPage < 4 || selectedLocation != null),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        if (pagerState.currentPage < 4) "Next" else "Get Started",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun OnboardingContentPage(data: OnboardingPageData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(160.dp),
            shape = CircleShape,
            color = data.color.copy(alpha = 0.1f)
        ) {
            Icon(
                imageVector = data.icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxSize(),
                tint = data.color
            )
        }
        
        Spacer(Modifier.height(48.dp))
        
        Text(
            text = data.title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 40.sp
        )
        
        Spacer(Modifier.height(16.dp))
        
        Text(
            text = data.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 24.sp
        )

        if (data.details.isNotEmpty()) {
            Spacer(Modifier.height(32.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                data.details.forEach { detail ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = data.color.copy(alpha = 0.2f),
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.padding(4.dp),
                                tint = data.color
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = detail,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LocationSelectionPage(
    locations: List<String>,
    selectedLocation: String?,
    onLocationSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp, start = 24.dp, end = 24.dp)
    ) {
        Text(
            text = "Your Home Base",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Text(
            text = "Select your primary location in Kenya to start receiving personalized AI insights.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(Modifier.height(32.dp))
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(locations) { location ->
                val isSelected = selectedLocation == location
                Surface(
                    onClick = { onLocationSelected(location) },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                    tonalElevation = if (isSelected) 0.dp else 2.dp,
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.padding(10.dp),
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Text(
                            text = location,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.weight(1f))
                        if (isSelected) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

data class OnboardingPageData(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val details: List<String> = emptyList()
)
