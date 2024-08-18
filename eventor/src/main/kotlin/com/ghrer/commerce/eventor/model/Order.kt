package com.ghrer.commerce.eventor.model

import java.time.LocalDateTime
import java.util.UUID

data class Order(
    val id: UUID,
    val customerId: String,
    val customerAddress: Address,
    val paymentId: String? = null,
    val shipmentId: String? = null,
    val totalPrice: Double,
    val status: OrderStatus,
    val createDate: LocalDateTime,
    val items: List<Item>
)
