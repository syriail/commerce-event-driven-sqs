package com.ghrer.commerce.orders.event.model

import com.ghrer.commerce.orders.model.OrderAggregate

data class OrderCreatedEvent(
    override val eventType: OrderEventType = OrderEventType.ORDER_CREATED,
    val order: OrderAggregate
) : OrderEvent {
    override val eventGroupId: String
        get() = order.id.toString()
}
