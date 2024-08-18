package com.ghrer.commerce.eventor.fixture

import com.ghrer.commerce.eventor.event.model.OrderCreatedEvent
import com.ghrer.commerce.eventor.model.Address
import com.ghrer.commerce.eventor.model.Item
import com.ghrer.commerce.eventor.model.Order
import com.ghrer.commerce.eventor.model.OrderStatus
import java.time.LocalDateTime
import java.util.UUID

object OrderCreatedEventFixture {
    fun getRandomOrderCreatedEvent() = OrderCreatedEvent(
        order = Order(
            id = UUID.randomUUID(),
            customerId = "testing@some.com",
            customerAddress = Address(
                firstName = "Hussein",
                lastName = "Ghrer",
                street = "Open Street",
                houseNumber = "3",
                postCode = "34",
                city = "Open city"
            ),
            status = OrderStatus.PLACED,
            createDate = LocalDateTime.now(),
            totalPrice = 32.6,
            items = listOf(
                Item(
                    id = UUID.randomUUID(),
                    quantity = 3,
                    price = 4.3
                ),
                Item(
                    id = UUID.randomUUID(),
                    quantity = 2,
                    price = 45.2
                )
            )
        )
    )
}
