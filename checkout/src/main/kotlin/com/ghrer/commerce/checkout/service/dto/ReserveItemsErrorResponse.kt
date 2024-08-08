package com.ghrer.commerce.checkout.service.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.UUID

@JsonIgnoreProperties(ignoreUnknown = true)
data class ReserveItemsErrorResponse(
    val message: String? = null,
    val notFoundItems: List<UUID> = emptyList(),
    val unavailableItems: List<UnavailableItem> = emptyList(),
)

data class UnavailableItem(
    val itemId: UUID,
    val requestedQuantity: Int,
    val availableQuantity: Int,
)
