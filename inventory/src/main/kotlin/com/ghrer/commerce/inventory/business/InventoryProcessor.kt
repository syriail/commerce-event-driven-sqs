package com.ghrer.commerce.inventory.business

import com.ghrer.commerce.inventory.controller.dto.ReserveItemRequest
import com.ghrer.commerce.inventory.model.Item
import com.ghrer.commerce.inventory.persistence.ReactiveItemPersistenceService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class InventoryProcessor(
    private val reactiveItemPersistenceService: ReactiveItemPersistenceService
) {
    fun reserve(itemsToReserve: List<ReserveItemRequest>): Mono<List<Item>> {
        val itemsToReserveMap = itemsToReserve.associateBy { it.id }

        return reactiveItemPersistenceService.findAllByIds(itemsToReserve.map { it.id })
            .map { storedItems ->
                ProcessingHelper.validateItemsToReserve(itemsToReserveMap, storedItems)
                storedItems.map { storedItem ->
                    val itemToReserve = itemsToReserveMap[storedItem.id]
                    checkNotNull(itemToReserve) { "You have a bug in InventoryHandler.reserve.. fix it :D" }
                    storedItem.copy(reserved = storedItem.reserved + itemToReserve.quantity)
                }
            }
            .flatMap { itemsToUpdate ->
                reactiveItemPersistenceService.saveAll(itemsToUpdate).collectList()
            }
    }

    fun commitReservedItems(items: List<ReserveItemRequest>): Mono<List<Item>> {
        val itemsToConfirmMap = items.associateBy { it.id }
        return reactiveItemPersistenceService.findAllByIds(items.map { it.id })
            .map { storedItems ->
                storedItems.map { storedItem ->
                    val itemToConfirm = itemsToConfirmMap[storedItem.id]
                    checkNotNull(itemToConfirm) { "You have a bug in InventoryHandler.commitReservedItems.. fix it :D" }
                    storedItem.copy(
                        reserved = storedItem.reserved - itemToConfirm.quantity,
                        quantity = storedItem.quantity - itemToConfirm.quantity
                    )
                }
            }.flatMap { itemsToUpdate ->
                reactiveItemPersistenceService.saveAll(itemsToUpdate).collectList()
            }
    }

    fun dispatchItems(items: List<ReserveItemRequest>): Mono<List<Item>> {
        val itemsToDispatch = items.associateBy { it.id }
        return reactiveItemPersistenceService.findAllByIds(items.map { it.id })
            .map { storedItems ->
                storedItems.map { storedItem ->
                    val itemToDispatch = itemsToDispatch[storedItem.id]
                    checkNotNull(itemToDispatch) { "You have a bug in InventoryHandler.dispatchItems.. fix it :D" }
                    storedItem.copy(onShelf = storedItem.onShelf - itemToDispatch.quantity)
                }
            }.flatMap { itemsToUpdate ->
                reactiveItemPersistenceService.saveAll(itemsToUpdate).collectList()
            }
    }
}
