package com.ghrer.commerce.eventor.event.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.ghrer.commerce.eventor.model.Order

@JsonIgnoreProperties(ignoreUnknown = true)
data class OrderCreatedEvent(
    override val eventType: OrderEventType = OrderEventType.ORDER_CREATED,
    val order: Order
) : OrderEvent
