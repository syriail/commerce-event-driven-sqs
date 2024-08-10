package com.ghrer.commerce.checkout.service.port

import com.ghrer.commerce.checkout.dto.ItemDto
import reactor.core.publisher.Mono

interface InventoryService {
    fun reserveIfAvailable(items: List<ItemDto>): Mono<List<ItemDto>>
}
