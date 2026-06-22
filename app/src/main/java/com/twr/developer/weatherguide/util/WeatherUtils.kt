package com.twr.developer.weatherguide.util

import java.text.SimpleDateFormat
import java.util.*

object WeatherUtils {
    
    fun parseTime(timeStr: String?): ParsedTime? {
        if (timeStr.isNullOrEmpty()) return null
        return try {
            // WeatherAPI time format is usually "yyyy-MM-dd HH:mm"
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val date = inputFormat.parse(timeStr) ?: return null
            val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

            ParsedTime(
                date = date,
                dayOfWeek = dayFormat.format(date),
                time = timeFormat.format(date)
            )
        } catch (e: Exception) {
            null
        }
    }

    fun formatDate(dateStr: String?): String {
        if (dateStr.isNullOrEmpty()) return "Unknown"
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
            val date = inputFormat.parse(dateStr)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateStr
        }
    }

    data class ParsedTime(
        val date: Date,
        val dayOfWeek: String,
        val time: String
    )
}