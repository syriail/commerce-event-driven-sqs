package com.ghrer.commerce.orders.controller

import com.ghrer.commerce.orders.business.OrderHandler
import com.ghrer.commerce.orders.dto.CreateOrderRequest
import com.ghrer.commerce.orders.model.OrderStatus
import jakarta.validation.Valid
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/orders")
@Validated
class OrdersController(
    private val orderHandler: OrderHandler
) {

    val logger = KotlinLogging.logger { }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    fun createOrder(
        @Valid @RequestBody createOrderRequest: CreateOrderRequest
    ) = orderHandler.createOrder(createOrderRequest)

    @PatchMapping("/status/{orderId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun updateOrderStatus(
        @PathVariable orderId: UUID,
        @RequestParam status: OrderStatus,
    ) = orderHandler.updateOrderStatus(orderId, status)
}
