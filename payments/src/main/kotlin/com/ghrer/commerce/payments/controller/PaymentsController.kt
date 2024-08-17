package com.ghrer.commerce.payments.controller

import com.ghrer.commerce.payments.business.PaymentProcessor
import com.ghrer.commerce.payments.controller.dto.ProcessPaymentRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
class PaymentsController(
    private val paymentProcessor: PaymentProcessor
) {

    @PostMapping("/process-payment")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun processPayment(
        @RequestBody @Valid processPaymentRequest: ProcessPaymentRequest
    ) {
        paymentProcessor.processPayment(processPaymentRequest)
    }
}
