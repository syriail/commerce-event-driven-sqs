package com.ghrer.commerce.inventory.persistence.repository

import com.ghrer.commerce.inventory.model.Item
import org.springframework.data.repository.ListCrudRepository
import java.util.UUID

interface ItemRepository : ListCrudRepository<Item, UUID>
