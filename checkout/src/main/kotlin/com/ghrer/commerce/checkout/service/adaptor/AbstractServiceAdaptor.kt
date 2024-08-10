package com.ghrer.commerce.checkout.service.adaptor

import com.ghrer.commerce.checkout.exception.ApplicationException
import com.ghrer.commerce.checkout.exception.ServiceInternalErrorException
import com.ghrer.commerce.checkout.exception.ServiceUnavailableException
import com.ghrer.commerce.checkout.exception.UnknownServiceException
import mu.KLogger
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlux
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.URI

abstract class AbstractServiceAdaptor(
    val webClient: WebClient,
) {

    abstract val logger: KLogger

    abstract val serviceName: String
    protected inline fun <reified T : Any> postRequestToMono(uri: URI, request: Any): Mono<T> {
        return webClient.post()
            .uri(uri)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchangeToMono { response ->
                if (response.statusCode().is2xxSuccessful) response.bodyToMono(T::class.java)
                else if (response.statusCode().is4xxClientError) handle4xxError(response)
                else Mono.error(handle5xxError(response))
            }
    }

    protected inline fun <reified T : Any> postRequestToFlux(uri: URI, request: Any): Flux<T> {
        return webClient.post()
            .uri(uri)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchangeToFlux { response ->
                if (response.statusCode().is2xxSuccessful) response.bodyToFlux(T::class.java)
                else if (response.statusCode().is4xxClientError) handle4xxError<T>(response).flux()
                else Flux.error(handle5xxError(response))
            }
    }

    protected abstract fun <Void> handle4xxError(response: ClientResponse): Mono<Void>

    protected fun handle5xxError(response: ClientResponse): ApplicationException {
        logger.info { "Handling ${response.statusCode()} error" }
        return when (response.statusCode()) {
            HttpStatus.SERVICE_UNAVAILABLE,
            HttpStatus.GATEWAY_TIMEOUT ->
                ServiceUnavailableException("Service is down: $serviceName")
            HttpStatus.INTERNAL_SERVER_ERROR ->
                ServiceInternalErrorException("Server Internal Error: $serviceName")
            else -> UnknownServiceException("Unexpected response ${response.statusCode()} from $serviceName")
        }
    }
}
