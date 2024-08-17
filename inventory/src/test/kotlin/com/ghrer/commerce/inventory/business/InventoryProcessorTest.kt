package com.ghrer.commerce.inventory.business

import com.ghrer.commerce.inventory.controller.dto.ReserveItemRequest
import com.ghrer.commerce.inventory.controller.dto.UnavailableItem
import com.ghrer.commerce.inventory.exception.ItemNotFoundException
import com.ghrer.commerce.inventory.exception.NotEnoughQuantityAvailableException
import com.ghrer.commerce.inventory.fixture.ItemFixture
import com.ghrer.commerce.inventory.persistence.ReactiveItemPersistenceService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.anyList
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExtendWith(MockitoExtension::class)
class InventoryProcessorTest {

    @Mock
    lateinit var reactiveItemPersistenceService: ReactiveItemPersistenceService

    @InjectMocks
    lateinit var inventoryProcessor: InventoryProcessor

    @Test
    fun `should return ItemNotFoundException when an item does not exist`() {
        val item1 = ItemFixture.getRandomItem()
        val item2 = ItemFixture.getRandomItem()
        val item3 = ItemFixture.getRandomItem()

        val request = listOf(
            ReserveItemRequest(item1.id, item1.quantity),
            ReserveItemRequest(item2.id, item2.quantity),
            ReserveItemRequest(item3.id, item3.quantity),
        )
        `when`(reactiveItemPersistenceService.findAllByIds(request.map { it.id }))
            .thenAnswer {
                Mono.just(
                    listOf(item1, item2)
                )
            }
        StepVerifier.create(inventoryProcessor.reserve(request))
            .consumeErrorWith {
                Assertions.assertThat(it).isInstanceOf(ItemNotFoundException::class.java)
                (it as ItemNotFoundException).let { e ->
                    Assertions.assertThat(e.notFoundItems).isEqualTo(listOf(item3.id))
                }
            }.verify()
    }

    @Test
    fun `should return NotEnoughQuantityAvailableException when an item has not enough quantity`() {
        val item1 = ItemFixture.getRandomItem()
        val item2 = ItemFixture.getRandomItem()
        val item3 = ItemFixture.getRandomItem()

        val request = listOf(
            ReserveItemRequest(item1.id, item1.quantity - item1.reserved),
            ReserveItemRequest(item2.id, item2.quantity + 1),
            ReserveItemRequest(item3.id, item3.quantity - item3.reserved),
        )
        `when`(reactiveItemPersistenceService.findAllByIds(request.map { it.id }))
            .thenAnswer {
                Mono.just(
                    listOf(item1, item2, item3)
                )
            }
        StepVerifier.create(inventoryProcessor.reserve(request))
            .consumeErrorWith {
                Assertions.assertThat(it).isInstanceOf(NotEnoughQuantityAvailableException::class.java)
                (it as NotEnoughQuantityAvailableException).let { e ->
                    Assertions.assertThat(e.unavailableItems[0]).isEqualTo(
                        UnavailableItem(item2.id, item2.quantity + 1, item2.quantity - item2.reserved)
                    )
                }
            }.verify()
    }

    @Test
    fun `should successfully reserve the items`() {
        val item1 = ItemFixture.getRandomItem()
        val item2 = ItemFixture.getRandomItem()

        val request = listOf(
            ReserveItemRequest(item1.id, 1),
            ReserveItemRequest(item2.id, 2),
        )
        `when`(reactiveItemPersistenceService.findAllByIds(request.map { it.id }))
            .thenAnswer {
                Mono.just(
                    listOf(item1, item2)
                )
            }

        val expectedItems = listOf(
            item1.copy(reserved = item1.reserved + 1),
            item2.copy(reserved = item2.reserved + 2)
        )

        `when`(reactiveItemPersistenceService.saveAll(anyList())).thenAnswer {
            Flux.fromIterable(expectedItems)
        }

        StepVerifier.create(inventoryProcessor.reserve(request))
            .expectNext(expectedItems)
            .verifyComplete()
    }

    @Test
    fun `should successfully commit reserved items`() {
        val item1 = ItemFixture.getRandomItem()
        val item2 = ItemFixture.getRandomItem()

        val request = listOf(
            ReserveItemRequest(item1.id, 1),
            ReserveItemRequest(item2.id, 2),
        )
        `when`(reactiveItemPersistenceService.findAllByIds(request.map { it.id }))
            .thenAnswer {
                Mono.just(
                    listOf(item1, item2)
                )
            }

        val expectedItems = listOf(
            item1.copy(reserved = item1.reserved - 1, quantity = item1.quantity - 1),
            item2.copy(reserved = item2.reserved - 2, quantity = item1.quantity - 2)
        )

        `when`(reactiveItemPersistenceService.saveAll(anyList())).thenAnswer {
            Flux.fromIterable(expectedItems)
        }
        StepVerifier.create(inventoryProcessor.commitReservedItems(request))
            .expectNext(expectedItems)
            .verifyComplete()
    }

    @Test
    fun `should successfully dispatch items`() {
        val item1 = ItemFixture.getRandomItem()
        val item2 = ItemFixture.getRandomItem()

        val request = listOf(
            ReserveItemRequest(item1.id, 1),
            ReserveItemRequest(item2.id, 2),
        )
        `when`(reactiveItemPersistenceService.findAllByIds(request.map { it.id }))
            .thenAnswer {
                Mono.just(
                    listOf(item1, item2)
                )
            }

        val expectedItems = listOf(
            item1.copy(onShelf = item1.onShelf - 1),
            item2.copy(onShelf = item2.onShelf - 2)
        )

        `when`(reactiveItemPersistenceService.saveAll(anyList())).thenAnswer {
            Flux.fromIterable(expectedItems)
        }
        StepVerifier.create(inventoryProcessor.dispatchItems(request))
            .expectNext(expectedItems)
            .verifyComplete()
    }
}
