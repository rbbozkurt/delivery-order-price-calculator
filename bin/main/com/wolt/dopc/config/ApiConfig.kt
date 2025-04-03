package com.wolt.dopc.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

/**
 * Configuration class for managing API URLs.
 */
@Configuration
class ApiConfig {

    @Value("\${api.base.url}")
    lateinit var baseUrl: String

    @Value("\${api.venue.static.endpoint}")
    lateinit var venueStaticEndpoint: String

    @Value("\${api.venue.dynamic.endpoint}")
    lateinit var venueDynamicEndpoint: String

    /**
     * Constructs the full URL for the venue static info API.
     */
    fun getVenueStaticUrl(venueSlug: String): String {
        return "$baseUrl$venueStaticEndpoint".replace("{venueSlug}", venueSlug)
    }

    /**
     * Constructs the full URL for the venue dynamic info API.
     */
    fun getVenueDynamicUrl(venueSlug: String): String {
        return "$baseUrl$venueDynamicEndpoint".replace("{venueSlug}", venueSlug)
    }
}
