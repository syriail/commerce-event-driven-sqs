package com.ghrer.commerce.inventory.persistence

import com.ghrer.commerce.inventory.model.Item
import com.ghrer.commerce.inventory.persistence.repository.ItemRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.UUID

@Service
class ReactiveItemPersistenceServiceImpl(
    private val itemRepository: ItemRepository
) : ReactiveItemPersistenceService {

    override fun findAllByIds(ids: List<UUID>): Mono<List<Item>> {
        return Mono.fromCallable {
            itemRepository.findAllById(ids)
        }.subscribeOn(Schedulers.boundedElastic())
    }

    override fun saveAll(items: List<Item>): Flux<Item> {
        return Flux.fromIterable(
            itemRepository.saveAll(items)
        ).subscribeOn(Schedulers.boundedElastic())
    }
}
