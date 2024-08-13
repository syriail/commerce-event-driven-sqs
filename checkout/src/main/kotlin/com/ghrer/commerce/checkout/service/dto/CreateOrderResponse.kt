package com.ghrer.commerce.checkout.service.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.ghrer.commerce.checkout.dto.AddressDto
import com.ghrer.commerce.checkout.dto.ItemDto
import com.ghrer.commerce.checkout.dto.OrderStatus
import java.time.LocalDateTime
import java.util.UUID

@JsonIgnoreProperties(ignoreUnknown = true)
data class CreateOrderResponse(
    val id: UUID,
    val customerId: String,
    val customerAddress: AddressDto,
    val paymentId: String? = null,
    val shipmentId: String? = null,
    val totalPrice: Double,
    val status: OrderStatus,
    val createDate: LocalDateTime,
    val items: List<ItemDto>
)
