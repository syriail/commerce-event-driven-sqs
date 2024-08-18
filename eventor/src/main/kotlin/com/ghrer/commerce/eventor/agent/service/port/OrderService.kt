package com.ghrer.commerce.eventor.agent.service.port

import com.ghrer.commerce.eventor.model.OrderStatus
import reactor.core.publisher.Mono
import java.util.UUID

interface OrderService {

    fun updateStatus(request: UpdateOrderStatusRequest): Mono<Void>
}

data class UpdateOrderStatusRequest(
    val orderId: UUID,
    val status: OrderStatus
)
