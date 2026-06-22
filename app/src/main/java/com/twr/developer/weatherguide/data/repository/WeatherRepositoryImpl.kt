package com.twr.developer.weatherguide.data.repository

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.twr.developer.weatherguide.BuildConfig
import com.twr.developer.weatherguide.data.local.dao.WeatherDao
import com.twr.developer.weatherguide.data.local.entity.CachedWeather
import com.twr.developer.weatherguide.data.remote.api.WeatherApiService
import com.twr.developer.weatherguide.domain.model.InsightsResponse
import com.twr.developer.weatherguide.domain.model.Location
import com.twr.developer.weatherguide.domain.model.UsageResponse
import com.twr.developer.weatherguide.domain.model.WeatherResponse
import com.twr.developer.weatherguide.domain.repository.WeatherRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApiService,
    private val dao: WeatherDao,
    private val context: Context // For loading mock JSON from assets
) : WeatherRepository {

    companion object {
        private const val CACHE_TTL_MS = 30 * 60 * 1000L // 30 minutes
        private const val USE_MOCK = false// Toggle for demo
        private val apiKey = BuildConfig.WEATHER_API_KEY
        private val authHeader = "Bearer $apiKey"
    }

    override suspend fun getWeather(location: Location): WeatherResponse {
        Log.d("WeatherRepo", "Fetching weather for: $location")
        
        // Check cache
        val cached = dao.get(location.name)
        /*if (cached != null && System.currentTimeMillis() - cached.timestamp < CACHE_TTL_MS) {
            Log.d("WeatherRepo", "Returning cached weather for: $location")
            return Gson().fromJson(cached.jsonResponse, WeatherResponse::class.java)
        }*/

        // Fetch fresh from API (Real-time data for summary)
        Log.d("WeatherRepo", "No valid cache. Calling WeatherAPI for: $location")
        val fresh = try {
            if (USE_MOCK) {
                Log.d("WeatherRepo", "Using mock data for: $location")
                val json = context.assets.open("forecast14.json").bufferedReader().use { it.readText() }
                Gson().fromJson(json, WeatherResponse::class.java)
            } else {
                api.getForecast(authHeader, location.lat, location.lon)
            }
        } catch (e: Exception) {
            Log.e("WeatherRepo", "API call failed for $location: ${e.message}")
            // Fallback to cached if API fails, even if expired
            cached?.let { 
                Log.w("WeatherRepo", "Falling back to expired cache for: $location")
                return Gson().fromJson(it.jsonResponse, WeatherResponse::class.java) 
            }
            throw e
        }

        Log.d("WeatherRepo", "Successfully fetched fresh data for: $location")
        // Cache the result
        val json = Gson().toJson(fresh)
        dao.insert(CachedWeather(location.name, json))
        return fresh
    }

    override suspend fun getForecast(location: Location): WeatherResponse {
        Log.d("WeatherRepo", "Fetching weather for: $location")

        // Check cache
        val cached = dao.get(location.name)
        /*if (cached != null && System.currentTimeMillis() - cached.timestamp < CACHE_TTL_MS) {
            Log.d("WeatherRepo", "Returning cached weather for: $location")
            return Gson().fromJson(cached.jsonResponse, WeatherResponse::class.java)
        }*/

        // Fetch fresh from API (Real-time data for summary)
        Log.d("WeatherRepo", "No valid cache. Calling WeatherAPI for: $location")
        val fresh = try {
            if (USE_MOCK) {
                Log.d("WeatherRepo", "Using mock data for: $location")
                val json = context.assets.open("forecast14.json").bufferedReader().use { it.readText() }
                Gson().fromJson(json, WeatherResponse::class.java)
            } else {
                api.getForecast(authHeader, location.lat, location.lon)
            }
        } catch (e: Exception) {
            Log.e("WeatherRepo", "API call failed for $location: ${e.message}")
            // Fallback to cached if API fails, even if expired
            cached?.let {
                Log.w("WeatherRepo", "Falling back to expired cache for: $location")
                return Gson().fromJson(it.jsonResponse, WeatherResponse::class.java)
            }
            throw e
        }

        Log.d("WeatherRepo", "Successfully fetched fresh data for: $location")
        // Cache the result
        val json = Gson().toJson(fresh)
        dao.insert(CachedWeather(location.name, json))
        return fresh
    }

    override suspend fun getPremiumForecast(location: Location): WeatherResponse {
        return if (USE_MOCK) {
            // Load from assets/forecast14.json
            val json = context.assets.open("forecast14.json").bufferedReader().use { it.readText() }
            Gson().fromJson(json, WeatherResponse::class.java)
        } else {
            api.getForecast14(authHeader, location.lat, location.lon)
        }
    }

    override suspend fun getAIInsights(location: Location): InsightsResponse {
        return if (USE_MOCK) {
            // Load from assets/insights.json
            val json = context.assets.open("insights.json").bufferedReader().use { it.readText() }
            Gson().fromJson(json, InsightsResponse::class.java)
        } else {
            api.getInsights(authHeader, location.lat, location.lon)
        }
    }

    override suspend fun getUsage(): UsageResponse {
        return if (USE_MOCK) {
            // Mock usage data
            UsageResponse(
                plan = "free",
                requests_used = 347,
                requests_limit = 1000,
                ai_requests_used = 0,
                ai_requests_limit = 200,
                remaining = 653,
                resets_at = "2026-07-01T00:00:00.000Z"
            )
        } else {
            api.getUsage(authHeader)
        }
    }
}