package com.twr.developer.weatherguide.domain.model

data class Location (
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val region: String? = null,
    val timezone: String = ""
) {
    // For display purposes
    val displayName: String
        get() = if (region != null) "$name, $region, $country" else "$name, $country"
    
    // For API calls
    val coordinates: String
        get() = "$lat,$lon"
}

object PopularLocations {
    val locations = listOf(
        Location(
            name = "Nairobi",
            country = "KE",
            region = "Nairobi County",
            lat = -1.2921,
            lon = 36.8219
        ),
        Location(
            name = "Mombasa",
            country = "KE",
            region = "Mombasa County",
            lat = -4.0435,
            lon = 39.6682
        ),
        Location(
            name = "Kisumu",
            country = "KE",
            region = "Kisumu County",
            lat = -0.1022,
            lon = 34.7617
        ),
        Location(
            name = "Nakuru",
            country = "KE",
            region = "Nakuru County",
            lat = -0.3031,
            lon = 36.0800
        ),
        Location(
            name = "Eldoret",
            country = "KE",
            region = "Uasin Gishu County",
            lat = 0.5143,
            lon = 35.2698
        ),
        Location(
            name = "Thika",
            country = "KE",
            region = "Kiambu County",
            lat = -1.0444,
            lon = 37.0698
        ),
        Location(
            name = "Malindi",
            country = "KE",
            region = "Kilifi County",
            lat = -3.2192,
            lon = 40.1169
        ),
        Location(
            name = "Kitale",
            country = "KE",
            region = "Trans-Nzoia County",
            lat = 1.0157,
            lon = 35.0062
        ),
        Location(
            name = "Garissa",
            country = "KE",
            region = "Garissa County",
            lat = -0.4637,
            lon = 39.6458
        ),
        Location(
            name = "Kakamega",
            country = "KE",
            region = "Kakamega County",
            lat = 0.2827,
            lon = 34.7519
        )
    )

    // Helper: Get by name
    fun getByName(name: String): Location? {
        return locations.find { it.name.equals(name, ignoreCase = true) }
    }

    // Helper: Get by coordinates
    fun getByCoords(lat: Double, lon: Double): Location? {
        return locations.find { 
            kotlin.math.abs(it.lat - lat) < 0.01 && 
            kotlin.math.abs(it.lon - lon) < 0.01 
        }
    }
}