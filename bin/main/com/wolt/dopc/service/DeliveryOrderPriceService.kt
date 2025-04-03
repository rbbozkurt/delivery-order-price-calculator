package com.wolt.dopc.service

import com.wolt.dopc.model.DeliveryCalculationResult
import com.wolt.dopc.model.VenueDynamicInfo
import com.wolt.dopc.model.VenueStaticInfo
import com.wolt.dopc.util.DistanceCalculator
import kotlinx.coroutines.*
import org.springframework.stereotype.Service

/**
 * Service class for calculating delivery order prices.
 *
 * @property homeAssignmentService The service used to fetch venue information.
 */
@Service
class DeliveryOrderPriceService(
    private val homeAssignmentService: HomeAssignmentService,
) {
    /**
     * Calculates the delivery data for a given venue and user location.
     *
     * @param venueSlug The slug identifier for the venue.
     * @param cartValue The value of the cart in cents.
     * @param userLat The latitude of the user's location.
     * @param userLon The longitude of the user's location.
     * @return The result of the delivery calculation.
     * @throws IllegalStateException If static or dynamic info for the venue is not found.
     * @throws IllegalArgumentException If the inputs are invalid.
     */
    fun calculateDeliveryData(
        venueSlug: String,
        cartValue: Int,
        userLat: Double,
        userLon: Double
    ): DeliveryCalculationResult {
        validateInputs(venueSlug, cartValue)

        // Fetch static and dynamic info concurrently


        val staticInfo = homeAssignmentService.getStaticInfo(venueSlug)
        val venueCoordinates = staticInfo.coordinates
        val dynamicInfo =homeAssignmentService.getDynamicInfo(venueSlug)

        val smallOrderSurcharge = calculateSmallOrderSurcharge(cartValue, dynamicInfo.orderMinimumNoSurcharge)

        val distance = calculateDistance(userLat, userLon, venueCoordinates)
        val deliveryFee = calculateDeliveryFee(distance, dynamicInfo)

        val totalPrice = cartValue + smallOrderSurcharge + deliveryFee

        return DeliveryCalculationResult(
            totalPrice = totalPrice,
            smallOrderSurcharge = smallOrderSurcharge,
            deliveryFee = deliveryFee,
            deliveryDistance = distance
        )
    }

    /**
     * Validates the input parameters for the delivery calculation.
     *
     * @param venueSlug The slug identifier for the venue.
     * @param cartValue The value of the cart in cents.
     * @param userLat The latitude of the user's location.
     * @param userLon The longitude of the user's location.
     * @throws IllegalArgumentException If any of the inputs are invalid.
     */
    private fun validateInputs(venueSlug: String, cartValue: Int) {
        if (venueSlug.isBlank()) throw IllegalArgumentException("Venue slug cannot be blank")
        if (cartValue <= 0 || cartValue > MAX_CART_VALUE) {
            throw IllegalArgumentException("Cart value must be between 1 and $MAX_CART_VALUE cents")
        }
    }

    /**
     * Calculates the distance between the user's location and the venue.
     *
     * @param userLat The latitude of the user's location.
     * @param userLon The longitude of the user's location.
     * @param venueCoordinates The coordinates of the venue.
     * @return The distance in meters.
     * @throws IllegalStateException If the venue coordinates are invalid.
     */
    private fun calculateDistance(userLat: Double, userLon: Double, venueCoordinates: List<Double>): Int {
        if (venueCoordinates.size != 2) {
            throw IllegalStateException("Invalid venue coordinates: $venueCoordinates")
        }
        return DistanceCalculator.calculateDistance(userLat, userLon, venueCoordinates[1], venueCoordinates[0])
    }

    /**
     * Calculates the small order surcharge based on the cart value and the minimum order value without surcharge.
     *
     * @param cartValue The value of the cart in cents.
     * @param orderMinimumNoSurcharge The minimum order value without surcharge.
     * @return The small order surcharge in cents.
     */
    private fun calculateSmallOrderSurcharge(cartValue: Int, orderMinimumNoSurcharge: Int): Int {
        return maxOf(0, orderMinimumNoSurcharge - cartValue)
    }

    /**
     * Calculates the delivery fee based on the distance and the dynamic information of the venue.
     *
     * @param distance The distance in meters.
     * @param dynamicInfo The dynamic information of the venue.
     * @return The delivery fee in cents.
     * @throws IllegalStateException If the distance ranges are empty.
     * @throws IllegalArgumentException If the delivery is not possible for the given distance.
     */
    private fun calculateDeliveryFee(distance: Int, dynamicInfo: VenueDynamicInfo): Int {
        val distanceRanges = dynamicInfo.distanceRanges
        if (distanceRanges.isEmpty()) {
            throw IllegalStateException("Distance ranges cannot be empty")
        }

        val range = distanceRanges.firstOrNull { it.min <= distance && (it.max == 0 || distance < it.max) }
            ?: throw IllegalArgumentException("Delivery not possible for distance: $distance meters")

        val distanceFee = (range.b * distance / 10)
        return dynamicInfo.basePrice + range.a + distanceFee
    }

    companion object {
        // Maximum cart value in cents
        const val MAX_CART_VALUE = 1_000_000
    }
}
