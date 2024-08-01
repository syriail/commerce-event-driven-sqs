package com.ghrer.commerce.orders.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.UUID

@Entity
data class OrderItem(
    @Id
    val id: UUID,
    val orderId: UUID,
    val quantity: Int,
    val price: Double
)
