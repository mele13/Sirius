package com.example.sirius.tools

import android.content.Context
import android.location.Address
import android.location.Geocoder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale

fun getZoneName(context: Context, latitude: Double, longitude: Double): String? {
    val geocoder = Geocoder(context, Locale.getDefault())
    val addresses: List<Address?>? = geocoder.getFromLocation(latitude, longitude, 1)
    println("addresses")
    println(addresses)
    return if (!addresses.isNullOrEmpty()) {
        addresses[0]?.subAdminArea
    } else {
        null
    }
}

fun getZoneNames(context: Context, locations: List<Location>): List<String?> {
    val zoneNames = mutableListOf<String?>()
    for (location in locations) {
        val zoneName = getZoneName(context, location.latitude, location.longitude)
        zoneNames.add(zoneName)
    }
    return zoneNames
}

data class Location(val latitude: Double, val longitude: Double)

fun parseCoordinates(coordinates: List<String>): List<Location> {
    val locations = mutableListOf<Location>()
    for (coord in coordinates) {
        val (latStr, longStr) = coord.split(";")
        val latitude = latStr.toDouble()
        val longitude = longStr.toDouble()
        locations.add(Location(latitude, longitude))
    }
    return locations
}

fun parseLocationsFlow(flow: Flow<List<String>>): Flow<List<Location>> {
    return flow.map { coordinates ->
        parseCoordinates(coordinates)
    }
}