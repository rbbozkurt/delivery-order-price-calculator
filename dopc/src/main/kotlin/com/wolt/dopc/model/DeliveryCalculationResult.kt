package com.wolt.dopc.model

data class DeliveryData(
    val totalPrice: Int,
    val smallOrderSurcharge: Int,
    val deliveryFee: Int,
    val deliveryDistance: Int
)
