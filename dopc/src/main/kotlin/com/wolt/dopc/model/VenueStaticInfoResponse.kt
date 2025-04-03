package com.wolt.dopc.model

data class StaticInfoResponse(
    val venue_raw: VenueRaw
) {
    data class VenueRaw(
        val location: Location
    ) {
        data class Location(
            val coordinates: List<Double>
        )
    }

    fun toVenueStaticInfo(): VenueStaticInfo {
        return VenueStaticInfo(
            coordinates = venue_raw.location.coordinates
        )
    }
}
