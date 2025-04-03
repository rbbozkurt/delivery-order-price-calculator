package com.wolt.dopc.util

import com.wolt.dopc.config.GeoConfig
import kotlin.math.*

/**
 * Object for calculating the distance between two geographical coordinates.
 */
object DistanceCalculator {

    /**
     * Calculates the distance between two geographical coordinates using the Haversine formula.
     *
     * @param lat1 The latitude of the first coordinate.
     * @param lon1 The longitude of the first coordinate.
     * @param lat2 The latitude of the second coordinate.
     * @param lon2 The longitude of the second coordinate.
     * @return The distance between the two coordinates in meters.
     * @throws IllegalArgumentException if any latitude or longitude is out of the valid range.
     */
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Int {
        validateCoordinates(lat1, lon1)
        validateCoordinates(lat2, lon2)

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (GeoConfig.EARTH_RADIUS * c).roundToInt()
    }

    /**
     * Validates that the given latitude and longitude are within the valid range.
     *
     * @param latitude The latitude to validate.
     * @param longitude The longitude to validate.
     * @throws IllegalArgumentException if the latitude or longitude is out of the valid range.
     */
    private fun validateCoordinates(latitude: Double, longitude: Double) {
        if (latitude !in GeoConfig.MIN_LATITUDE..GeoConfig.MAX_LATITUDE) {
            throw IllegalArgumentException("Invalid latitude: $latitude. Latitude must be between ${GeoConfig.MIN_LATITUDE} and ${GeoConfig.MAX_LATITUDE} degrees.")
        }
        if (longitude !in GeoConfig.MIN_LONGITUDE..GeoConfig.MAX_LONGITUDE) {
            throw IllegalArgumentException("Invalid longitude: $longitude. Longitude must be between ${GeoConfig.MIN_LONGITUDE} and ${GeoConfig.MAX_LONGITUDE} degrees.")
        }
    }
}