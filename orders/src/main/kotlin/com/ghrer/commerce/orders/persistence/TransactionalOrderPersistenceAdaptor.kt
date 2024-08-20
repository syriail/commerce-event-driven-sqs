package com.ghrer.commerce.orders.persistence

import com.ghrer.commerce.orders.dto.CreateOrderRequest
import com.ghrer.commerce.orders.exception.ApplicationException
import com.ghrer.commerce.orders.model.Order
import com.ghrer.commerce.orders.model.OrderAggregate
import com.ghrer.commerce.orders.model.OrderStatus
import com.ghrer.commerce.orders.persistence.repository.OrderItemRepository
import com.ghrer.commerce.orders.persistence.repository.OrderRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Service
class TransactionalOrderPersistenceAdaptor(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val orderMapper: OrderMapper,
    transactionManager: PlatformTransactionManager,
) : OrderPersistenceService {

    private val transactionTemplate = TransactionTemplate(transactionManager)
    override fun createOrder(createOrderRequest: CreateOrderRequest): Mono<OrderAggregate> {

        return Mono.fromCallable {
            runCatching {
                transactionTemplate.execute {
                    val createdOrder = orderRepository.save(
                        orderMapper.mapToOrder(createOrderRequest)
                    )
                    val createdItems = orderItemRepository.saveAll(
                        orderMapper.mapToOrderItems(createOrderRequest, createdOrder.id)
                    )
                    orderMapper.mapToOrderAggregate(createdOrder, createdItems)
                }!!
            }.getOrElse {
                throw ApplicationException(false, message = it.message, cause = it)
            }
        }.subscribeOn(Schedulers.boundedElastic())
    }

    override fun updateOrderPaymentStatus(id: UUID, status: OrderStatus, paymentId: String?): Mono<Order> {
        return Mono.fromCallable {
            val order = orderRepository.findById(id).getOrNull()
                ?: throw ApplicationException(false)

            orderRepository.save(
                order.copy(status = status, paymentId = paymentId)
            )
        }.subscribeOn(Schedulers.boundedElastic())
    }
}
