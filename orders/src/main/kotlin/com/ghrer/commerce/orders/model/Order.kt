package com.ghrer.commerce.orders.model

import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.PrePersist
import jakarta.persistence.Table
import org.hibernate.annotations.Type
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "orders")
data class Order(
    @Id
    @Column(name = "id")
    val id: UUID = UUID.randomUUID(),

    @Column(name = "customer_id")
    val customerId: String,

    @Type(JsonType::class)
    @Column(name = "address")
    val address: Address,

    @Column(name = "payment_id", nullable = true)
    val paymentId: String? = null,

    @Column(name = "shipment_id", nullable = true)
    val shipmentId: String? = null,

    @Column(name = "total_price")
    val totalPrice: Double,

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    val status: OrderStatus = OrderStatus.PLACED,

    @Column(name = "create_date")
    var createDate: LocalDateTime? = null,
) {
    @PrePersist
    fun prePersist() {
        createDate = LocalDateTime.now()
    }
}

data class Address(
    val firstName: String,
    val lastName: String,
    val street: String,
    val houseNumber: String,
    val postCode: String,
    val city: String,
)
