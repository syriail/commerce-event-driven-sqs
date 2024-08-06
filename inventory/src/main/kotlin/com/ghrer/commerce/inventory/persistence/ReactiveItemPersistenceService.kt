package com.ghrer.commerce.inventory.persistence

import com.ghrer.commerce.inventory.model.Item
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

interface ReactiveItemPersistenceService {
    fun findAllByIds(ids: List<UUID>): Mono<List<Item>>
    fun saveAll(items: List<Item>): Flux<Item>
}
