package com.wolt.dopc.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Data class representing the response for venue static information.
 *
 * @property venueRaw The raw venue data.
 */
data class VenueStaticInfoResponse @JsonCreator constructor(
    @JsonProperty("venue_raw") val venueRaw: VenueRaw
) {
    data class VenueRaw @JsonCreator constructor(
        @JsonProperty("location") val location: Location
    ) {
        data class Location @JsonCreator constructor(
            @JsonProperty("coordinates") val coordinates: List<Double>
        )
    }

    fun toVenueStaticInfo(): VenueStaticInfo {
        return VenueStaticInfo(
            coordinates = venueRaw.location.coordinates
        )
    }
}
