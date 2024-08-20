package com.ghrer.commerce.eventor.agent.service.adaptor

import com.ghrer.commerce.eventor.agent.service.config.OrderServiceConfig
import com.ghrer.commerce.eventor.agent.service.port.OrderService
import com.ghrer.commerce.eventor.agent.service.port.UpdateOrderPaymentStatusRequest
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import reactor.core.publisher.Mono
import java.net.URI
import java.util.*

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

    override fun updatePaymentStatus(request: UpdateOrderPaymentStatusRequest): Mono<Void> {
        return orderServiceWebClient.patch().uri { uriBuilder ->
            uriBuilder
                .path("${orderServiceConfig.paymentStatusPath}/${request.orderId}")
                .queryParam(STATUS_QUERY_PARAM, request.status)
                .queryParamIfPresent(PAYMENT_ID_QUERY_PARAM, Optional.ofNullable(request.paymentId))
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
        const val PAYMENT_ID_QUERY_PARAM = "paymentId"
    }
}
