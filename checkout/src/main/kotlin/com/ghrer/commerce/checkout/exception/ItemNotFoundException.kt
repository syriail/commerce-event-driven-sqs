package com.ghrer.commerce.checkout.exception

import java.util.UUID

class ItemNotFoundException(
    override val message: String? = null,
    val notFoundItems: List<UUID>
) : ApplicationException(false, message)
