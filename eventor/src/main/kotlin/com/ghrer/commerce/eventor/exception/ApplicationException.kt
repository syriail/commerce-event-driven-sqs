package com.ghrer.commerce.checkout.exception

open class ApplicationException(
    val retriable: Boolean,
    override val message: String? = null,
    override val cause: Throwable? = null
) : RuntimeException(message, cause)
