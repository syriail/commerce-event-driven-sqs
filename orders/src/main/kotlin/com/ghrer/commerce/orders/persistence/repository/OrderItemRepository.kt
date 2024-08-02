package com.ghrer.commerce.orders.persistence.repository

import com.ghrer.commerce.orders.model.OrderItem
import com.ghrer.commerce.orders.model.OrderItemId
import org.springframework.data.repository.ListCrudRepository
import java.util.UUID

interface OrderItemRepository : ListCrudRepository<OrderItem, OrderItemId> {

    fun findByOrderItemIdOrderId(orderId: UUID): List<OrderItem>
}
