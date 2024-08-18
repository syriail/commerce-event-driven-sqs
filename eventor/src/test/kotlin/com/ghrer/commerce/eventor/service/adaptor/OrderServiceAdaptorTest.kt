package com.ghrer.commerce.eventor.service.adaptor

import com.ghrer.commerce.eventor.agent.service.adaptor.OrderServiceAdaptor
import com.ghrer.commerce.eventor.agent.service.config.OrderServiceConfig
import com.ghrer.commerce.eventor.agent.service.port.UpdateOrderStatusRequest
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
        it.statusPath = "/orders/status"
    }

    private val webClient = WebClient.create("http://localhost:$PORT")

    private val orderServiceAdaptor = OrderServiceAdaptor(
        orderServiceConfig,
        webClient,
    )

    @Test
    fun `should send update status request correctly`() {
        val orderId = UUID.randomUUID()
        WireMock.stubFor(
            WireMock.patch("/orders/status/$orderId?${OrderServiceAdaptor.STATUS_QUERY_PARAM}=${OrderStatus.PAID}")
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(HttpStatus.ACCEPTED.value())
                )
        )

        StepVerifier.create(
            orderServiceAdaptor.updateStatus(
                UpdateOrderStatusRequest(
                    orderId,
                    OrderStatus.PAID
                )
            )
        ).verifyComplete()
    }
}
