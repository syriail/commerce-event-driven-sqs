package com.ghrer.commerce.orders.model

import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import java.io.Serializable
import java.util.UUID

@Entity
data class OrderItem(
    @EmbeddedId
    val orderItemId: OrderItemId,
    val quantity: Int,
    val price: Double
)

@Embeddable
data class OrderItemId(
    val id: UUID,
    val orderId: UUID
) : Serializable
