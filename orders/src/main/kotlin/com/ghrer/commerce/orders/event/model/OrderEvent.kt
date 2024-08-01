package com.ghrer.commerce.orders.event.model

enum class OrderEventType {
    ORDER_CREATED,
}
interface OrderEvent {
    val eventType: OrderEventType
    val eventGroupId: String
}
