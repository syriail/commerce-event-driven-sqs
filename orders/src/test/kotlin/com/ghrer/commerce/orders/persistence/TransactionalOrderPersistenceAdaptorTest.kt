package com.ghrer.commerce.orders.persistence

import com.ghrer.commerce.orders.exception.ApplicationException
import com.ghrer.commerce.orders.fixture.OrderFixture
import com.ghrer.commerce.orders.persistence.repository.OrderItemRepository
import com.ghrer.commerce.orders.persistence.repository.OrderRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.springframework.transaction.PlatformTransactionManager
import reactor.test.StepVerifier
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class TransactionalOrderPersistenceAdaptorTest(
    @Mock val orderRepository: OrderRepository,
    @Mock val orderItemRepository: OrderItemRepository,
    @Mock val transactionManager: PlatformTransactionManager,
) {

    private val orderMapper = OrderMapper()

    private val orderServiceAdaptor = TransactionalOrderPersistenceAdaptor(
        orderRepository,
        orderItemRepository,
        orderMapper,
        transactionManager
    )

    @Test
    fun `should fail creating order and throw exception when save order items fails`() {
        val request = OrderFixture.getSampleCreateOrderRequest()
        val createdOrder = orderMapper.mapToOrder(request).copy(
            createDate = LocalDateTime.now()
        )
        `when`(orderRepository.save(any())).thenReturn(createdOrder)
        val orderItemsToSave = orderMapper.mapToOrderItems(request, createdOrder.id)
        `when`(orderItemRepository.saveAll(orderItemsToSave)).thenThrow(ApplicationException(false))

        StepVerifier.create(orderServiceAdaptor.createOrder(request))
            .expectErrorMatches {
                it is ApplicationException
            }.verify()
    }
}
