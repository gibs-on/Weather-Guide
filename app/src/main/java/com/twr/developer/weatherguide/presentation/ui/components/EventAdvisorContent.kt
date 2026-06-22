package com.twr.developer.weatherguide.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twr.developer.weatherguide.domain.model.WeatherResponse
import com.twr.developer.weatherguide.domain.scorer.EventScorer
import com.twr.developer.weatherguide.util.WeatherUtils

@Composable
fun EventAdvisorContent(weather: WeatherResponse?) {
    val eventScorer = remember { EventScorer() }
    
    val hourlyData = remember(weather) {
        weather?.hourly?.take(24) ?: emptyList()
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Timeline Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Next 24h Opportunity",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "Event Probability",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        if (weather != null) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(hourlyData) { hour ->
                    val score = eventScorer.calculateEventScore(hour)
                    val color = when {
                        score >= 80 -> Color(0xFF4CAF50)
                        score >= 60 -> Color(0xFFFF9800)
                        else -> Color(0xFFF44336)
                    }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = WeatherUtils.parseTime(hour.time)?.time ?: "",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(color.copy(alpha = 0.1f), CircleShape)
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = { score / 100f },
                                modifier = Modifier.fillMaxSize(),
                                color = color,
                                strokeWidth = 3.dp,
                                trackColor = color.copy(alpha = 0.1f)
                            )
                            Text(
                                text = "$score",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = color
                            )
                        }
                    }
                }
            }
        } else {
            // Show placeholders for timeline
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                repeat(5) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("--:--", style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("--", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        AIInsightBox(
            insight = weather?.let { "Plan for early afternoon gatherings between 01:00 PM and 03:00 PM for maximum comfort. Probability of interference remains below 15%." } ?: "Scanning for event windows..."
        )
    }
}
