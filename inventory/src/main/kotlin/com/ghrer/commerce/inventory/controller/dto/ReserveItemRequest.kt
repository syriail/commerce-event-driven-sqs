package com.ghrer.commerce.inventory.controller.dto

import java.util.UUID

data class ReserveItemRequest(
    val id: UUID,
    val quantity: Int,
)
