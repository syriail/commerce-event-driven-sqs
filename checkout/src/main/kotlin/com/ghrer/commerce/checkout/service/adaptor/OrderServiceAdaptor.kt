package com.ghrer.commerce.checkout.service.adaptor

import com.ghrer.commerce.checkout.controller.dto.PlaceOrderRequest
import com.ghrer.commerce.checkout.exception.BadRequestException
import com.ghrer.commerce.checkout.service.config.OrderServiceConfig
import com.ghrer.commerce.checkout.service.dto.CreateOrderResponse
import com.ghrer.commerce.checkout.service.port.OrdersService
import mu.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.net.URI

@Service
@Profile("!test")
class OrderServiceAdaptor(
    webClient: WebClient,
    private val orderServiceConfig: OrderServiceConfig,
) : OrdersService, AbstractServiceAdaptor(
    webClient
) {
    override val logger = KotlinLogging.logger { }
    override val serviceName = "Order Service"
    override fun createOrder(placeOrderRequest: PlaceOrderRequest): Mono<CreateOrderResponse> {
        val uri = URI.create("${orderServiceConfig.baseUrl}${orderServiceConfig.createOrderPath}")
        return postRequestToMono<CreateOrderResponse>(uri, placeOrderRequest)
    }

    override fun <Void> handle4xxError(response: ClientResponse): Mono<Void> {
        return when (response.statusCode()) {
            HttpStatus.BAD_REQUEST -> {
                response.bodyToMono(BadRequestException::class.java).flatMap {
                    Mono.error(it)
                }
            }
            else -> response.createError()
        }
    }
}
