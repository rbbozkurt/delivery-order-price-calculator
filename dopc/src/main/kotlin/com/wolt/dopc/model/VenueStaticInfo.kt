package com.wolt.dopc.model

/**
 * Data class representing static information about a venue.
 *
 * @property coordinates The list of coordinates representing the venue's location.
 */
data class VenueStaticInfo(
    val coordinates: List<Double>
)