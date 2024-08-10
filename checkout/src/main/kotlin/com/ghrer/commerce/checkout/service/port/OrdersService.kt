package com.ghrer.commerce.checkout.service.port

import com.ghrer.commerce.checkout.controller.dto.PlaceOrderRequest
import com.ghrer.commerce.checkout.service.dto.CreateOrderResponse
import reactor.core.publisher.Mono

interface OrdersService {
    fun createOrder(placeOrderRequest: PlaceOrderRequest): Mono<CreateOrderResponse>
}
