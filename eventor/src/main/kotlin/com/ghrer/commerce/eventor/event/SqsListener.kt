package com.ghrer.commerce.eventor.event

import com.ghrer.commerce.eventor.event.model.OrderEvent
import io.awspring.cloud.sqs.annotation.SqsListener
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.MessageHeaders

class MessageConsumer {
    @SqsListener("\${spring.cloud.aws.commerceEventsQueueUrl}")
    fun listen(event: OrderEvent) {

    }
}