package com.ghrer.commerce.eventor.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.ghrer.commerce.eventor.event.model.OrderEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class MessageTransformer(
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val objectMapper: ObjectMapper,
) {
    fun transform(message: String) {
        val event = objectMapper.readValue(message, OrderEvent::class.java)
        applicationEventPublisher.publishEvent(event)
    }
}