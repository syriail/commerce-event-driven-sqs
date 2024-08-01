package com.ghrer.commerce.orders.persistence.repository

import com.ghrer.commerce.orders.model.OrderItem
import org.springframework.data.repository.ListCrudRepository
import java.util.UUID

interface OrderItemRepository : ListCrudRepository<OrderItem, UUID> {

    fun findAllByOrderId(orderId: UUID): List<OrderItem>
}
