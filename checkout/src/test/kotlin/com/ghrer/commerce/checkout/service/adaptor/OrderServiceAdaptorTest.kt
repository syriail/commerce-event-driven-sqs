package com.ghrer.commerce.checkout.service.adaptor

import com.ghrer.commerce.checkout.exception.BadRequestException
import com.ghrer.commerce.checkout.fixtures.PlaceOrderRequestFixture
import com.ghrer.commerce.checkout.service.config.OrderServiceConfig
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier
import java.time.LocalDateTime
import java.util.UUID

private const val PORT = 4545

@WireMockTest(httpPort = PORT)
class OrderServiceAdaptorTest {

    private val orderServiceConfig = OrderServiceConfig().also {
        it.baseUrl = "http://localhost:$PORT"
        it.createOrderPath = "/orders"
    }

    private val webClient = WebClient.create()

    private val orderServiceAdaptor = OrderServiceAdaptor(
        webClient,
        orderServiceConfig
    )

    @Test
    fun `should throw BadRequest with fields when OrderService complains about empty items`() {
        val request = PlaceOrderRequestFixture.getValidPlaceOrderRequest()
        WireMock.stubFor(
            WireMock.post("/orders")
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(HttpStatus.BAD_REQUEST.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
                        .withBody(
                            """
                                {
                                    "message": "Request is not valid. See fields for details",
                                    "fields": {
                                        "items": "Items should not be empty"
                                    }
                                }
                            """.trimIndent()
                        )
                )
        )

        StepVerifier.create(orderServiceAdaptor.createOrder(request))
            .consumeErrorWith {
                Assertions.assertThat(it).isInstanceOf(BadRequestException::class.java)
                it as BadRequestException
                Assertions.assertThat(it.message).isNotNull()
                Assertions.assertThat(it.fields).hasSize(1)
                Assertions.assertThat(it.fields?.get("items")).isEqualTo("Items should not be empty")
            }.verify()
    }

    @Test
    fun `should throw BadRequest with message when OrderService complains about null fields`() {
        val request = PlaceOrderRequestFixture.getValidPlaceOrderRequest()
        WireMock.stubFor(
            WireMock.post("/orders")
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(HttpStatus.BAD_REQUEST.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
                        .withBody(
                            """
                                {
                                    "message": "Required fields are null"
                                }
                            """.trimIndent()
                        )
                )
        )

        StepVerifier.create(orderServiceAdaptor.createOrder(request))
            .consumeErrorWith {
                Assertions.assertThat(it).isInstanceOf(BadRequestException::class.java)
                it as BadRequestException
                Assertions.assertThat(it.message).isEqualTo("Required fields are null")
                Assertions.assertThat(it.fields).isNull()
            }.verify()
    }

    @Test
    fun `should return placed order when OrderService succeeded`() {
        val request = PlaceOrderRequestFixture.getValidPlaceOrderRequest()
        val createdOrderId = UUID.randomUUID()
        WireMock.stubFor(
            WireMock.post("/orders")
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
                        .withBody(
                            """
                                {
                                    "id": "$createdOrderId",
                                    "customerId": "${request.customerId}",
                                    "totalPrice": ${request.totalPrice},
                                    "customerAddress": {
                                        "firstName": "${request.customerAddress.firstName}",
                                        "lastName": "${request.customerAddress.lastName}",
                                        "street": "${request.customerAddress.street}",
                                        "houseNumber": "${request.customerAddress.houseNumber}",
                                        "postCode": "${request.customerAddress.postCode}",
                                        "city": "${request.customerAddress.city}"
                                    },
                                    "items": [
                                        {
                                            "id": "${request.items[0].id}",
                                            "quantity": ${request.items[0].quantity},
                                            "price": ${request.items[0].price}
                                        },
                                        {
                                            "id": "${request.items[1].id}",
                                            "quantity": ${request.items[1].quantity},
                                            "price": ${request.items[1].price}
                                        },
                                        {
                                            "id": "${request.items[2].id}",
                                            "quantity": ${request.items[2].quantity},
                                            "price": ${request.items[2].price}
                                        }
                                    ],
                                    "status": "PLACED",
                                    "createDate": "${LocalDateTime.now()}"
                                }
                            """.trimIndent()
                        )
                )
        )

        StepVerifier.create(orderServiceAdaptor.createOrder(request))
            .consumeNextWith {
                Assertions.assertThat(it.id).isEqualTo(createdOrderId)
                Assertions.assertThat(it.totalPrice).isEqualTo(request.totalPrice)
                Assertions.assertThat(it.items).hasSize(3)
            }.expectComplete()
            .verify()
    }
}
