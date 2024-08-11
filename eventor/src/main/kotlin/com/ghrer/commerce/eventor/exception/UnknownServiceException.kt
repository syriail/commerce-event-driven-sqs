package com.ghrer.commerce.eventor.exception

class UnknownServiceException(
    override val message: String?
) : ApplicationException(false, message)
