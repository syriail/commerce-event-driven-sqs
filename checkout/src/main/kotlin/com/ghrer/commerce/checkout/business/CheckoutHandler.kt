package com.ghrer.commerce.checkout.business

import com.ghrer.commerce.checkout.controller.dto.PlaceOrderRequest
import com.ghrer.commerce.checkout.controller.dto.PlaceOrderResponse
import reactor.core.publisher.Mono

interface CheckoutHandler {
    fun placeOrder(placeOrderRequest: PlaceOrderRequest): Mono<PlaceOrderResponse>
}
