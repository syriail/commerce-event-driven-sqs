package com.ghrer.commerce.checkout.dto

import java.util.UUID

data class PlaceOrderRequest(
    val customer: CustomerDto,
    val items: List<PlaceOrderRequestItem>,
    val invoiceAddress: AddressDto,
    val shippingAddress: AddressDto,
)

data class PlaceOrderRequestItem(
    val itemId: UUID,
    val quantity: Int,
)
