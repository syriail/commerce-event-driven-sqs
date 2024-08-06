package com.ghrer.commerce.inventory.business

import com.ghrer.commerce.inventory.controller.dto.ReserveItemRequest
import com.ghrer.commerce.inventory.controller.dto.UnavailableItem
import com.ghrer.commerce.inventory.exception.ItemNotFoundException
import com.ghrer.commerce.inventory.exception.NotEnoughQuantityAvailableException
import com.ghrer.commerce.inventory.model.Item
import com.ghrer.commerce.inventory.persistence.ReactiveItemPersistenceService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.util.UUID

@Service
class InventoryHandler(
    private val reactiveItemPersistenceService: ReactiveItemPersistenceService
) {
    fun reserve(itemsToReserve: List<ReserveItemRequest>): Flux<Item> {
        val itemsToReserveMap = itemsToReserve.associateBy { it.itemId }

        return reactiveItemPersistenceService.findAllByIds(itemsToReserve.map { it.itemId })
            .map { itemsToUpdate ->
                validateItemsToReserve(itemsToReserveMap, itemsToUpdate)
                itemsToUpdate.map { item ->
                    val itemToReserve = itemsToReserveMap[item.id]
                    checkNotNull(itemToReserve) { "You have a bug in InventoryHandler.reserve.. fix it :D" }
                    item.copy(reserved = item.reserved + itemToReserve.quantity)
                }
            }
            .flatMapMany { itemsToUpdate ->
                reactiveItemPersistenceService.saveAll(itemsToUpdate)
            }
    }

    private fun validateItemsToReserve(
        itemsToReserveMap: Map<UUID, ReserveItemRequest>,
        itemsToUpdate: List<Item>
    ) {
        validateItemsExistence(itemsToReserveMap, itemsToUpdate)
        validateItemQuantities(itemsToReserveMap, itemsToUpdate)
    }

    private fun validateItemsExistence(itemsToReserveMap: Map<UUID, ReserveItemRequest>, itemsToUpdate: List<Item>) {
        if (itemsToReserveMap.size > itemsToUpdate.size) {
            val notFoundItems = itemsToReserveMap.keys.filter { id -> itemsToUpdate.none { it.id == id } }
            throw ItemNotFoundException(notFoundItems = notFoundItems)
        }
    }

    private fun validateItemQuantities(itemsToReserveMap: Map<UUID, ReserveItemRequest>, itemsToUpdate: List<Item>) {
        val unavailableItems = itemsToUpdate.mapNotNull { item ->
            val itemToReserve = itemsToReserveMap[item.id]
            checkNotNull(itemToReserve) { "You have a bug in validateItemQuantities.reserve.. fix it :D" }
            val availableQuantity = item.quantity - item.reserved
            if (itemToReserve.quantity > availableQuantity) {
                UnavailableItem(item.id, itemToReserve.quantity, availableQuantity)
            } else {
                null
            }
        }
        if (unavailableItems.isNotEmpty())
            throw NotEnoughQuantityAvailableException(unavailableItems = unavailableItems)
    }
}
