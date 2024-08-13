package com.ghrer.commerce.checkout.controller

import com.ghrer.commerce.checkout.business.CheckoutHandler
import com.ghrer.commerce.checkout.controller.dto.PlaceOrderRequest
import com.ghrer.commerce.checkout.controller.dto.PlaceOrderResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/checkout")
@Validated
class CheckoutController(
    private val checkoutHandler: CheckoutHandler
) {

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/order")
    fun placeOrder(
        @RequestBody @Valid placeOrderRequest: PlaceOrderRequest
    ): Mono<PlaceOrderResponse> {
        return checkoutHandler.placeOrder(placeOrderRequest)
    }
}
