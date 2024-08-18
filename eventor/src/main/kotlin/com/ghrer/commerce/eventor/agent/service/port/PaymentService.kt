package com.ghrer.commerce.eventor.agent.service.port

import com.ghrer.commerce.eventor.model.Address
import com.ghrer.commerce.eventor.model.Item
import reactor.core.publisher.Mono
import java.util.UUID

interface PaymentService {
    fun processPayment(request: ProcessPaymentRequest): Mono<Void>
}

data class ProcessPaymentRequest(
    val orderId: UUID,
    val customerId: String,
    val customerAddress: Address,
    val totalPrice: Double,
    val items: List<Item>
)
