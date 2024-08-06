package com.ghrer.commerce.inventory.controller.dto

import java.util.UUID

data class ReserveItemRequest(
    val itemId: UUID,
    val quantity: Int,
)
