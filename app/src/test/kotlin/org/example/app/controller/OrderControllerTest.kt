package org.example.app.controller

import org.example.common.dto.order.CreateOrderItemRequest
import org.example.common.dto.order.CreateOrderRequest
import org.example.data.cache.NoOpCacheFacade
import org.example.domain.model.Order
import org.example.domain.model.OrderItem
import org.example.domain.model.OrderStatus
import org.example.domain.service.OrderService
import kotlin.test.Test
import kotlin.test.assertEquals
import java.math.BigDecimal
import java.time.Instant

class OrderControllerTest {
    @Test
    fun `createOrder returns created order id`() {
        val service = object : OrderService {
            override fun createOrder(userId: Long, items: List<OrderItem>): Order = Order(
                id = 42,
                userId = userId,
                items = items,
                total = BigDecimal("50.00"),
                status = OrderStatus.CREATED,
                createdAt = Instant.parse("2026-03-01T10:00:00Z")
            )

            override fun getOrders(userId: Long): List<Order> = emptyList()
            override fun cancelOrder(userId: Long, orderId: Long) = Unit
        }

        val controller = OrderController(
            orderService = service,
            cacheFacade = NoOpCacheFacade,
            cacheTtlSeconds = 60
        )
        val response = controller.createOrder(
            userId = 10,
            request = CreateOrderRequest(items = listOf(CreateOrderItemRequest(productId = 1, quantity = 2)))
        )

        assertEquals(42, response.orderId)
    }
}
