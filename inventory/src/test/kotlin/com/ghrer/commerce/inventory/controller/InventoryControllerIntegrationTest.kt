package com.ghrer.commerce.inventory.controller

import com.ghrer.commerce.inventory.BaseIntegrationTest
import com.ghrer.commerce.inventory.controller.dto.UnavailableItem
import com.ghrer.commerce.inventory.exception.ItemNotFoundException
import com.ghrer.commerce.inventory.exception.NotEnoughQuantityAvailableException
import com.ghrer.commerce.inventory.fixture.ItemFixture
import com.ghrer.commerce.inventory.model.Item
import com.ghrer.commerce.inventory.persistence.DataPersistenceService
import com.ghrer.commerce.inventory.persistence.ReactiveItemPersistenceService
import com.ghrer.commerce.inventory.utils.FileUtil
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.UUID

class InventoryControllerIntegrationTest : BaseIntegrationTest() {

    @Autowired
    lateinit var itemPersistenceService: ReactiveItemPersistenceService

    @Autowired
    lateinit var dataPersistenceService: DataPersistenceService

    @Autowired
    lateinit var webTestClient: WebTestClient

    private val storedItems = listOf(
        ItemFixture.getRandomItem().copy(id = UUID.fromString("31c5e774-a0de-4af7-a296-da791f01cade")),
        ItemFixture.getRandomItem().copy(id = UUID.fromString("a49c3010-8cbe-4d35-8bb0-93ce1ec6bb1d")),
        ItemFixture.getRandomItem().copy(id = UUID.fromString("58eb6db8-9117-4283-b2d7-c79c9cbc1dc5")),
    )

    @BeforeEach
    fun setup() {
        itemPersistenceService.saveAll(storedItems).collectList().block()
    }

    @AfterEach
    fun tearDown() {
        dataPersistenceService.deleteAllItems()
    }

    @Test
    fun `should return 404 with list of not found items`() {
        FileUtil.readJsonFileAsString("fixtures/reserve_request_with_not_exist_item.json")?.let {
            webTestClient.post().uri("/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(it)
                .exchange()
                .expectStatus().isNotFound
                .expectBody(ItemNotFoundException::class.java)
                .value { e ->
                    Assertions.assertThat(e.notFoundItems).hasSize(1)
                    Assertions.assertThat(e.notFoundItems[0].toString()).isEqualTo("58eb6db8-9117-4283-b2d7-c79c9cbc1dc6")
                }
        }
    }

    @Test
    fun `should return 409 with list of not enough items`() {
        val expectedUnavailableItems = listOf(
            UnavailableItem(
                storedItems[1].id,
                11,
                storedItems[1].quantity - storedItems[1].reserved
            ),
            UnavailableItem(
                storedItems[2].id,
                11,
                storedItems[2].quantity - storedItems[2].reserved
            ),
        )
        FileUtil.readJsonFileAsString("fixtures/reserve_request_not_enough.json")?.let {
            webTestClient.post().uri("/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(it)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody(NotEnoughQuantityAvailableException::class.java)
                .value { e ->
                    Assertions.assertThat(e.unavailableItems).hasSize(2)
                    Assertions.assertThat(e.unavailableItems).contains(*expectedUnavailableItems.toTypedArray())
                }
        }
    }

    @Test
    fun `should successfully reserve the items`() {
        FileUtil.readJsonFileAsString("fixtures/reserve_request_valid.json")?.let {
            webTestClient.post().uri("/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(it)
                .exchange()
                .expectStatus().isOk
                .expectBodyList(Item::class.java)
                .hasSize(3)
                .value<WebTestClient.ListBodySpec<Item>> { items ->
                    items.forEachIndexed { index, item ->
                        Assertions.assertThat(item.id).isEqualTo(storedItems[index].id)
                        Assertions.assertThat(item.reserved).isEqualTo(storedItems[index].reserved + 1)
                    }
                }
        }
    }
}
