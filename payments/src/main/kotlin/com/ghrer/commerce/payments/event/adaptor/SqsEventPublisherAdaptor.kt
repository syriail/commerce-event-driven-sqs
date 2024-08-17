package com.ghrer.commerce.payments.event.adaptor

import com.ghrer.commerce.payments.event.EventPublisher
import com.ghrer.commerce.payments.event.model.OrderEvent
import io.awspring.cloud.sqs.operations.SqsTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class SqsEventPublisherAdaptor(
    private val sqsTemplate: SqsTemplate,
    @Value("\${aws-resources.commerceEventsQueueUrl}")
    private val queueUrl: String,
) : EventPublisher {
    override fun publish(event: OrderEvent) {
        sqsTemplate.send {
            it.queue(queueUrl)
            it.header(SQS_GROUP_ID_HEADER, event.eventGroupId)
            it.payload(event)
        }
    }

    companion object {
        const val SQS_GROUP_ID_HEADER = "message-group-id"
    }
}
