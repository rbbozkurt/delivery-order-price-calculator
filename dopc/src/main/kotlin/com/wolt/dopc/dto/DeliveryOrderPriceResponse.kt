package com.wolt.dopc.model

data class DeliveryOrderPriceResponse(
    val totalPrice: Int,
    val smallOrderSurcharge: Int,
    val cartValue: Int,
    val delivery: Delivery
) {
    data class Delivery(
        val fee: Int,
        val distance: Int
    )
}
