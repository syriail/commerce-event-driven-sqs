package com.ghrer.commerce.eventor.service.port

import com.ghrer.commerce.eventor.model.Item
import reactor.core.publisher.Mono

interface InventoryService {
    fun commitItemsReservation(items: List<Item>): Mono<Void>
}
