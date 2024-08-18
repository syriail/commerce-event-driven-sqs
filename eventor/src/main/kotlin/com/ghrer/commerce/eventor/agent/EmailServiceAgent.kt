package com.ghrer.commerce.eventor.agent

import com.ghrer.commerce.eventor.event.model.OrderPaymentFailedEvent
import com.ghrer.commerce.eventor.event.model.OrderPaymentSuccessfulEvent
import mu.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class EmailServiceAgent {

    private val logger = KotlinLogging.logger { }

    @EventListener
    fun on(paymentSuccessfulEvent: OrderPaymentSuccessfulEvent) {
        logger.info { "Sending confirmation email to customer for order: ${paymentSuccessfulEvent.orderId}" }
    }

    @EventListener
    fun on(paymentFailedEvent: OrderPaymentFailedEvent) {
        logger.error {
            "Sending email to customer informing them that the payment for " +
                "order ${paymentFailedEvent.orderId} has been fail. Reason: ${paymentFailedEvent.reason}"
        }
    }
}
