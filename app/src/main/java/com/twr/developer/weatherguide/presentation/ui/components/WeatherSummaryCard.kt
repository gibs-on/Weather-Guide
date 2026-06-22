package com.twr.developer.weatherguide.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.twr.developer.weatherguide.domain.model.Location
import com.twr.developer.weatherguide.domain.model.WeatherConditionMapper
import com.twr.developer.weatherguide.domain.model.WeatherResponse

@Composable
fun WeatherSummaryCard(
    weather: WeatherResponse?,
    isAiUnlocked: Boolean = false,
    onUnlockAi: () -> Unit = {},
    activeLocation: Location?
) {

    val conditionText = weather?.current?.let { WeatherConditionMapper.getConditionText(it.condition_code) }
    val conditionEmoji = weather?.current?.let { WeatherConditionMapper.getConditionEmoji(it.condition_code) }
    val conditionColor = weather?.current?.let { WeatherConditionMapper.getConditionColor(it.condition_code) }
    val precip = weather?.hourly?.firstOrNull()?.getPrecipitation()
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = activeLocation?.name ?: "--",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = activeLocation?.country ?: "--",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.size(64.dp)
                ) {
                    if (weather != null) {
                        AsyncImage(
                            model = weather.current.icon,
                            contentDescription = null,
                            modifier = Modifier.padding(12.dp)
                        )
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = conditionEmoji ?: "--°",
                                style = MaterialTheme.typography.displayLarge,
                                fontWeight = FontWeight.Black,
                                fontSize = 72.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = weather?.current?.temperature?.let { "${it.toInt()}°" } ?: "--°",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Black,
                    fontSize = 72.sp
                )
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = conditionText ?: "--",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = weather?.current?.feels_like?.let { "Feels like ${it}°" } ?: "Feels like --°",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherDetailItem(Icons.Default.Air, weather?.current?.wind_speed?.let { "$it km/h" } ?: "-- km/h", "Wind")
                WeatherDetailItem(Icons.Default.WaterDrop, weather?.current?.humidity?.let { "$it%" } ?: "--%", "Humidity")
                WeatherDetailItem(Icons.Default.Thermostat, weather?.current?.uv_index?.let { "${it}mm" } ?: "--mm", "Precip")
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // AI Interpretation Section
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { if (!isAiUnlocked) onUnlockAi() },
                color = if (isAiUnlocked) 
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) 
                else 
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Weather Interpretation",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (!isAiUnlocked) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = "PRO",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = if (weather == null) {
                            "Analyzing atmospheric conditions..."
                        } else if (isAiUnlocked) {
                            "The ${conditionText?.lowercase()} conditions today are ideal for most outdoor activities. With humidity at ${weather.current.humidity}%, it feels comfortable, though the ${weather.current.wind_speed} km/h wind might be noticeable."
                        } else {
                            "Unlock AI-powered breakdown of how today's weather affects your specific plans and health."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}


@Composable
fun WeatherDetailItem(icon: ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            icon, 
            contentDescription = null, 
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
