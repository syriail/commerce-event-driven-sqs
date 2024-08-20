package com.ghrer.commerce.eventor.agent.service.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "orders-service")
@Configuration
data class OrderServiceConfig(
    var baseUrl: String = "",
    var paymentStatusPath: String = ""
)
