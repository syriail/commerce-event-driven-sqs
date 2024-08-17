package com.ghrer.commerce.payments.business

import com.ghrer.commerce.payments.BaseIntegrationTest
import com.ghrer.commerce.payments.controller.dto.Address
import com.ghrer.commerce.payments.controller.dto.Item
import com.ghrer.commerce.payments.controller.dto.ProcessPaymentRequest
import com.ghrer.commerce.payments.event.adaptor.SqsEventPublisherAdaptor
import com.ghrer.commerce.payments.event.model.OrderEvent
import com.ghrer.commerce.payments.event.model.OrderPaymentFailedEvent
import com.ghrer.commerce.payments.event.model.OrderPaymentSuccessfulEvent
import io.awspring.cloud.sqs.operations.SqsTemplate
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import java.util.UUID

class PaymentProcessorIntegrationTest : BaseIntegrationTest() {

    @Value("\${aws-resources.commerceEventsQueueUrl}")
    private lateinit var queueUrl: String

    @Autowired
    private lateinit var sqsTemplate: SqsTemplate

    @Autowired
    private lateinit var paymentProcessor: PaymentProcessor

    @Test
    fun `should publish OrderPaymentSuccessfulEvent when customerId does not contain decline`() {
        val request = getSampleProcessPaymentRequest(false)
        paymentProcessor.processPayment(request)
        val message = sqsTemplate.receive(queueUrl, OrderEvent::class.java).get()

        Assertions.assertThat(message).isNotNull
        message.let {
            Assertions.assertThat(
                it.headers[SqsEventPublisherAdaptor.SQS_GROUP_ID_HEADER]
            ).isEqualTo(request.orderId.toString())
            Assertions.assertThat(it.payload).isInstanceOf(OrderPaymentSuccessfulEvent::class.java)
            (it.payload as? OrderPaymentSuccessfulEvent)?.let { event ->
                Assertions.assertThat(event.paymentId).isNotNull()
            }
        }
    }

    @Test
    fun `should publish OrderPaymentFailedEvent when customerId contains decline`() {
        val request = getSampleProcessPaymentRequest(true)
        paymentProcessor.processPayment(request)
        val message = sqsTemplate.receive(queueUrl, OrderEvent::class.java).get()

        Assertions.assertThat(message).isNotNull
        message.let {
            Assertions.assertThat(
                it.headers[SqsEventPublisherAdaptor.SQS_GROUP_ID_HEADER]
            ).isEqualTo(request.orderId.toString())
            Assertions.assertThat(it.payload).isInstanceOf(OrderPaymentFailedEvent::class.java)
            (it.payload as? OrderPaymentFailedEvent)?.let { event ->
                Assertions.assertThat(event.reason).isNotNull()
            }
        }
    }

    private fun getSampleProcessPaymentRequest(shouldDeclined: Boolean) = ProcessPaymentRequest(
        orderId = UUID.randomUUID(),
        customerId = if (shouldDeclined) "decline@me.please" else "accept@me.please",
        customerAddress = Address(
            firstName = "Hussein",
            lastName = "Ghrer",
            street = "am Strand Strasse",
            houseNumber = "5C",
            postCode = "69",
            city = "Neustadt am See"
        ),
        totalPrice = 83.4,
        items = listOf(
            Item(
                id = UUID.randomUUID(),
                price = 4.3,
                quantity = 4
            ),
            Item(
                id = UUID.randomUUID(),
                price = 44.5,
                quantity = 1
            )
        )
    )
}
