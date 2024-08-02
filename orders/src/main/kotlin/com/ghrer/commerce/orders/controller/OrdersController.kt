package com.ghrer.commerce.orders.controller

import com.ghrer.commerce.orders.business.OrderHandler
import com.ghrer.commerce.orders.dto.CreateOrderRequest
import com.ghrer.commerce.orders.model.OrderAggregate
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/orders")
@Validated
class OrdersController(
    private val orderHandler: OrderHandler
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createOrder(
        @Valid @RequestBody createOrderRequest: CreateOrderRequest
    ): Mono<OrderAggregate> = orderHandler.createOrder(createOrderRequest)
}
