package com.ghrer.commerce.orders.exception

open class ApplicationException(
    val retriable: Boolean,
    override val message: String? = null,
    override val cause: Throwable? = null
) : RuntimeException(message, cause)
