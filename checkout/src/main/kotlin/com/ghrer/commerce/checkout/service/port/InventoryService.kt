package com.ghrer.commerce.checkout.service.port

import com.ghrer.commerce.checkout.dto.PlaceOrderRequestItem
import reactor.core.publisher.Mono

interface InventoryService {
    fun reserveIfAvailable(items: List<PlaceOrderRequestItem>): Mono<Void>
}
