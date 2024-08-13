package com.ghrer.commerce.checkout.service.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "order-service")
data class OrderServiceConfig(
    var baseUrl: String = "",
    var createOrderPath: String = ""
)
