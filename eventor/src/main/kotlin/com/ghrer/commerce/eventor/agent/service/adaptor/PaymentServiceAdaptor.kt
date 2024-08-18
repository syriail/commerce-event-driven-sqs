package com.ghrer.commerce.eventor.agent.service.adaptor

import com.ghrer.commerce.eventor.agent.service.config.PaymentServiceConfig
import com.ghrer.commerce.eventor.agent.service.port.PaymentService
import com.ghrer.commerce.eventor.agent.service.port.ProcessPaymentRequest
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.net.URI

@Service
class PaymentServiceAdaptor(
    private val paymentServiceConfig: PaymentServiceConfig,
    paymentServiceWebClient: WebClient
) : PaymentService, AbstractServiceAdaptor(
    paymentServiceWebClient
) {
    override val logger = KotlinLogging.logger { }
    override val serviceName = "Payments Service"

    override fun processPayment(request: ProcessPaymentRequest): Mono<Void> {
        val uri = URI.create("${paymentServiceConfig.baseUrl}${paymentServiceConfig.processPaymentPath}")
        return postRequestToMono<Void>(uri, request)
    }

    override fun <Void> handle4xxError(response: ClientResponse): Mono<Void> {
        return response.createError()
    }
}
