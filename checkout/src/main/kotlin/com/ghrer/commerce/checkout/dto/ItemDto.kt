package com.ghrer.commerce.checkout.dto

import java.util.UUID

data class ItemDto(
    val id: UUID,
    val quantity: Int,
    val price: Double
)
