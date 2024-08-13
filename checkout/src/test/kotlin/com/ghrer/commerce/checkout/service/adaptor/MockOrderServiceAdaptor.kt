package com.ghrer.commerce.checkout.service.adaptor

import com.ghrer.commerce.checkout.controller.dto.PlaceOrderRequest
import com.ghrer.commerce.checkout.dto.OrderStatus
import com.ghrer.commerce.checkout.exception.BadRequestException
import com.ghrer.commerce.checkout.service.dto.CreateOrderResponse
import com.ghrer.commerce.checkout.service.port.OrdersService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.UUID

@Service
@Profile("test")
class MockOrderServiceAdaptor : OrdersService {
    override fun createOrder(placeOrderRequest: PlaceOrderRequest): Mono<CreateOrderResponse> {
        return when (determineErrorType(placeOrderRequest.customerId)) {
            ErrorType.NONE -> Mono.just(
                simulateOrderSuccessfullyCreated(placeOrderRequest)
            )
            ErrorType.NULL_FIELDS -> Mono.error(simulateNullFieldsError())
            ErrorType.FIELD_NOT_VALID -> Mono.error(simulateFieldNotValidError())
        }
    }

    private fun simulateOrderSuccessfullyCreated(placeOrderRequest: PlaceOrderRequest) = CreateOrderResponse(
        id = UUID.randomUUID(),
        customerId = placeOrderRequest.customerId,
        customerAddress = placeOrderRequest.customerAddress,
        totalPrice = placeOrderRequest.totalPrice,
        items = placeOrderRequest.items,
        status = OrderStatus.PLACED,
        createDate = LocalDateTime.now()
    )

    private fun simulateNullFieldsError() = BadRequestException(
        message = "Some required fields are null"
    )

    private fun simulateFieldNotValidError() = BadRequestException(
        message = "Some fields are not valid. See 'fields' for details",
        fields = mapOf(
            "myNewField" to "myNewField should not be null."
        )
    )

    private fun determineErrorType(customerId: String): ErrorType {
        val parts = customerId.split("@")
        return if ("orders" == parts[0]) {
            when (parts[1]) {
                "field.not.valid" -> ErrorType.FIELD_NOT_VALID
                "null.fields" -> ErrorType.NULL_FIELDS
                else -> ErrorType.NONE
            }
        } else ErrorType.NONE
    }

    enum class ErrorType {
        NULL_FIELDS,
        FIELD_NOT_VALID,
        NONE
    }
}
