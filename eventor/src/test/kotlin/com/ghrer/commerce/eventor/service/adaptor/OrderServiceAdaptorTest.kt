package com.ghrer.commerce.eventor.service.adaptor

import com.ghrer.commerce.eventor.agent.service.adaptor.OrderServiceAdaptor
import com.ghrer.commerce.eventor.agent.service.config.OrderServiceConfig
import com.ghrer.commerce.eventor.agent.service.port.UpdateOrderPaymentStatusRequest
import com.ghrer.commerce.eventor.model.OrderStatus
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier
import java.util.UUID

private const val PORT = 4545

@WireMockTest(httpPort = PORT)
class OrderServiceAdaptorTest {

    private val orderServiceConfig = OrderServiceConfig().also {
        it.baseUrl = "http://localhost:$PORT"
        it.paymentStatusPath = "/orders/payment/status"
    }

    private val webClient = WebClient.create("http://localhost:$PORT")

    private val orderServiceAdaptor = OrderServiceAdaptor(
        orderServiceConfig,
        webClient,
    )

    @Test
    fun `should send update payment status request with paymentId correctly`() {
        val orderId = UUID.randomUUID()
        val paymentId = UUID.randomUUID()
        val queryString = "${OrderServiceAdaptor.STATUS_QUERY_PARAM}=${OrderStatus.PAID}&${OrderServiceAdaptor.PAYMENT_ID_QUERY_PARAM}=$paymentId"
        WireMock.stubFor(
            WireMock.patch("/orders/payment/status/$orderId?$queryString")
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(HttpStatus.ACCEPTED.value())
                )
        )

        StepVerifier.create(
            orderServiceAdaptor.updatePaymentStatus(
                UpdateOrderPaymentStatusRequest(
                    orderId,
                    OrderStatus.PAID,
                    paymentId = paymentId.toString()
                )
            )
        ).verifyComplete()
    }

    @Test
    fun `should send update payment status request without paymentId correctly`() {
        val orderId = UUID.randomUUID()
        val queryString = "${OrderServiceAdaptor.STATUS_QUERY_PARAM}=${OrderStatus.PAYMENT_FAILED}"
        WireMock.stubFor(
            WireMock.patch("/orders/payment/status/$orderId?$queryString")
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(HttpStatus.ACCEPTED.value())
                )
        )

        StepVerifier.create(
            orderServiceAdaptor.updatePaymentStatus(
                UpdateOrderPaymentStatusRequest(
                    orderId,
                    OrderStatus.PAYMENT_FAILED,
                )
            )
        ).verifyComplete()
    }
}
