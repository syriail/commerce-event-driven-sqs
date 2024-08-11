package com.ghrer.commerce.eventor.exception

class ServiceUnavailableException(
    override val message: String? = null
) : ApplicationException(true, message)
