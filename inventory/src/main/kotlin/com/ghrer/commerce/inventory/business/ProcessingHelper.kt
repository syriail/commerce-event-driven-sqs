package com.ghrer.commerce.inventory.business

import com.ghrer.commerce.inventory.controller.dto.ReserveItemRequest
import com.ghrer.commerce.inventory.controller.dto.UnavailableItem
import com.ghrer.commerce.inventory.exception.ItemNotFoundException
import com.ghrer.commerce.inventory.exception.NotEnoughQuantityAvailableException
import com.ghrer.commerce.inventory.model.Item
import java.util.UUID

object ProcessingHelper {
    fun validateItemQuantities(itemsToReserveMap: Map<UUID, ReserveItemRequest>, storedItems: List<Item>) {
        val unavailableItems = storedItems.mapNotNull { item ->
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

    fun validateItemsExistence(itemsToReserveMap: Map<UUID, ReserveItemRequest>, storedItems: List<Item>) {
        if (itemsToReserveMap.size > storedItems.size) {
            val notFoundItems = itemsToReserveMap.keys.filter { id -> storedItems.none { it.id == id } }
            throw ItemNotFoundException(notFoundItems = notFoundItems)
        }
    }

    fun validateItemsToReserve(
        itemsToReserveMap: Map<UUID, ReserveItemRequest>,
        storedItems: List<Item>
    ) {
        validateItemsExistence(itemsToReserveMap, storedItems)
        validateItemQuantities(itemsToReserveMap, storedItems)
    }
}
