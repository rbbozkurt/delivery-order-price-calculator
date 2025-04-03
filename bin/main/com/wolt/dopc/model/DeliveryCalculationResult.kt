package com.wolt.dopc.model

/**
 * Data class representing the result of a delivery calculation.
 *
 * @property totalPrice The total price of the delivery order.
 * @property smallOrderSurcharge The surcharge applied for small orders.
 * @property deliveryFee The fee for the delivery.
 * @property deliveryDistance The distance of the delivery.
 */
data class DeliveryCalculationResult(
    val totalPrice: Int,
    val smallOrderSurcharge: Int,
    val deliveryFee: Int,
    val deliveryDistance: Int
)