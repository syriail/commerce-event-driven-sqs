package com.ghrer.commerce.payments.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.awspring.cloud.sqs.operations.SqsTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.sqs.SqsAsyncClient

@Configuration
class AwsConfig {
    @Bean
    fun sqsTemplate(sqsAsyncClient: SqsAsyncClient, objectMapper: ObjectMapper) = SqsTemplate
        .builder()
        .sqsAsyncClient(sqsAsyncClient)
        .configureDefaultConverter { converter ->
            converter.setObjectMapper(objectMapper)
            converter.setPayloadTypeHeaderValueFunction {
                null
            }
        }.build()
}
