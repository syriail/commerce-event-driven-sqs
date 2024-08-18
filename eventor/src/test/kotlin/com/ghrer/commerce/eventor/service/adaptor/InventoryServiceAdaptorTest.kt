package com.ghrer.commerce.eventor.service.adaptor

import com.ghrer.commerce.eventor.agent.service.adaptor.InventoryServiceAdaptor
import com.ghrer.commerce.eventor.agent.service.config.InventoryServiceConfig
import com.ghrer.commerce.eventor.exception.ServiceUnavailableException
import com.ghrer.commerce.eventor.fixture.OrderCreatedEventFixture
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier

private const val PORT = 4545
@WireMockTest(httpPort = PORT)
class InventoryServiceAdaptorTest {

    private val inventoryServiceConfig = InventoryServiceConfig(
        baseUrl = "http://localhost:$PORT",
        commitReservedPath = "/commit-reserved"
    )

    private val webClient = WebClient.create()

    private val inventoryServiceAdaptor = InventoryServiceAdaptor(
        inventoryServiceConfig,
        webClient
    )
    @Test
    fun `should not throw exception when receive http status 200 `() {
        val items = OrderCreatedEventFixture.getRandomOrderCreatedEvent().order.items
        WireMock.stubFor(
            WireMock.post("/commit-reserved")
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
                )
        )

        StepVerifier.create(inventoryServiceAdaptor.commitItemsReservation(items))
            .verifyComplete()

        Assertions.assertThatNoException().isThrownBy {
            inventoryServiceAdaptor.commitItemsReservation(items).block()
        }
    }

    @Test
    fun `should throw ServiceUnavailableException when receive http status 503`() {
        val items = OrderCreatedEventFixture.getRandomOrderCreatedEvent().order.items
        WireMock.stubFor(
            WireMock.post("/commit-reserved")
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(HttpStatus.SERVICE_UNAVAILABLE.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
                )
        )

        StepVerifier.create(inventoryServiceAdaptor.commitItemsReservation(items))
            .consumeErrorWith {
                Assertions.assertThat(it).isInstanceOf(ServiceUnavailableException::class.java)
            }.verify()

        Assertions.assertThatThrownBy {
            inventoryServiceAdaptor.commitItemsReservation(items).block()
        }.isInstanceOf(ServiceUnavailableException::class.java)
    }
}
