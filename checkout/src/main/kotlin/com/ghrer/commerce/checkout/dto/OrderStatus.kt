package com.ghrer.commerce.checkout.dto

import com.fasterxml.jackson.annotation.JsonFormat

@JsonFormat(shape = JsonFormat.Shape.STRING)
enum class OrderStatus {
    PLACED
}
