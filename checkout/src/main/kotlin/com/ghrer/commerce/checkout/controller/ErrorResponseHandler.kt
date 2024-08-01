package com.ghrer.commerce.checkout.controller

import com.ghrer.commerce.checkout.exception.ItemNotAvailableException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import kotlin.jvm.internal.Intrinsics.Kotlin

@RestControllerAdvice
class ErrorResponseAdvisor {

    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    @ExceptionHandler(ItemNotAvailableException::class)
    fun handleItemNotAvailableException(e: ItemNotAvailableException) : ResponseEntity<ItemNotAvailableException> {
        return ResponseEntity(e)
    }
}