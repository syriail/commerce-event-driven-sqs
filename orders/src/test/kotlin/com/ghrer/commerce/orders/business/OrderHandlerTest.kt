package com.ghrer.commerce.orders.business

import com.ghrer.commerce.orders.event.EventPublisher
import com.ghrer.commerce.orders.event.model.OrderCreatedEvent
import com.ghrer.commerce.orders.event.model.OrderEvent
import com.ghrer.commerce.orders.exception.ApplicationException
import com.ghrer.commerce.orders.fixture.OrderFixture
import com.ghrer.commerce.orders.persistence.OrderPersistenceService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.capture
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExtendWith(MockitoExtension::class)
class OrderHandlerTest(
    @Mock val orderPersistenceService: OrderPersistenceService,
    @Mock val eventPublisher: EventPublisher
) {

    @Captor
    private lateinit var eventCaptor: ArgumentCaptor<OrderEvent>

    @InjectMocks
    private lateinit var orderHandler: OrderHandler

    @Test
    fun `should fail creating order and not publishing the event when it could not be persisted`() {
        val request = OrderFixture.getSampleCreateOrderRequest()

        `when`(orderPersistenceService.createOrder(request)).thenAnswer {
            Mono.error<ApplicationException>(ApplicationException(false))
        }

        StepVerifier.create(orderHandler.createOrder(request))
            .consumeErrorWith {
                Assertions.assertThat(it).isInstanceOf(ApplicationException::class.java)
                verifyNoInteractions(eventPublisher)
            }.verify()
    }

    @Test
    fun `should create order successfully and publish OrderCreatedEvent`() {
        val request = OrderFixture.getSampleCreateOrderRequest()
        val createdOrder = OrderFixture.getCreatedOrderAggregate(request)

        `when`(orderPersistenceService.createOrder(request)).thenAnswer {
            Mono.just(createdOrder)
        }

        StepVerifier.create(orderHandler.createOrder(request))
            .consumeNextWith {
                Assertions.assertThat(it.id).isEqualTo(createdOrder.id)
            }.verifyComplete()

        verify(eventPublisher).publish(capture(eventCaptor))
        eventCaptor.value.also {
            Assertions.assertThat(it.eventGroupId).isEqualTo(createdOrder.id.toString())
            Assertions.assertThat(it).isInstanceOf(OrderCreatedEvent::class.java)
        }
    }
}
