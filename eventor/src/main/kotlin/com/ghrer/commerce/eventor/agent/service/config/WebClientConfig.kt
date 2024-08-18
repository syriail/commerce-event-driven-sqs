package com.ghrer.commerce.eventor.agent.service.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {

    @Bean
    fun webClient(): WebClient {
        return WebClient.builder().build()
    }

    @Bean(name = ["orderServiceWebClient"])
    @Primary
    fun orderServiceWebClient(
        @Value("\${orders-service.baseUrl}")
        baseUrl: String
    ): WebClient {
        return WebClient.builder()
            .baseUrl(baseUrl)
            .build()
    }
}
