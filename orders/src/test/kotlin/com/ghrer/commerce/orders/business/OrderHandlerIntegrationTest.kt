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
import kotlin.jvm.optionals.getOrNull

class OrderHandlerIntegrationTest : BaseIntegrationTest() {

    @Value("\${spring.cloud.aws.commerceEventsQueueUrl}")
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
            val message = sqsTemplate.receive(queueUrl, OrderCreatedEvent::class.java).getOrNull()

            Assertions.assertThat(message).isNotNull
            message?.let {
                Assertions.assertThat(it.payload.order.id).isEqualTo(createdOrder?.id)
                Assertions.assertThat(
                    it.headers[SqsEventPublisherAdaptor.SQS_GROUP_ID_HEADER]
                ).isEqualTo(createdOrder?.id.toString())
            }
        }.verifyComplete()
    }
}
