package com.ghrer.commerce.eventor.model

import java.util.UUID

data class Item(
    val id: UUID,
    val quantity: Int,
    val price: Double
)
