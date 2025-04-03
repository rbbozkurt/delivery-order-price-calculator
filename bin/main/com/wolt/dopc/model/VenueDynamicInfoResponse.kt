package com.wolt.dopc.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Data class representing the response for venue dynamic information.
 *
 * @property venueRaw The raw venue data.
 */
data class VenueDynamicInfoResponse @JsonCreator constructor(
    @JsonProperty("venue_raw") val venueRaw: VenueRaw
) {
    /**
     * Data class representing the raw venue data.
     *
     * @property deliverySpecs The delivery specifications.
     */
    data class VenueRaw @JsonCreator constructor(
        @JsonProperty("delivery_specs") val deliverySpecs: DeliverySpecs
    ) {
        /**
         * Data class representing the delivery specifications.
         *
         * @property orderMinimumNoSurcharge The minimum order value without surcharge.
         * @property deliveryPricing The delivery pricing details.
         */
        data class DeliverySpecs @JsonCreator constructor(
            @JsonProperty("order_minimum_no_surcharge") val orderMinimumNoSurcharge: Int,
            @JsonProperty("delivery_pricing") val deliveryPricing: DeliveryPricing
        ) {
            /**
             * Data class representing the delivery pricing details.
             *
             * @property basePrice The base price for the delivery.
             * @property distanceRanges The list of distance ranges for delivery pricing.
             */
            data class DeliveryPricing @JsonCreator constructor(
                @JsonProperty("base_price") val basePrice: Int,
                @JsonProperty("distance_ranges") val distanceRanges: List<DistanceRange>
            ) {
                /**
                 * Data class representing a distance range for delivery pricing.
                 *
                 * @property min The minimum distance in the range.
                 * @property max The maximum distance in the range.
                 * @property a The coefficient 'a' used in the pricing formula.
                 * @property b The coefficient 'b' used in the pricing formula.
                 */
                data class DistanceRange @JsonCreator constructor(
                    @JsonProperty("min") val min: Int,
                    @JsonProperty("max") val max: Int,
                    @JsonProperty("a") val a: Int,
                    @JsonProperty("b") val b: Int
                )
            }
        }
    }

    /**
     * Converts the raw venue data to a VenueDynamicInfo object.
     *
     * @return The VenueDynamicInfo object.
     */
    fun toVenueDynamicInfo(): VenueDynamicInfo {
        return VenueDynamicInfo(
            orderMinimumNoSurcharge = venueRaw.deliverySpecs.orderMinimumNoSurcharge,
            basePrice = venueRaw.deliverySpecs.deliveryPricing.basePrice,
            distanceRanges = venueRaw.deliverySpecs.deliveryPricing.distanceRanges.map {
                VenueDynamicInfo.DistanceRange(
                    min = it.min,
                    max = it.max,
                    a = it.a,
                    b = it.b
                )
            }
        )
    }
}
