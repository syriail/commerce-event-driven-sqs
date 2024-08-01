package com.ghrer.commerce.orders.event.adaptor

import com.ghrer.commerce.orders.event.EventPublisher
import com.ghrer.commerce.orders.event.model.OrderEvent
import io.awspring.cloud.sqs.operations.SqsTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class SqsEventPublisherAdaptor(
    private val sqsTemplate: SqsTemplate,
    @Value("\${spring.cloud.aws.commerceEventsQueueUrl}")
    private val queueUrl: String,
) : EventPublisher {
    override fun publish(event: OrderEvent) {
        sqsTemplate.sendAsync {
            it.queue(queueUrl)
            it.headers(
                mapOf(
                    SQS_GROUP_ID_HEADER to event.eventGroupId
                )
            )
            it.payload(event)
        }
    }

    companion object {
        const val SQS_GROUP_ID_HEADER = "message-group-id"
    }
}
