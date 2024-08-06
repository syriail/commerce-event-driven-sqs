package com.ghrer.commerce.inventory.exception

import com.ghrer.commerce.inventory.controller.dto.UnavailableItem

class NotEnoughQuantityAvailableException(
    override val message: String? = null,
    val unavailableItems: List<UnavailableItem>
) : ApplicationException(false, message)
