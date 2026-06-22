package com.twr.developer.weatherguide.utils

import android.content.Context

fun Context.loadJsonFromAssets(filename: String): String {
    return assets.open(filename).bufferedReader().use { it.readText() }
}