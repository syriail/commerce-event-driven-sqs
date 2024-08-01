package com.ghrer.commerce.orders.fixture

import com.ghrer.commerce.orders.dto.CreateOrderRequest
import com.ghrer.commerce.orders.dto.OrderRequestAddress
import com.ghrer.commerce.orders.dto.OrderRequestOrderItem
import com.ghrer.commerce.orders.model.Address
import com.ghrer.commerce.orders.model.OrderAggregate
import com.ghrer.commerce.orders.model.OrderItem
import com.ghrer.commerce.orders.model.OrderStatus
import java.time.LocalDateTime
import java.util.UUID

object OrderFixture {

    fun getSampleCreateOrderRequest() = CreateOrderRequest(
        customerId = "some@hello.com",
        totalPrice = 32.4,
        customerAddress = OrderRequestAddress(
            firstName = "Hussein",
            lastName = "Ghrer",
            street = "some where str.",
            houseNumber = "43B",
            postalCode = "4320",
            city = "nice city"
        ),
        items = listOf(
            OrderRequestOrderItem(UUID.randomUUID(), 3, 1.2),
            OrderRequestOrderItem(UUID.randomUUID(), 1, 12.0),
            OrderRequestOrderItem(UUID.randomUUID(), 2, 4.7),
        )
    )

    fun getCreatedOrderAggregate(): OrderAggregate {
        val orderId = UUID.randomUUID()
        return OrderAggregate(
            id = orderId,
            customerId = "some@hello.com",
            totalPrice = 32.4,
            address = Address(
                firstName = "Hussein",
                lastName = "Ghrer",
                street = "some where str.",
                houseNumber = "43B",
                postalCode = "4320",
                city = "nice city"
            ),
            status = OrderStatus.PLACED,
            createDate = LocalDateTime.now(),
            items = listOf(
                OrderItem(
                    id = UUID.randomUUID(),
                    orderId = orderId,
                    quantity = 3,
                    price = 1.2
                ),
                OrderItem(
                    id = UUID.randomUUID(),
                    orderId = orderId,
                    quantity = 1,
                    price = 12.0
                )
            )
        )
    }

    fun getCreatedOrderAggregate(request: CreateOrderRequest): OrderAggregate {
        val orderId = UUID.randomUUID()
        return OrderAggregate(
            id = orderId,
            customerId = request.customerId,
            totalPrice = request.totalPrice,
            address = Address(
                firstName = request.customerAddress.firstName,
                lastName = request.customerAddress.lastName,
                street = request.customerAddress.street,
                houseNumber = request.customerAddress.houseNumber,
                postalCode = request.customerAddress.postalCode,
                city = request.customerAddress.city
            ),
            status = OrderStatus.PLACED,
            createDate = LocalDateTime.now(),
            items = request.items.map {
                OrderItem(
                    id = it.id,
                    price = it.price,
                    quantity = it.quantity,
                    orderId = orderId
                )
            }
        )
    }
}
