package com.wolt.dopc.config

import org.springframework.stereotype.Component

/**
 * Configuration class for geographical constants.
 */
@Component
class GeoConfig {
    companion object {
        // Constants for geographical bounds
        const val MIN_LATITUDE: Double = -90.0
        const val MAX_LATITUDE: Double = 90.0
        const val MIN_LONGITUDE: Double = -180.0
        const val MAX_LONGITUDE: Double = 180.0
        const val EARTH_RADIUS: Double = 6371000.0 // meters
    }
}
