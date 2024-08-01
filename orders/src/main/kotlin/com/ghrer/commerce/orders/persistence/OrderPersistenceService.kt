package com.ghrer.commerce.orders.persistence

import com.ghrer.commerce.orders.dto.CreateOrderRequest
import com.ghrer.commerce.orders.model.OrderAggregate
import reactor.core.publisher.Mono

interface OrderPersistenceService {

    fun createOrder(createOrderRequest: CreateOrderRequest): Mono<OrderAggregate>
}
