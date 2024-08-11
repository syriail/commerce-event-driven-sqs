package com.ghrer.commerce.eventor.exception

class ServiceInternalErrorException(
    override val message: String? = null
) : ApplicationException(false, message)
