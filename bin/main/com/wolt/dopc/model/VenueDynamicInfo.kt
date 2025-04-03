package com.wolt.dopc.model

/**
 * Data class representing dynamic information about a venue.
 *
 * @property orderMinimumNoSurcharge The minimum order value without surcharge.
 * @property basePrice The base price for the delivery.
 * @property distanceRanges The list of distance ranges for delivery pricing.
 */
data class VenueDynamicInfo(
    val orderMinimumNoSurcharge: Int,
    val basePrice: Int,
    val distanceRanges: List<DistanceRange>
) {
    /**
     * Data class representing a distance range for delivery pricing.
     *
     * @property min The minimum distance in the range.
     * @property max The maximum distance in the range.
     * @property a The coefficient 'a' used in the pricing formula.
     * @property b The coefficient 'b' used in the pricing formula.
     */
    data class DistanceRange(
        val min: Int,
        val max: Int,
        val a: Int,
        val b: Int
    )
}