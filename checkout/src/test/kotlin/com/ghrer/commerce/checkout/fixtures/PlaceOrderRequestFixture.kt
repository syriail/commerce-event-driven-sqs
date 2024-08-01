package com.ghrer.commerce.checkout.fixtures

import com.ghrer.commerce.checkout.dto.AddressDto
import com.ghrer.commerce.checkout.dto.CustomerDto
import com.ghrer.commerce.checkout.dto.PlaceOrderRequest
import com.ghrer.commerce.checkout.dto.PlaceOrderRequestItem
import java.util.*

object PlaceOrderRequest {

    fun getValidPlaceOrderRequest() =
        PlaceOrderRequest(
            customer = CustomerDto(
                email = "joe@klerk.com",
                firstName = "Joe",
                lastName = "Klerk",
            ),
            invoiceAddress = AddressDto(
                firstName = "Joe",
                lastName = "Klerk",
                street = "west",
                houseNumber = "3A",
                postCode = "1234",
                city = "East city",
                country = "Independent State"
            ),
            shippingAddress = AddressDto(
                firstName = "Joe",
                lastName = "Klerk",
                street = "west",
                houseNumber = "3A",
                postCode = "1234",
                city = "East city",
                country = "Independent State"
            ),
            items = listOf(
                PlaceOrderRequestItem(UUID.randomUUID(), 2),
                PlaceOrderRequestItem(UUID.randomUUID(), 1),
                PlaceOrderRequestItem(UUID.randomUUID(), 3),
            )
        )
}