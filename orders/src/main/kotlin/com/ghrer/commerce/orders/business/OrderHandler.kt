package com.ghrer.commerce.orders.business

import com.ghrer.commerce.orders.dto.CreateOrderRequest
import com.ghrer.commerce.orders.event.EventPublisher
import com.ghrer.commerce.orders.event.model.OrderCreatedEvent
import com.ghrer.commerce.orders.model.OrderAggregate
import com.ghrer.commerce.orders.persistence.OrderPersistenceService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class OrderHandler(
    private val orderPersistenceService: OrderPersistenceService,
    private val eventPublisher: EventPublisher,
) {
    fun createOrder(createOrderRequest: CreateOrderRequest): Mono<OrderAggregate> {
        return orderPersistenceService.createOrder(createOrderRequest)
            .doOnNext {
                eventPublisher.publish(
                    OrderCreatedEvent(order = it)
                )
            }
    }
}
