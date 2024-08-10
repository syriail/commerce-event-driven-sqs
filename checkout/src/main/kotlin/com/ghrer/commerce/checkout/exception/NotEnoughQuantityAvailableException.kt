package com.ghrer.commerce.checkout.exception

import com.ghrer.commerce.checkout.service.dto.UnavailableItem

class NotEnoughQuantityAvailableException(
    override val message: String? = null,
    val unavailableItems: List<UnavailableItem>
) : ApplicationException(false, message)
