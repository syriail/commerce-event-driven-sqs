package com.ghrer.commerce.eventor.service.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "inventory-service")
@Configuration
data class InventoryServiceConfig(
    var baseUrl: String = "",
    var commitReservedPath: String = ""
)
