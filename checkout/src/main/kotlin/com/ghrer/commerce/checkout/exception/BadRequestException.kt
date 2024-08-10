package com.ghrer.commerce.checkout.exception

class BadRequestException(
    override val message: String? = null,
    val fields: Map<String, String>? = null,
) : ApplicationException(false, message)
