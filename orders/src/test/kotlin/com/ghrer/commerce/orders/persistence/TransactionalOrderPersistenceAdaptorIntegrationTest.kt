package com.ghrer.commerce.orders.persistence

import com.ghrer.commerce.orders.BaseIntegrationTest
import com.ghrer.commerce.orders.exception.ApplicationException
import com.ghrer.commerce.orders.fixture.OrderFixture
import com.ghrer.commerce.orders.persistence.repository.OrderItemRepository
import com.ghrer.commerce.orders.persistence.repository.OrderRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.anyList
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.dao.DataIntegrityViolationException
import reactor.test.StepVerifier

class TransactionalOrderPersistenceAdaptorIntegrationTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var transactionalOrderPersistenceAdaptor: TransactionalOrderPersistenceAdaptor

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @SpyBean
    lateinit var orderItemRepository: OrderItemRepository

    @BeforeEach
    fun cleanup() {
        orderItemRepository.deleteAll()
        orderRepository.deleteAll()
    }

    @Test
    fun `should fail to create order and rollback when saving order items fails`() {
        val request = OrderFixture.getSampleCreateOrderRequest()
        `when`(orderItemRepository.saveAll(anyList())).thenThrow(
            DataIntegrityViolationException("could not execute statement")
        )
        StepVerifier.create(transactionalOrderPersistenceAdaptor.createOrder(request))
            .consumeErrorWith {
                (it as? ApplicationException).let { e ->
                    Assertions.assertThat(e?.cause).isInstanceOf(DataIntegrityViolationException::class.java)
                    Assertions.assertThat(e?.message).contains("could not execute statement")
                }
                Assertions.assertThat(orderRepository.findAll()).isEmpty()
                Assertions.assertThat(orderItemRepository.findAll()).isEmpty()
            }.verify()
    }

    @Test
    fun `should create order along with order items`() {
        val request = OrderFixture.getSampleCreateOrderRequest()

        StepVerifier.create(transactionalOrderPersistenceAdaptor.createOrder(request))
            .consumeNextWith {
                Assertions.assertThat(it).isNotNull()
                it?.also {
                    val orderItems = orderItemRepository.findByOrderItemIdOrderId(it.id)
                    Assertions.assertThat(orderItems.size).isEqualTo(request.items.size)
                }
            }.verifyComplete()
    }
}
