package com.ghrer.commerce.checkout.exception

class UnknownServiceException(
    override val message: String?
) : ApplicationException(false, message)
