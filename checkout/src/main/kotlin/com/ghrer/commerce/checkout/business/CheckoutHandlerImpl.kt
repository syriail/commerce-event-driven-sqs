package com.ghrer.commerce.checkout.business

import com.ghrer.commerce.checkout.controller.dto.PlaceOrderRequest
import com.ghrer.commerce.checkout.controller.dto.PlaceOrderResponse
import com.ghrer.commerce.checkout.service.port.InventoryService
import com.ghrer.commerce.checkout.service.port.OrdersService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CheckoutHandlerImpl(
    private val inventoryService: InventoryService,
    private val ordersService: OrdersService
) : CheckoutHandler {
    override fun placeOrder(placeOrderRequest: PlaceOrderRequest): Mono<PlaceOrderResponse> {

        return inventoryService.reserveIfAvailable(placeOrderRequest.items)
            .flatMap {
                ordersService.createOrder(placeOrderRequest)
            }.map {
                PlaceOrderResponse(
                    id = it.id,
                    customerId = it.customerId,
                    customerAddress = it.customerAddress,
                    totalPrice = it.totalPrice,
                    items = it.items,
                    status = it.status,
                    createDate = it.createDate
                )
            }
    }
}
