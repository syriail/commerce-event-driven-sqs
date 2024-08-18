package com.ghrer.commerce.eventor.agent

import com.ghrer.commerce.eventor.agent.service.port.OrderService
import com.ghrer.commerce.eventor.agent.service.port.UpdateOrderStatusRequest
import com.ghrer.commerce.eventor.event.model.OrderPaymentFailedEvent
import com.ghrer.commerce.eventor.event.model.OrderPaymentSuccessfulEvent
import com.ghrer.commerce.eventor.model.OrderStatus
import mu.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class OrderServiceAgent(
    private val orderService: OrderService
) {

    private val logger = KotlinLogging.logger { }

    @EventListener
    fun on(paymentSuccessfulEvent: OrderPaymentSuccessfulEvent) {
        runCatching {
            orderService.updateStatus(
                UpdateOrderStatusRequest(
                    paymentSuccessfulEvent.orderId,
                    OrderStatus.PAID
                )
            ).block()
        }.onFailure {
            logger.error(it) { "Failed to update order ${paymentSuccessfulEvent.orderId} with status ${OrderStatus.PAID}" }
        }
    }

    @EventListener
    fun on(paymentFailedEvent: OrderPaymentFailedEvent) {
        runCatching {
            orderService.updateStatus(
                UpdateOrderStatusRequest(
                    paymentFailedEvent.orderId,
                    OrderStatus.PAYMENT_FAILED
                )
            ).block()
        }.onFailure {
            logger.error(it) { "Failed to update order ${paymentFailedEvent.orderId} with status ${OrderStatus.PAID}" }
        }
    }
}
