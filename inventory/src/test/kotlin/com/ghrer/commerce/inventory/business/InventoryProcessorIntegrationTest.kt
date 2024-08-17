package com.ghrer.commerce.inventory.business

import com.ghrer.commerce.inventory.BaseIntegrationTest
import com.ghrer.commerce.inventory.controller.dto.ReserveItemRequest
import com.ghrer.commerce.inventory.controller.dto.UnavailableItem
import com.ghrer.commerce.inventory.exception.ItemNotFoundException
import com.ghrer.commerce.inventory.exception.NotEnoughQuantityAvailableException
import com.ghrer.commerce.inventory.fixture.ItemFixture
import com.ghrer.commerce.inventory.persistence.DataPersistenceService
import com.ghrer.commerce.inventory.persistence.ReactiveItemPersistenceService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier

class InventoryProcessorIntegrationTest : BaseIntegrationTest() {

    @Autowired
    lateinit var dataPersistenceService: DataPersistenceService

    @Autowired
    lateinit var reactiveItemPersistenceService: ReactiveItemPersistenceService

    @Autowired
    lateinit var inventoryProcessor: InventoryProcessor

    private val storedItems = listOf(
        ItemFixture.getRandomItem(),
        ItemFixture.getRandomItem(),
        ItemFixture.getRandomItem(),
    )

    @BeforeEach
    fun setup() {
        // We are not testing ReactiveItemPersistenceService behaviour, therefor, calling block() is OK
        reactiveItemPersistenceService.saveAll(storedItems).collectList().block()
    }

    @AfterEach
    fun tearDown() {
        dataPersistenceService.deleteAllItems()
    }

    @Test
    fun `should return ItemNotFoundException when an item does not exist`() {
        val notExistItem = ItemFixture.getRandomItem()
        val request = listOf(
            ReserveItemRequest(storedItems[0].id, storedItems[0].quantity),
            ReserveItemRequest(storedItems[1].id, storedItems[1].quantity),
            ReserveItemRequest(storedItems[2].id, storedItems[2].quantity),
            ReserveItemRequest(notExistItem.id, notExistItem.quantity),
        )
        StepVerifier.create(inventoryProcessor.reserve(request))
            .consumeErrorWith {
                Assertions.assertThat(it).isInstanceOf(ItemNotFoundException::class.java)
                (it as ItemNotFoundException).let { e ->
                    Assertions.assertThat(e.notFoundItems[0]).isEqualTo(notExistItem.id)
                }
            }.verify()
    }

    @Test
    fun `should return NotEnoughQuantityAvailableException when an item has not enough quantity`() {

        val request = listOf(
            ReserveItemRequest(storedItems[0].id, storedItems[0].quantity - storedItems[0].reserved),
            ReserveItemRequest(storedItems[1].id, storedItems[1].quantity + 1),
            ReserveItemRequest(storedItems[2].id, storedItems[2].quantity - storedItems[2].reserved),
        )
        StepVerifier.create(inventoryProcessor.reserve(request))
            .consumeErrorWith {
                Assertions.assertThat(it).isInstanceOf(NotEnoughQuantityAvailableException::class.java)
                (it as NotEnoughQuantityAvailableException).let { e ->
                    Assertions.assertThat(e.unavailableItems[0]).isEqualTo(
                        UnavailableItem(
                            storedItems[1].id,
                            storedItems[1].quantity + 1,
                            storedItems[1].quantity - storedItems[1].reserved
                        )
                    )
                }
            }.verify()
    }

    @Test
    fun `should successfully reserve the items`() {

        val request = listOf(
            ReserveItemRequest(storedItems[0].id, 1),
            ReserveItemRequest(storedItems[1].id, 2),
        )

        val expectedItems = listOf(
            storedItems[0].copy(reserved = storedItems[0].reserved + 1),
            storedItems[1].copy(reserved = storedItems[1].reserved + 2)
        )

        StepVerifier.create(inventoryProcessor.reserve(request))
            .consumeNextWith {
                Assertions.assertThat(expectedItems[0].reserved).isEqualTo(it[0].reserved)
                Assertions.assertThat(expectedItems[1].reserved).isEqualTo(it[1].reserved)
            }
            .verifyComplete()
    }

    @Test
    fun `should successfully commit reserved items`() {

        val request = listOf(
            ReserveItemRequest(storedItems[0].id, 1),
            ReserveItemRequest(storedItems[1].id, 2),
        )

        val expectedItems = listOf(
            storedItems[0].copy(reserved = storedItems[0].reserved - 1, quantity = storedItems[0].quantity - 1),
            storedItems[1].copy(reserved = storedItems[1].reserved - 2, quantity = storedItems[1].quantity - 2)
        )

        StepVerifier.create(inventoryProcessor.commitReservedItems(request))
            .consumeNextWith {
                Assertions.assertThat(expectedItems[0].reserved).isEqualTo(it[0].reserved)
                Assertions.assertThat(expectedItems[1].reserved).isEqualTo(it[1].reserved)
                Assertions.assertThat(expectedItems[0].quantity).isEqualTo(it[0].quantity)
                Assertions.assertThat(expectedItems[1].quantity).isEqualTo(it[1].quantity)
            }
            .verifyComplete()
    }

    @Test
    fun `should successfully dispatch items`() {

        val request = listOf(
            ReserveItemRequest(storedItems[0].id, 1),
            ReserveItemRequest(storedItems[1].id, 2),
        )

        val expectedItems = listOf(
            storedItems[0].copy(onShelf = storedItems[0].onShelf - 1),
            storedItems[1].copy(onShelf = storedItems[1].onShelf - 2)
        )

        StepVerifier.create(inventoryProcessor.dispatchItems(request))
            .consumeNextWith {
                Assertions.assertThat(expectedItems[0].onShelf).isEqualTo(it[0].onShelf)
                Assertions.assertThat(expectedItems[1].onShelf).isEqualTo(it[1].onShelf)
            }
            .verifyComplete()
    }
}
