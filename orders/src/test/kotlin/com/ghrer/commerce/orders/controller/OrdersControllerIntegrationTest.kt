package com.ghrer.commerce.orders.controller

import com.ghrer.commerce.orders.BaseIntegrationTest
import com.ghrer.commerce.orders.model.OrderAggregate
import com.ghrer.commerce.orders.util.FileUtil
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

class OrdersControllerIntegrationTest : BaseIntegrationTest() {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Test
    fun `should create order and return HTTP 200 when request is valid`() {
        FileUtil.readJsonFileAsString("fixtures/create_order_request_valid.json")?.let { it ->
            webTestClient.post().uri("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(it)
                .exchange()
                .expectStatus().isOk
                .expectBody(OrderAggregate::class.java)
                .value { createdOrder ->
                    Assertions.assertThat(createdOrder.id).isNotNull()
                    Assertions.assertThat(createdOrder.items.size).isEqualTo(3)
                }
        }
    }

    @Test
    fun `should return HTTP 400 when createOrderRequest misses customerId`() {
        FileUtil.readJsonFileAsString("fixtures/create_order_request_missing_customerId.json")?.let {
            webTestClient.post().uri("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(it)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody()
                .jsonPath("$.message").isEqualTo("Request is not valid. Required fields are null")
        }
    }

    @Test
    fun `should return HTTP 400 when createOrderRequest has no items`() {
        FileUtil.readJsonFileAsString("fixtures/create_order_request_with_no_items.json")?.let {
            webTestClient.post().uri("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(it)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody()
                .jsonPath("$.fields.items").isEqualTo("Items cannot be empty")
        }
    }
}
