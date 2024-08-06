package com.ghrer.commerce.inventory.persistence

import com.ghrer.commerce.inventory.persistence.repository.ItemRepository
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("test")
class DataPersistenceService(
    private val itemRepository: ItemRepository
) {
    fun deleteAllItems() = itemRepository.deleteAll()
}
