package com.ghrer.commerce.checkout.exception

import java.util.UUID

class ItemNotAvailableException(
    val unavailableItems: List<UnavailableItem>
) : ApplicationException(false)

data class UnavailableItem(
    val itemId: UUID,
    val requestedQuantity: Int,
    val availableQuantity: Int,
)
