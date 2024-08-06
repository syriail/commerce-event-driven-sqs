package com.ghrer.commerce.inventory.controller.dto

import java.util.UUID

data class UnavailableItem(
    val itemId: UUID,
    val requestedQuantity: Int,
    val availableQuantity: Int,
)
