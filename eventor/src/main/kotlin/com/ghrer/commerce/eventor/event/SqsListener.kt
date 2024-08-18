package com.ghrer.commerce.eventor.event

import io.awspring.cloud.sqs.annotation.SqsListener
import org.springframework.stereotype.Component

@Component
class SqsListener(
    private val messageTransformer: MessageTransformer,
) {

    @SqsListener("\${aws-resources.commerceEventsQueueUrl}")
    fun listen(message: String) {
        messageTransformer.transform(message)
    }
}
