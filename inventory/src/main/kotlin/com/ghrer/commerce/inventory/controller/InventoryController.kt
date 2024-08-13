package com.ghrer.commerce.inventory.controller

import com.ghrer.commerce.inventory.business.InventoryHandler
import com.ghrer.commerce.inventory.controller.dto.ReserveItemRequest
import com.ghrer.commerce.inventory.model.Item
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class InventoryController(
    private val inventoryHandler: InventoryHandler
) {

    @PostMapping("/reserve")
    @ResponseStatus(HttpStatus.OK)
    fun reserve(
        @RequestBody items: List<ReserveItemRequest>
    ): Mono<List<Item>> {
        return inventoryHandler.reserve(items)
    }
}
