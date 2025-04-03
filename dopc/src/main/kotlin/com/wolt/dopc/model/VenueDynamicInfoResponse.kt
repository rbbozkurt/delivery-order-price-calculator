package com.wolt.dopc.model

data class DynamicInfoResponse(
    val venue_raw: VenueRaw
) {
    data class VenueRaw(
        val delivery_specs: DeliverySpecs
    ) {
        data class DeliverySpecs(
            val order_minimum_no_surcharge: Int,
            val delivery_pricing: DeliveryPricing
        ) {
            data class DeliveryPricing(
                val base_price: Int,
                val distance_ranges: List<DistanceRange>
            ) {
                data class DistanceRange(
                    val min: Int,
                    val max: Int,
                    val a: Int,
                    val b: Double
                )
            }
        }
    }

    fun toVenueDynamicInfo(): VenueDynamicInfo {
        return VenueDynamicInfo(
            orderMinimumNoSurcharge = venue_raw.delivery_specs.order_minimum_no_surcharge,
            basePrice = venue_raw.delivery_specs.delivery_pricing.base_price,
            distanceRanges = venue_raw.delivery_specs.delivery_pricing.distance_ranges.map {
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
