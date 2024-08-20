package com.ghrer.commerce.eventor.agent.service.port

import com.ghrer.commerce.eventor.model.OrderStatus
import reactor.core.publisher.Mono
import java.util.UUID

interface OrderService {

    fun updatePaymentStatus(request: UpdateOrderPaymentStatusRequest): Mono<Void>
}

data class UpdateOrderPaymentStatusRequest(
    val orderId: UUID,
    val status: OrderStatus,
    val paymentId: String? = null
)
