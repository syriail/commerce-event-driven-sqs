package com.ghrer.commerce.checkout.business

import com.ghrer.commerce.checkout.controller.dto.PlaceOrderResponse
import com.ghrer.commerce.checkout.dto.OrderStatus
import com.ghrer.commerce.checkout.exception.BadRequestException
import com.ghrer.commerce.checkout.exception.ItemNotFoundException
import com.ghrer.commerce.checkout.exception.NotEnoughQuantityAvailableException
import com.ghrer.commerce.checkout.fixtures.PlaceOrderRequestFixture
import com.ghrer.commerce.checkout.service.dto.UnavailableItem
import com.ghrer.commerce.checkout.service.port.InventoryService
import com.ghrer.commerce.checkout.service.port.OrdersService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDateTime
import java.util.UUID

class CheckoutHandlerImplTest {

    private val inventoryService = mock<InventoryService>()
    private val ordersService = mock<OrdersService>()

    private val checkoutHandler = CheckoutHandlerImpl(
        inventoryService, ordersService
    )

    @Test
    fun `should return PlaceOrderResponse when items are reserved and order is created`() {
        val request = PlaceOrderRequestFixture.getValidPlaceOrderRequest()
        val expectedResponse = PlaceOrderResponse(
            UUID.randomUUID(),
            customerId = request.customerId,
            customerAddress = request.customerAddress,
            totalPrice = request.totalPrice,
            status = OrderStatus.PLACED,
            createDate = LocalDateTime.now(),
            items = request.items
        )
        `when`(inventoryService.reserveIfAvailable(request.items)).thenAnswer {
            Mono.just(request.items)
        }

        `when`(ordersService.createOrder(request)).thenAnswer {
            Mono.just(expectedResponse)
        }

        StepVerifier.create(checkoutHandler.placeOrder(request))
            .consumeNextWith {
                Assertions.assertThat(it.id).isEqualTo(expectedResponse.id)
                Assertions.assertThat(it.items).hasSize(request.items.size)
            }
            .expectComplete()
    }

    @Test
    fun `should throw NotEnoughQuantityAvailableException when inventoryService returns it`() {
        val request = PlaceOrderRequestFixture.getValidPlaceOrderRequest()
        val expectedException = NotEnoughQuantityAvailableException(
            unavailableItems = listOf(
                UnavailableItem(
                    request.items[0].id,
                    request.items[0].quantity,
                    0
                )
            )
        )
        `when`(inventoryService.reserveIfAvailable(request.items)).thenAnswer {
            Mono.error<Void>(expectedException)
        }

        StepVerifier.create(checkoutHandler.placeOrder(request))
            .consumeErrorWith {
                it as NotEnoughQuantityAvailableException
                Assertions.assertThat(it).isNotNull()
                Assertions.assertThat(it.unavailableItems).hasSize(expectedException.unavailableItems.size)
                Assertions.assertThat(it.unavailableItems[0].availableQuantity)
                    .isEqualTo(expectedException.unavailableItems[0].availableQuantity)
            }.verify()
    }

    @Test
    fun `should throw ItemNotFoundException when inventoryService returns it`() {
        val request = PlaceOrderRequestFixture.getValidPlaceOrderRequest()
        val expectedException = ItemNotFoundException(
            notFoundItems = listOf(
                request.items[0].id,
                request.items[2].id
            )
        )
        `when`(inventoryService.reserveIfAvailable(request.items)).thenAnswer {
            Mono.error<Void>(expectedException)
        }

        StepVerifier.create(checkoutHandler.placeOrder(request))
            .consumeErrorWith {
                it as ItemNotFoundException
                Assertions.assertThat(it).isNotNull()
                Assertions.assertThat(it.notFoundItems).hasSize(expectedException.notFoundItems.size)
                Assertions.assertThat(it.notFoundItems[0]).isEqualTo(request.items[0].id)
                Assertions.assertThat(it.notFoundItems[1]).isEqualTo(request.items[2].id)
            }.verify()
    }

    @Test
    fun `should throw BadRequestException when orderService returns it`() {
        val request = PlaceOrderRequestFixture.getValidPlaceOrderRequest()
        val expectedException = BadRequestException(
            fields = mapOf(
                "myNewField" to "myNewField should not be null"
            )
        )
        `when`(inventoryService.reserveIfAvailable(request.items)).thenAnswer {
            Mono.just(request.items)
        }

        `when`(ordersService.createOrder(request)).thenAnswer {
            Mono.error<Void>(expectedException)
        }

        StepVerifier.create(checkoutHandler.placeOrder(request))
            .consumeErrorWith {
                it as BadRequestException
                Assertions.assertThat(it).isNotNull()
                Assertions.assertThat(it.fields).isNotEmpty
                Assertions.assertThat(it.fields?.get("myNewField")).isEqualTo("myNewField should not be null")
            }.verify()
    }
}
