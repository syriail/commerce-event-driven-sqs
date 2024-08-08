package com.ghrer.commerce.orders.model

import java.time.LocalDateTime
import java.util.UUID

data class OrderAggregate(
    val id: UUID,
    val customerId: String,
    val address: Address,
    val paymentId: String? = null,
    val shipmentId: String? = null,
    val totalPrice: Double,
    val status: OrderStatus,
    val createDate: LocalDateTime,
    val items: List<Item>
)

data class Item(
    val itemId: UUID,
    val quantity: Int,
    val price: Double
)
