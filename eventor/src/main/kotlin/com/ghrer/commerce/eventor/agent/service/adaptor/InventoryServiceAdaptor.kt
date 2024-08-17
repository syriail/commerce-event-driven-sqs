package com.ghrer.commerce.eventor.service.adaptor

import com.ghrer.commerce.eventor.model.Item
import com.ghrer.commerce.eventor.service.config.InventoryServiceConfig
import com.ghrer.commerce.eventor.service.port.InventoryService
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.net.URI

@Service
class InventoryServiceAdaptor(
    private val inventoryServiceConfig: InventoryServiceConfig,
    inventoryWebClient: WebClient,
) : InventoryService, AbstractServiceAdaptor(
    inventoryWebClient
) {
    override val logger = KotlinLogging.logger { }
    override val serviceName = "Inventory Service"

    override fun commitItemsReservation(items: List<Item>): Mono<Void> {
        val uri = URI.create("${inventoryServiceConfig.baseUrl}${inventoryServiceConfig.commitReservedPath}")
        return postRequestToMono<Void>(uri, items)
    }

    override fun <Void> handle4xxError(response: ClientResponse): Mono<Void> {
        return response.createError()
    }
}
