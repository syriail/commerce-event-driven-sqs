package com.ghrer.commerce.eventor.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.ghrer.commerce.eventor.event.model.OrderEvent
import mu.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class MessageTransformer(
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val objectMapper: ObjectMapper,
) {
    val logger = KotlinLogging.logger { }
    fun transform(message: String) {
        runCatching {
            val event = objectMapper.readValue(message, OrderEvent::class.java)
            applicationEventPublisher.publishEvent(event)
        }.onFailure {
            logger.error(it) { "It seems that the event is not deserializable. $message" }
        }
    }
}
