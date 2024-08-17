package com.ghrer.commerce.payments.event

import com.ghrer.commerce.payments.event.model.OrderEvent

interface EventPublisher {
    fun publish(event: OrderEvent)
}
