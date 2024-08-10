package com.ghrer.commerce.checkout.controller.dto

import com.ghrer.commerce.checkout.dto.AddressDto
import com.ghrer.commerce.checkout.dto.ItemDto
import jakarta.validation.constraints.Size

data class PlaceOrderRequest(
    val customerId: String,
    @field:Size(min = 1, message = "Items cannot be empty")
    val items: List<ItemDto>,
    val totalPrice: Double,
    val customerAddress: AddressDto,
)
