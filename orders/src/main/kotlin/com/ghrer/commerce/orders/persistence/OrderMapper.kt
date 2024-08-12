package com.ghrer.commerce.orders.persistence

import com.ghrer.commerce.orders.dto.CreateOrderRequest
import com.ghrer.commerce.orders.model.Address
import com.ghrer.commerce.orders.model.Item
import com.ghrer.commerce.orders.model.Order
import com.ghrer.commerce.orders.model.OrderAggregate
import com.ghrer.commerce.orders.model.OrderItem
import com.ghrer.commerce.orders.model.OrderItemId
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class OrderMapper {
    fun mapToOrder(createOrderRequest: CreateOrderRequest) = Order(
        customerId = createOrderRequest.customerId,
        totalPrice = createOrderRequest.totalPrice,
        address = createOrderRequest.customerAddress
            .let {
                Address(
                    it.firstName,
                    it.lastName,
                    it.street,
                    it.houseNumber,
                    it.postCode,
                    it.city
                )
            },
    )

    fun mapToOrderItems(createOrderRequest: CreateOrderRequest, orderId: UUID) = createOrderRequest.items.map {
        OrderItem(
            OrderItemId(id = it.id, orderId = orderId),
            quantity = it.quantity,
            price = it.price
        )
    }

    fun mapToOrderAggregate(order: Order, items: List<OrderItem>) = OrderAggregate(
        id = order.id,
        customerId = order.customerId,
        paymentId = order.paymentId,
        shipmentId = order.shipmentId,
        status = order.status,
        createDate = order.createDate!!,
        items = items.map {
            Item(it.orderItemId.id, it.quantity, it.price)
        },
        totalPrice = order.totalPrice,
        customerAddress = order.address
    )
}
