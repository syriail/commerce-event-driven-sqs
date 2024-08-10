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
//        val reserveResponse = inventoryService.reserveIfAvailable(placeOrderRequest.items)
//        val createOrderResponse = ordersService.createOrder(placeOrderRequest)
//        return Mono.zip(reserveResponse, createOrderResponse)
//            .map {
//                PlaceOrderResponse(
//                    id = it.t2.id,
//                    customerId = it.t2.customerId,
//                    address = it.t2.address,
//                    totalPrice = it.t2.totalPrice,
//                    items = it.t2.items,
//                    status = it.t2.status,
//                    createDate = it.t2.createDate
//                )
//            }
        return inventoryService.reserveIfAvailable(placeOrderRequest.items)
            .flatMap {
                ordersService.createOrder(placeOrderRequest)
            }.map {
                PlaceOrderResponse(
                    id = it.id,
                    customerId = it.customerId,
                    address = it.address,
                    totalPrice = it.totalPrice,
                    items = it.items,
                    status = it.status,
                    createDate = it.createDate
                )
            }
    }
}
