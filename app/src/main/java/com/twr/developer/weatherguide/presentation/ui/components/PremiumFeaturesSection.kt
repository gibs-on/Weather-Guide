package com.twr.developer.weatherguide.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.twr.developer.weatherguide.domain.model.PremiumFeature
import com.twr.developer.weatherguide.domain.model.UnlockedFeatures
import com.twr.developer.weatherguide.domain.model.WeatherResponse

@Composable
fun PremiumFeaturesSection(
    weather: WeatherResponse?,
    unlockedFeatures: UnlockedFeatures,
    onUnlockFeature: (PremiumFeature) -> Unit,
    onUnlockAll: () -> Unit
) {
    Column {
        // Section Header with "Unlock All" button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🌟 Premium Features",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            TextButton(onClick = onUnlockAll) {
                Text("Unlock All")
            }
        }

        // Feature 1: Harvest Advisor
        PremiumFeatureCard(
            title = "🌱 Harvest Advisor",
            description = "Get expert guidance on the best times for planting, spraying, and harvesting based on hyper-local moisture and wind conditions.",
            isUnlocked = unlockedFeatures.harvestAdvisor,
            onUnlock = { onUnlockFeature(PremiumFeature.HARVEST_ADVISOR) }
        ) {
            HarvestAdvisorContent(weather)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Feature 2: Adventure Scout
        PremiumFeatureCard(
            title = "⛷️ Adventure Scout",
            description = "Plan your outdoor escapes with confidence. Receive tailored suitability scores for hiking, cycling, and camping.",
            isUnlocked = unlockedFeatures.adventureScout,
            onUnlock = { onUnlockFeature(PremiumFeature.ADVENTURE_SCOUT) }
        ) {
            AdventureScoutContent(weather)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Feature 3: Event Advisor
        PremiumFeatureCard(
            title = "🎉 Event Advisor",
            description = "Never let rain ruin your special day. Discover the perfect hourly windows for outdoor gatherings and celebrations.",
            isUnlocked = unlockedFeatures.eventAdvisor,
            onUnlock = { onUnlockFeature(PremiumFeature.EVENT_ADVISOR) }
        ) {
            EventAdvisorContent(weather)
        }

        // Feature 4: AI Strategy
        PremiumFeatureCard(
            title = "🧠 AI Strategy",
            description = "Access deep executive summaries of weather patterns to make informed decisions for your business or energy consumption.",
            isUnlocked = unlockedFeatures.aiStrategy,
            onUnlock = { onUnlockFeature(PremiumFeature.AI_STRATEGY) }
        ) {
            AIStrategyContent(weather)
        }
    }
}
