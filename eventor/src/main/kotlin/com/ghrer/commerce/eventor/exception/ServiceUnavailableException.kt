package com.ghrer.commerce.checkout.exception

class ServiceUnavailableException(
    override val message: String? = null
) : ApplicationException(true, message)
