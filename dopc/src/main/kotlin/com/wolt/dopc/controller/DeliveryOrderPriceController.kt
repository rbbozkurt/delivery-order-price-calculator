package com.wolt.dopc.controller

import com.wolt.dopc.config.GeoConfig
import com.wolt.dopc.dto.DeliveryOrderPriceResponse
import com.wolt.dopc.dto.DeliveryOrderPriceResponse.Delivery
import com.wolt.dopc.service.DeliveryOrderPriceService
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Max
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * REST controller for handling delivery order price calculations.
 */
@RestController
@Validated
@RequestMapping("/api/v1/delivery-order-price")
class DeliveryOrderPriceController(
    private val deliveryOrderPriceService: DeliveryOrderPriceService
) {

    /**
     * Endpoint to calculate the delivery order price.
     *
     * @param venueSlug Unique identifier of the venue.
     * @param cartValue Total value of the items in the cart in cents.
     * @param userLat Latitude of the user's location.
     * @param userLon Longitude of the user's location.
     * @return Response containing total price, breakdown, and delivery details.
     */
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getDeliveryOrderPrice(
        @RequestParam("venue_slug")
        @NotBlank(message = "Venue slug must not be blank.")
        venueSlug: String,

        @RequestParam("cart_value")
        @Positive(message = "Cart value must be a positive integer.")
        cartValue: Int,

        @RequestParam("user_lat")
        @NotNull(message = "User latitude is required.")
        @Min(GeoConfig.MIN_LATITUDE.toLong(), message = "Latitude must be greater than or equal to ${GeoConfig.MIN_LATITUDE}.")
        @Max(GeoConfig.MAX_LATITUDE.toLong(), message = "Latitude must be less than or equal to ${GeoConfig.MAX_LATITUDE}.")
        userLat: Double,

        @RequestParam("user_lon")
        @NotNull(message = "User longitude is required.")
        @Min(GeoConfig.MIN_LONGITUDE.toLong(), message = "Longitude must be greater than or equal to ${GeoConfig.MIN_LONGITUDE}.")
        @Max(GeoConfig.MAX_LONGITUDE.toLong(), message = "Longitude must be less than or equal to ${GeoConfig.MAX_LONGITUDE}.")
        userLon: Double
    ): ResponseEntity<Any> {
        // Calculate delivery data using the service layer
        val deliveryData = deliveryOrderPriceService.calculateDeliveryData(
            venueSlug = venueSlug,
            cartValue = cartValue,
            userLat = userLat,
            userLon = userLon
        )

        // Construct and return the response
        val response = DeliveryOrderPriceResponse(
            totalPrice = deliveryData.totalPrice,
            smallOrderSurcharge = deliveryData.smallOrderSurcharge,
            cartValue = cartValue,
            delivery = Delivery(
                fee = deliveryData.deliveryFee,
                distance = deliveryData.deliveryDistance
            )
        )

        return ResponseEntity.ok(response)
    }
}