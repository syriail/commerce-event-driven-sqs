package com.ghrer.commerce.eventor.event.model

import java.util.UUID

data class OrderPaymentFailedEvent(
    override val eventType: OrderEventType = OrderEventType.ORDER_PAYMENT_FAILED,
    val orderId: UUID,
    val reason: String,
) : OrderEvent
