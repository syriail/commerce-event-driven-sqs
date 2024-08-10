package com.ghrer.commerce.checkout.controller

import com.ghrer.commerce.checkout.exception.BadRequestException
import com.ghrer.commerce.checkout.exception.ItemNotFoundException
import com.ghrer.commerce.checkout.exception.NotEnoughQuantityAvailableException
import org.springframework.core.codec.DecodingException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebInputException

@RestControllerAdvice
class ErrorResponseHandler {

    @ExceptionHandler(NotEnoughQuantityAvailableException::class)
    fun handleItemNotAvailableException(e: NotEnoughQuantityAvailableException): ProblemDetail {
        return ProblemDetail.forStatus(HttpStatus.EXPECTATION_FAILED).apply {
            setProperty("items", e.unavailableItems)
            setProperty(ERROR_CODE, ErrorCode.INSUFFICIENT_ITEMS)
        }
    }

    @ExceptionHandler(ItemNotFoundException::class)
    fun handleItemNotFoundException(e: ItemNotFoundException): ProblemDetail {
        return ProblemDetail.forStatus(HttpStatus.NOT_FOUND).apply {
            setProperty("items", e.notFoundItems)
            setProperty(ERROR_CODE, ErrorCode.ITEM_NOT_FOUND)
        }
    }
// org.springframework.core.codec.DecodingException
    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(e: BadRequestException): ProblemDetail {
        return ProblemDetail.forStatus(HttpStatus.BAD_REQUEST).apply {
            setProperty("fields", e.fields)
            setProperty(ERROR_CODE, ErrorCode.INVALID_FIELDS)
        }
    }

    @ExceptionHandler(ServerWebInputException::class)
    fun handleServerWebInputException(e: ServerWebInputException): ProblemDetail {
        return (e.cause as? DecodingException)?.let {
            ProblemDetail.forStatus(HttpStatus.BAD_REQUEST).apply {
                setProperty(ERROR_CODE, ErrorCode.INVALID_FIELDS)
                setProperty(
                    "fields",
                    mapOf(
                        "request" to it.message
                    )
                )
                // JSON decoding error: Cannot construct instance of `com.ghrer.commerce.checkout.dto.AddressDto`, problem: Parameter specified as non-null is null: method com.ghrer.commerce.checkout.dto.AddressDto.<init>, parameter postCode
            }
        } ?: ProblemDetail.forStatus(HttpStatus.BAD_REQUEST).apply {
            setProperty(ERROR_CODE, ErrorCode.INVALID_FIELDS)
            setProperty("details", e.message)
        }
    }

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
            setProperty(ERROR_CODE, ErrorCode.INVALID_FIELDS)
            setProperty("fields", fields)
        }
    }
    companion object {
        const val ERROR_CODE = "errorCode"
    }
}

enum class ErrorCode {
    INVALID_FIELDS,
    INSUFFICIENT_ITEMS,
    ITEM_NOT_FOUND
}
