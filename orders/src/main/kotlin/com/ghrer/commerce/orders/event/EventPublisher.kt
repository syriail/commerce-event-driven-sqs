package com.ghrer.commerce.orders.event

import com.ghrer.commerce.orders.event.model.OrderEvent

interface EventPublisher {
    fun publish(event: OrderEvent)
}
