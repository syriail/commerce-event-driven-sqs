package com.ghrer.commerce.checkout.fixtures

import com.ghrer.commerce.checkout.controller.dto.PlaceOrderRequest
import com.ghrer.commerce.checkout.dto.AddressDto
import com.ghrer.commerce.checkout.dto.ItemDto
import java.util.UUID

object PlaceOrderRequestFixture {

    fun getValidPlaceOrderRequest() =
        PlaceOrderRequest(
            customerId = "joe@klerk.com",
            customerAddress = AddressDto(
                firstName = "Joe",
                lastName = "Klerk",
                street = "west",
                houseNumber = "3A",
                postCode = "1234",
                city = "East city",
            ),
            items = listOf(
                ItemDto(UUID.randomUUID(), 2, 5.6),
                ItemDto(UUID.randomUUID(), 1, 6.3),
                ItemDto(UUID.randomUUID(), 3, 12.5),
            ),
            totalPrice = 55.0
        )
}
