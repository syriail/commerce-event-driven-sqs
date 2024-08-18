package com.ghrer.commerce.eventor.event.model

import java.util.UUID

data class OrderPaymentSuccessfulEvent(
    override val eventType: OrderEventType = OrderEventType.ORDER_PAYMENT_SUCCESSFUL,
    val orderId: UUID,
    val paymentId: UUID,
) : OrderEvent
