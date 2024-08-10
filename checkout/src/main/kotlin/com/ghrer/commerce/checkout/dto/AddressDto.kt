package com.ghrer.commerce.checkout.dto

data class AddressDto(
    val firstName: String,
    val lastName: String,
    val street: String,
    val houseNumber: String,
    val postCode: String,
    val city: String,
)
