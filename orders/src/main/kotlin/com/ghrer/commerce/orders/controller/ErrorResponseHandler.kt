package com.ghrer.commerce.orders.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebInputException

@RestControllerAdvice
class ErrorResponseHandler {
    @ExceptionHandler(WebExchangeBindException::class)
    fun handleWebExchangeBindException(e: WebExchangeBindException): ProblemDetail {
        val fields: MutableMap<String, String> = HashMap()
        e.bindingResult.allErrors.forEach { error ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.defaultMessage ?: "Invalid value"
            fields[fieldName] = errorMessage
        }
        return ProblemDetail.forStatus(HttpStatus.BAD_REQUEST).apply {
            setProperty("message", "Some fields are not valid. See 'fields' for details")
            setProperty("fields", fields)
        }
    }

    @ExceptionHandler(ServerWebInputException::class)
    fun handleServerWebInputException(e: ServerWebInputException): ProblemDetail {
        return ProblemDetail.forStatus(HttpStatus.BAD_REQUEST).apply {
            setProperty("message", "Request is not valid. Required fields are null")
        }
    }
}
