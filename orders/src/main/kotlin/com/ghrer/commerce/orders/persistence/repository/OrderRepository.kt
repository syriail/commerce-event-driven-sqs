package com.ghrer.commerce.orders.persistence.repository

import com.ghrer.commerce.orders.model.Order
import org.springframework.data.repository.ListCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface OrderRepository : ListCrudRepository<Order, UUID> {
    fun findByCustomerId(customerId: String): List<Order>
}
