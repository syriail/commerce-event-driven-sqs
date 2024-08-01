package com.ghrer.commerce.checkout.exception

class ServiceInternalErrorException(
    override val message: String? = null
) : ApplicationException(false, message)
