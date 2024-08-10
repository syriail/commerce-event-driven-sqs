package com.ghrer.commerce.checkout

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableAutoConfiguration
class CheckoutApplication

fun main(args: Array<String>) {
    runApplication<CheckoutApplication>(*args)
}
