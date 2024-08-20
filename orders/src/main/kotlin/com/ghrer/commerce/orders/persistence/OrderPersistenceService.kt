package com.ghrer.commerce.orders.persistence

import com.ghrer.commerce.orders.dto.CreateOrderRequest
import com.ghrer.commerce.orders.model.Order
import com.ghrer.commerce.orders.model.OrderAggregate
import com.ghrer.commerce.orders.model.OrderStatus
import reactor.core.publisher.Mono
import java.util.UUID

interface OrderPersistenceService {

    fun createOrder(createOrderRequest: CreateOrderRequest): Mono<OrderAggregate>

    fun updateOrderPaymentStatus(id: UUID, status: OrderStatus, paymentId: String?): Mono<Order>
}
