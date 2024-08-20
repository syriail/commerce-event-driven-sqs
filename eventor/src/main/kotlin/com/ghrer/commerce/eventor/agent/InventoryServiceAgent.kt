package com.ghrer.commerce.eventor.agent

import com.ghrer.commerce.eventor.agent.service.port.InventoryService
import com.ghrer.commerce.eventor.event.model.OrderCreatedEvent
import mu.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class InventoryServiceAgent(
    private val inventoryService: InventoryService
) {
    val logger = KotlinLogging.logger { }
    @EventListener
    fun on(orderCreatedEvent: OrderCreatedEvent) {
        logger.info { "Commit reserved items for order ${orderCreatedEvent.order.id}" }
        runCatching {
            inventoryService.commitItemsReservation(orderCreatedEvent.order.items).block()
        }.onFailure {
            logger.error(it) { "Error while processing OrderCreatedEvent ${orderCreatedEvent.order.id}" }
        }
    }
}
