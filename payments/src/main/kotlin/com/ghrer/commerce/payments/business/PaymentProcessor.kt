package com.ghrer.commerce.payments.business

import com.ghrer.commerce.payments.controller.dto.ProcessPaymentRequest
import com.ghrer.commerce.payments.event.EventPublisher
import com.ghrer.commerce.payments.event.model.OrderPaymentFailedEvent
import com.ghrer.commerce.payments.event.model.OrderPaymentSuccessfulEvent
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class PaymentProcessor(
    private val eventPublisher: EventPublisher
) {

    fun processPayment(processPaymentRequest: ProcessPaymentRequest) {
        if (processPaymentRequest.customerId.contains("decline", ignoreCase = true)) {
            eventPublisher.publish(
                OrderPaymentFailedEvent(
                    orderId = processPaymentRequest.orderId,
                    reason = "Card decline"
                )
            )
        } else {
            eventPublisher.publish(
                OrderPaymentSuccessfulEvent(
                    orderId = processPaymentRequest.orderId,
                    paymentId = UUID.randomUUID()
                )
            )
        }
    }
}
