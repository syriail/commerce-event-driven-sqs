package com.ghrer.commerce.payments.event.model

import java.util.UUID

data class OrderPaymentSuccessfulEvent(
    override val eventType: OrderEventType = OrderEventType.ORDER_PAYMENT_SUCCESSFUL,
    val orderId: UUID,
    val paymentId: UUID,
) : OrderEvent {
    override val eventGroupId: String
        get() = orderId.toString()
}
