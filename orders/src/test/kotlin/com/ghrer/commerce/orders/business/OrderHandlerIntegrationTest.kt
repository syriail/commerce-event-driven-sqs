package com.ghrer.commerce.orders.business

import com.ghrer.commerce.orders.BaseIntegrationTest
import com.ghrer.commerce.orders.event.adaptor.SqsEventPublisherAdaptor
import com.ghrer.commerce.orders.event.model.OrderCreatedEvent
import com.ghrer.commerce.orders.fixture.OrderFixture
import io.awspring.cloud.sqs.operations.SqsTemplate
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import reactor.test.StepVerifier

class OrderHandlerIntegrationTest : BaseIntegrationTest() {

    @Value("\${aws-resources.commerceEventsQueueUrl}")
    private lateinit var queueUrl: String

    @Autowired
    private lateinit var sqsTemplate: SqsTemplate

    @Autowired
    private lateinit var orderHandler: OrderHandler

    @Test
    fun `should create an order and publish OrderCreatedEvent`() {
        StepVerifier.create(
            orderHandler.createOrder(
                OrderFixture.getSampleCreateOrderRequest()
            )
        ).consumeNextWith { createdOrder ->
            val messages = sqsTemplate.receiveMany(queueUrl, OrderCreatedEvent::class.java)

            Assertions.assertThat(messages.size).isGreaterThan(0)
            val message = messages.find { it.payload.order.id == createdOrder?.id }
            Assertions.assertThat(message).isNotNull
            message?.let {
                println(it.headers)
                Assertions.assertThat(
                    it.headers[SqsEventPublisherAdaptor.SQS_GROUP_ID_HEADER]
                ).isEqualTo(createdOrder?.id.toString())
            }
        }.verifyComplete()
    }
}
