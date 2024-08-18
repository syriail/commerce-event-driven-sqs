package com.ghrer.commerce.eventor.agent.service.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "payments-service")
@Configuration
data class PaymentServiceConfig(
    var baseUrl: String = "",
    var processPaymentPath: String = "",
)
