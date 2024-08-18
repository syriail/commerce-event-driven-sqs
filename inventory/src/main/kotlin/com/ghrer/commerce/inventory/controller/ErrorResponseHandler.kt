package com.ghrer.commerce.inventory.controller

import com.ghrer.commerce.inventory.exception.ItemNotFoundException
import com.ghrer.commerce.inventory.exception.NotEnoughQuantityAvailableException
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException

@RestControllerAdvice
class ErrorResponseHandler {

    val logger = KotlinLogging.logger { }
    @ExceptionHandler(WebExchangeBindException::class)
    fun handleWebExchangeBindException(e: WebExchangeBindException): ProblemDetail {
        val errors: MutableMap<String, String> = HashMap()
        e.bindingResult.allErrors.forEach { error ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.defaultMessage ?: "Invalid value"
            errors[fieldName] = errorMessage
        }
        return ProblemDetail.forStatus(HttpStatus.BAD_REQUEST).apply {
            setProperty("errors", errors)
        }
    }

    @ExceptionHandler(NotEnoughQuantityAvailableException::class)
    fun handleNotEnoughQuantityAvailableException(e: NotEnoughQuantityAvailableException): ProblemDetail {
        return ProblemDetail.forStatus(HttpStatus.CONFLICT).apply {
            setProperty("message", e.message ?: "At least one item has not enough quantity to reserve. Check unavailableItems")
            setProperty("unavailableItems", e.unavailableItems)
        }
    }

    @ExceptionHandler(ItemNotFoundException::class)
    fun handleItemNotFoundException(e: ItemNotFoundException): ProblemDetail {
        return ProblemDetail.forStatus(HttpStatus.NOT_FOUND).apply {
            setProperty("message", e.message ?: "At least one item is not found. Check notFoundItems")
            setProperty("notFoundItems", e.notFoundItems)
        }
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleRunTimeException(e: RuntimeException): ProblemDetail {
        logger.error(e) { e.message }
        return ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR).apply {
            setProperty("message", e.message ?: "Internal Server Exception")
        }
    }
}
