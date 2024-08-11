package com.ghrer.commerce.eventor.exception

open class ApplicationException(
    val retriable: Boolean,
    override val message: String? = null,
    override val cause: Throwable? = null
) : RuntimeException(message, cause)
