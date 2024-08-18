package com.ghrer.commerce.eventor.agent.service.port

import com.ghrer.commerce.eventor.model.Item
import reactor.core.publisher.Mono

interface InventoryService {
    fun commitItemsReservation(items: List<Item>): Mono<Void>
}
