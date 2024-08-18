package com.ghrer.commerce.eventor.agent.service.adaptor

import com.ghrer.commerce.eventor.agent.service.config.OrderServiceConfig
import com.ghrer.commerce.eventor.agent.service.port.OrderService
import com.ghrer.commerce.eventor.agent.service.port.UpdateOrderStatusRequest
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class OrderServiceAdaptor @Autowired constructor(
    private val orderServiceConfig: OrderServiceConfig,
    @Autowired @Qualifier("orderServiceWebClient")
    private val orderServiceWebClient: WebClient,
) : OrderService, AbstractServiceAdaptor(
    orderServiceWebClient,
) {
    override val logger = KotlinLogging.logger { }
    override val serviceName = "Order Service"

    override fun updateStatus(request: UpdateOrderStatusRequest): Mono<Void> {
        return orderServiceWebClient.patch().uri { uriBuilder ->
            uriBuilder
                .path("${orderServiceConfig.statusPath}/${request.orderId}")
                .queryParam(STATUS_QUERY_PARAM, request.status)
                .build()
        }
            .contentLength(0)
            .exchangeToMono { response ->
                if (response.statusCode().is2xxSuccessful) response.bodyToMono(Void::class.java)
                else if (response.statusCode().is4xxClientError) handle4xxError(response)
                else Mono.error(handle5xxError(response))
            }
    }

    override fun <T> handle4xxError(response: ClientResponse): Mono<T> {
        return response.createError()
    }

    companion object {
        const val STATUS_QUERY_PARAM = "status"
    }
}
