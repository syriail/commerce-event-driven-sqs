package com.ghrer.commerce.checkout.service.adaptor

import com.ghrer.commerce.checkout.exception.ItemNotFoundException
import com.ghrer.commerce.checkout.exception.NotEnoughQuantityAvailableException
import com.ghrer.commerce.checkout.fixtures.PlaceOrderRequestFixture
import com.ghrer.commerce.checkout.service.config.InventoryServiceConfig
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
        reservePath = "/reserve"
    )

    private val webClient = WebClient.create()

    private val inventoryServiceAdaptor = InventoryServiceAdaptor(
        inventoryServiceConfig,
        webClient
    )

    @Test
    fun `should complete when receive http status 200 `() {
        val items = PlaceOrderRequestFixture.getValidPlaceOrderRequest().items
        WireMock.stubFor(
            WireMock.post("/reserve")
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
                )
        )

        StepVerifier.create(
            inventoryServiceAdaptor.reserveIfAvailable(items)
        )
            .verifyComplete()
    }

    @Test
    fun `should throw ItemNotFoundException when receive http status 404`() {
        val items = PlaceOrderRequestFixture.getValidPlaceOrderRequest().items
        WireMock.stubFor(
            WireMock.post("/reserve")
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
                        .withBody(
                            """
                                {
                                    "message": "Where the hell you got these items from?",
                                    "notFoundItems": [
                                        "${items[0].id}"
                                    ]
                                }
                            """.trimIndent()
                        )
                )
        )

        StepVerifier.create(
            inventoryServiceAdaptor.reserveIfAvailable(items)
        )
            .consumeErrorWith {
                it as ItemNotFoundException
                Assertions.assertThat(it).isNotNull()
                Assertions.assertThat(it.notFoundItems[0]).isEqualTo(items[0].id)
                Assertions.assertThat(it.message).isEqualTo("Where the hell you got these items from?")
            }.verify()
    }

    @Test
    fun `should throw NotEnoughQuantityAvailableException when receive http status 409`() {
        val items = PlaceOrderRequestFixture.getValidPlaceOrderRequest().items
        WireMock.stubFor(
            WireMock.post("/reserve")
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(HttpStatus.CONFLICT.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
                        .withBody(
                            """
                                {
                                    "message": "Sorry! Not enough items in inventory",
                                    "unavailableItems": [
                                        {
                                            "itemId": "${items[0].id}",
                                            "requestedQuantity" : ${items[0].quantity},
                                            "availableQuantity": 1
                                        }
                                    ]
                                }
                            """.trimIndent()
                        )
                )
        )

        StepVerifier.create(
            inventoryServiceAdaptor.reserveIfAvailable(items)
        )
            .consumeErrorWith {
                it as NotEnoughQuantityAvailableException
                Assertions.assertThat(it).isNotNull()
                Assertions.assertThat(it.unavailableItems[0].itemId).isEqualTo(items[0].id)
                Assertions.assertThat(it.unavailableItems[0].availableQuantity).isEqualTo(1)
                Assertions.assertThat(it.message).isEqualTo("Sorry! Not enough items in inventory")
            }.verify()
    }
}
