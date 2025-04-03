package com.wolt.dopc.dto

/**
 * Data class representing the response for delivery order price.
 *
 * @property totalPrice The total price of the delivery order.
 * @property smallOrderSurcharge The surcharge applied for small orders.
 * @property cartValue The total value of the items in the cart.
 * @property delivery The delivery details including fee and distance.
 */
data class DeliveryOrderPriceResponse(
    val totalPrice: Int,
    val smallOrderSurcharge: Int,
    val cartValue: Int,
    val delivery: Delivery
) {
    /**
     * Data class representing the delivery details.
     *
     * @property fee The delivery fee.
     * @property distance The delivery distance.
     */
    data class Delivery(
        val fee: Int,
        val distance: Int
    )
}