package com.ghrer.commerce.inventory.exception

import java.util.UUID

class ItemNotFoundException(
    override val message: String? = null,
    val notFoundItems: List<UUID>
) : ApplicationException(false, message)
