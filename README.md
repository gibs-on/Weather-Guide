# 🌤️ Weather Guide

**Navigate your day. Farm smarter. Adventure better.**

WeatherAI Compass is an intelligent weather application that transforms raw weather data into actionable insights for **farmers**, **adventure enthusiasts**, and **event planners**. Built with a clean MVVM architecture and Jetpack Compose, it demonstrates real-world integration with the WeatherAI API.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-purple.svg)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.06.00-blue.svg)](https://developer.android.com/jetpack/compose)

---

## 📱 Features

### 🌍 Free Tier (Always Available)
- **Current Weather**: Temperature, wind speed, humidity, and feels-like temperature
- **7-Day Forecast**: Daily overview with high/low temperatures and condition icons
- **Hourly Breakdown**: Detailed hour-by-hour weather data
- **Smart Caching**: 30-minute TTL cache to minimize API calls (respects Free tier limits)
- **Location Search**: Search by city name or use GPS coordinates

### 🌱 Premium Features (Unlockable)
| Feature | What It Does | API Used |
|---------|--------------|----------|
| **Harvest Advisor** | Scores planting, spraying, and harvesting windows based on weather conditions | `/v1/daily` |
| **Adventure Scout** | Ranks days for hiking, skiing, and surfing activities | `/v1/daily` |
| **Event Advisor** | Finds the best hour for outdoor events (weddings, sports, photoshoots) | `/v1/hourly` |
| **AI Strategy** | AI-powered insights and personalized recommendations | `/v1/insights` (Pro) |

### 💰 Monetization Strategy
- **A la carte unlocks**: Users unlock only the features they need
- **"Unlock All"**: One-click access to all premium features
- **Mocked Paywall**: Ready for Google Play Billing integration

---


### Tech Stack
| Component | Technology | Why |
|-----------|------------|-----|
| **Language** | Kotlin 2.0.0 | Modern, concise, safe |
| **UI** | Jetpack Compose | Declarative UI, Material 3 |
| **DI** | Dagger Hilt | Clean dependency injection |
| **Networking** | Retrofit + OkHttp | Type-safe API calls |
| **Caching** | Room | Offline-first experience |
| **Preferences** | DataStore | Lightweight key-value storage |
| **Async** | Coroutines + Flow | Reactive, efficient threading |
| **Image Loading** | Coil | Compose-friendly image loading |

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Jellyfish | 2023.3.1 or higher
- Android SDK 24+
- WeatherAI API Key (Free tier available)

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/yourusername/weatherAI-compass.git
cd weatherAI-compass
```

2. **Add your API key**

Create or edit local.properties in the project root:

```
WEATHER_API_KEY=wai_your_api_key_here
```
3. **Build and run**

```
# Build the app
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Or open in Android Studio and click Run
```

### Running with Mock Data

Set `USE_MOCK = true` in `WeatherRepositoryImpl.kt` to use sample data without an API key.

### 📦 Download APK

**Latest Release**

Download the latest APK from the [Releases](https://github.com/gibs-on/Weather-Guide/releases) page.

### GitHub Actions Build

Every push to `main` triggers a GitHub Actions workflow that builds and uploads the APK as an artifact:

1. Go to Actions tab 
2. Select the latest workflow run 
3. Download `app-debug.apk` or `app-release.apk` from artifacts

### 📄 License
Distributed under the Apache 2 `License.` See LICENSE for more information.

### 🙏 Acknowledgments
WeatherAI team for their comprehensive API documentation

Open-source community for the amazing libraries

All users testing and providing feedback

### 📬 Contact
Gibson - @gibson_c_ - dev.gibson@gmail.com

Project Link: https://github.com/gibs-on/weather-guide