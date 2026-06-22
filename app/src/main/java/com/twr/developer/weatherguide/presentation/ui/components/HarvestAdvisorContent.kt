package com.twr.developer.weatherguide.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twr.developer.weatherguide.domain.model.WeatherResponse

@Composable
fun HarvestAdvisorContent(weather: WeatherResponse?) {
    Column(modifier = Modifier.fillMaxWidth()) {
        PremiumMetricTable(
            metrics = listOf(
                MetricData("Planting", weather?.let { "85%" } ?: "--", MaterialTheme.colorScheme.primary),
                MetricData("Spraying", weather?.let { "92%" } ?: "--", Color(0xFF4CAF50)),
                MetricData("Harvest", weather?.let { "64%" } ?: "--", Color(0xFFFF9800))
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        AIInsightBox(
            insight = weather?.let { "Optimal window for maize spraying opens at 09:00 AM. Avoid irrigation after 04:00 PM as humidity peaks, increasing fungal risk." } ?: "Analyzing harvest conditions..."
        )
    }
}

@Composable
fun AdventureScoutContent(weather: WeatherResponse?) {
    Column(modifier = Modifier.fillMaxWidth()) {
        PremiumMetricTable(
            metrics = listOf(
                MetricData("Hiking", weather?.let { "High" } ?: "--", MaterialTheme.colorScheme.secondary),
                MetricData("Cycling", weather?.let { "Ideal" } ?: "--", Color(0xFF00BCD4)),
                MetricData("Camping", weather?.let { "Good" } ?: "--", Color(0xFF8BC34A))
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        AIInsightBox(
            insight = weather?.let { "High visibility and moderate winds make the morning perfect for trail exploration. Pack light waterproofs for a potential late afternoon drizzle." } ?: "Calculating adventure safety..."
        )
    }
}

@Composable
fun AIStrategyContent(weather: WeatherResponse?) {
    Column(modifier = Modifier.fillMaxWidth()) {
        AIInsightBox(
            insight = weather?.let { "Current atmospheric pressure suggests stable conditions for the next 48 hours. Energy consumption likely to peak around 02:00 PM due to temperature rise." } ?: "Processing strategic patterns...",
            title = "Executive Strategy"
        )
    }
}

@Composable
fun PremiumMetricTable(metrics: List<MetricData>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            metrics.forEachIndexed { index, metric ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = metric.label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = metric.color.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = metric.value,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = metric.color
                        )
                    }
                }
                if (index < metrics.size - 1) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
fun AIInsightBox(insight: String, title: String = "AI Recommendation") {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = insight,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp
        )
    }
}

data class MetricData(val label: String, val value: String, val color: Color)
