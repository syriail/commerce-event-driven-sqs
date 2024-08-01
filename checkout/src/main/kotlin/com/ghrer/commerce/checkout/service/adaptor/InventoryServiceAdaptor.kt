package com.ghrer.commerce.checkout.service.adaptor

import com.ghrer.commerce.checkout.dto.PlaceOrderRequestItem
import com.ghrer.commerce.checkout.service.config.InventoryServiceConfig
import com.ghrer.commerce.checkout.service.dto.ReserveItemsResponse
import com.ghrer.commerce.checkout.service.port.InventoryService
import com.ghrer.commerce.checkout.exception.ItemNotFoundException
import com.ghrer.commerce.checkout.exception.NotEnoughQuantityAvailableException
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
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
    override fun reserveIfAvailable(items: List<PlaceOrderRequestItem>): Mono<Void> {
        val uri = URI.create("${inventoryServiceConfig.baseUrl}${inventoryServiceConfig.reservePath}")
        return postRequestToMono<ReserveItemsResponse>(uri, items).flatMap { Mono.empty() }
    }

    override fun <Void> handle4xxError(response: ClientResponse): Mono<Void> {
        return when (response.statusCode()) {
            HttpStatus.CONFLICT -> {
                response.bodyToMono<ReserveItemsResponse>().flatMap {
                    Mono.error(NotEnoughQuantityAvailableException(it.message, it.unavailableItems))
                }
            }

            HttpStatus.NOT_FOUND -> {
                response.bodyToMono<ReserveItemsResponse>().flatMap {
                    Mono.error(ItemNotFoundException(it.message, it.notFoundItems))
                }
            }
            else -> response.createError()
        }
    }
}
