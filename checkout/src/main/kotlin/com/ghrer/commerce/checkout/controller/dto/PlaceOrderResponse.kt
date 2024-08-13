package com.ghrer.commerce.checkout.controller.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.ghrer.commerce.checkout.dto.AddressDto
import com.ghrer.commerce.checkout.dto.ItemDto
import com.ghrer.commerce.checkout.dto.OrderStatus
import java.time.LocalDateTime
import java.util.UUID

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class PlaceOrderResponse(
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
