package com.ghrer.commerce.checkout.service.adaptor

import com.ghrer.commerce.checkout.dto.ItemDto
import com.ghrer.commerce.checkout.exception.ItemNotFoundException
import com.ghrer.commerce.checkout.exception.NotEnoughQuantityAvailableException
import com.ghrer.commerce.checkout.service.dto.UnavailableItem
import com.ghrer.commerce.checkout.service.port.InventoryService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
@Profile("test")
class MockInventoryServiceAdaptor : InventoryService {
    override fun reserveIfAvailable(items: List<ItemDto>): Mono<List<ItemDto>> {
        return when (items.size) {
            2 -> Mono.error(simulateNotEnoughQuantityError(items))
            3 -> Mono.error(simulateItemNotFoundError(items))
            else -> Mono.just(items)
        }
    }

    private fun simulateItemNotFoundError(items: List<ItemDto>) = ItemNotFoundException(
        notFoundItems = listOf(items[0].id)
    )

    private fun simulateNotEnoughQuantityError(items: List<ItemDto>) = NotEnoughQuantityAvailableException(
        unavailableItems = listOf(
            UnavailableItem(items[0].id, items[0].quantity, 0)
        )
    )
}
