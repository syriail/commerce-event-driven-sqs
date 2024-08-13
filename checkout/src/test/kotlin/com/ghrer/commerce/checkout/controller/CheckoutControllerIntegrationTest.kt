package com.ghrer.commerce.checkout.controller

import com.ghrer.commerce.checkout.BaseIntegrationTest
import com.ghrer.commerce.checkout.dto.OrderStatus
import com.ghrer.commerce.checkout.service.dto.CreateOrderResponse
import com.ghrer.commerce.checkout.util.FileUtil
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

class CheckoutControllerIntegrationTest : BaseIntegrationTest() {

    @Autowired
    lateinit var webTestClient: WebTestClient
    // create_order_request_item_not_found

    @Test
    fun `should return 400 when the request is invalid`() {
        FileUtil.readJsonFileAsString("fixtures/create_order_request_invalid.json")?.let {
            webTestClient.post().uri("/checkout/order")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(it)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo(ErrorCode.INVALID_FIELDS.name)
                .jsonPath("$.fields.request").value<String> { v ->
                    v.contains("parameter postCode")
                }
        }
    }

    @Test
    fun `should return 400 when order service complains about no valid fields`() {
        FileUtil.readJsonFileAsString("fixtures/create_order_request_field_not_valid.json")?.let {
            webTestClient.post().uri("/checkout/order")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(it)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo(ErrorCode.INVALID_FIELDS.name)
                .jsonPath("$.fields.myNewField").isNotEmpty
        }
    }

    @Test
    fun `should return 404 Item not found when inventory service complains about non existence of an item`() {
        FileUtil.readJsonFileAsString("fixtures/create_order_request_item_not_found.json")?.let {
            webTestClient.post().uri("/checkout/order")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(it)
                .exchange()
                .expectStatus().isNotFound
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo(ErrorCode.ITEM_NOT_FOUND.name)
                .jsonPath("$.items").isArray
                .jsonPath("$.items[0]").isNotEmpty
        }
    }

    @Test
    fun `should return 417 Item EXPECTATION_FAILED when inventory service complains about insufficient item`() {
        FileUtil.readJsonFileAsString("fixtures/create_order_request_insufficient_item.json")?.let {
            webTestClient.post().uri("/checkout/order")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(it)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.EXPECTATION_FAILED)
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo(ErrorCode.INSUFFICIENT_ITEMS.name)
                .jsonPath("$.items").isArray
                .jsonPath("$.items[0]").isNotEmpty
        }
    }

    @Test
    fun `should place order and return a valid response`() {
        FileUtil.readJsonFileAsString("fixtures/create_order_request_valid.json")?.let {
            webTestClient.post().uri("/checkout/order")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(it)
                .exchange()
                .expectStatus().isOk
                .expectBody(CreateOrderResponse::class.java)
                .value { response ->
                    Assertions.assertThat(response).isNotNull
                    Assertions.assertThat(response.items).hasSize(4)
                    Assertions.assertThat(response.status).isEqualTo(OrderStatus.PLACED)
                }
        }
    }
}
