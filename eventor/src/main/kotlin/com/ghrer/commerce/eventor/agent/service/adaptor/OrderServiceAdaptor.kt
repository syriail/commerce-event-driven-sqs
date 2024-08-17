package com.ghrer.commerce.eventor.service.adaptor

import com.ghrer.commerce.eventor.service.port.OrderService
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class OrderServiceAdaptor(
    webClient: WebClient,
) : OrderService, AbstractServiceAdaptor(
    webClient,
) {
    override val logger = KotlinLogging.logger { }
    override val serviceName = "Order Service"
    override fun <T> handle4xxError(response: ClientResponse): Mono<T> {
        TODO("Not yet implemented")
    }
}
