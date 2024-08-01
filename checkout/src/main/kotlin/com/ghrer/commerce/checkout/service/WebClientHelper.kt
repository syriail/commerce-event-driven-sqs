package com.ghrer.commerce.checkout.service

import com.ghrer.commerce.checkout.exception.ServiceInternalErrorException
import com.ghrer.commerce.checkout.exception.ServiceUnavailableException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.web.reactive.function.client.ClientResponse
import reactor.core.publisher.Mono

class WebClientHelper {
    companion object {
        fun handle5xxError(response: ClientResponse, serviceName: String): Mono<Void> {
            return when (response.statusCode()) {
                HttpStatusCode.valueOf(HttpStatus.SERVICE_UNAVAILABLE.value()),
                HttpStatusCode.valueOf(HttpStatus.GATEWAY_TIMEOUT.value()) ->
                    Mono.error(ServiceUnavailableException("Service is down: $serviceName"))
                HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()) ->
                    Mono.error(ServiceInternalErrorException("Server Internal Error: $serviceName"))
                else -> response.createError()
            }
        }
    }
}
