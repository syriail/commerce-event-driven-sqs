package com.ghrer.commerce.checkout.service.port

import com.ghrer.commerce.checkout.dto.PlaceOrderRequest
import com.ghrer.commerce.checkout.dto.PlaceOrderResponse
import reactor.core.publisher.Mono

interface OrdersService {
    fun createOrder(placeOrderRequest: PlaceOrderRequest): Mono<PlaceOrderResponse>
}
