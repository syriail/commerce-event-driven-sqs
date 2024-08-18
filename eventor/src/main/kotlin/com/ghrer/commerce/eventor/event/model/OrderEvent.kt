package com.ghrer.commerce.eventor.event.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

enum class OrderEventType {
    ORDER_CREATED,
    ORDER_PAYMENT_SUCCESSFUL,
    ORDER_PAYMENT_FAILED,
}
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "eventType")
@JsonSubTypes(
    JsonSubTypes.Type(value = OrderCreatedEvent::class, name = "ORDER_CREATED"),
    JsonSubTypes.Type(value = OrderPaymentSuccessfulEvent::class, name = "ORDER_PAYMENT_SUCCESSFUL"),
    JsonSubTypes.Type(value = OrderPaymentFailedEvent::class, name = "ORDER_PAYMENT_FAILED"),
)
interface OrderEvent {
    val eventType: OrderEventType
}
