package com.ghrer.commerce.orders.dto

import java.util.UUID

data class CreateOrderRequest(
    val customerId: String,
    val totalPrice: Double,
    val customerAddress: OrderRequestAddress,
    val items: List<OrderRequestOrderItem>
)

data class OrderRequestAddress(
    val firstName: String,
    val lastName: String,
    val street: String,
    val houseNumber: String,
    val postalCode: String,
    val city: String,
)

data class OrderRequestOrderItem(
    val id: UUID,
    val quantity: Int,
    val price: Double
)
