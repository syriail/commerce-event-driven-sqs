package com.ghrer.commerce.checkout.service.adaptor

import com.ghrer.commerce.checkout.dto.PlaceOrderRequest
import com.ghrer.commerce.checkout.dto.PlaceOrderResponse
import com.ghrer.commerce.checkout.service.port.OrdersService
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class OrderServiceAdaptor(
    private val webClient: WebClient
) : OrdersService {
    override fun createOrder(placeOrderRequest: PlaceOrderRequest): Mono<PlaceOrderResponse> {
        TODO("Not yet implemented")
    }
}
