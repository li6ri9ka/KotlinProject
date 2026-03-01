package org.example.app.controller

import org.example.common.dto.order.CreateOrderRequest
import org.example.common.dto.order.OrderCreatedResponse
import org.example.common.dto.order.OrderResponse
import org.example.domain.service.OrderService

class OrderController(
    private val orderService: OrderService
) {
    fun createOrder(userId: Long, request: CreateOrderRequest): OrderCreatedResponse {
        // Will be implemented in the next commit of this stage.
        throw NotImplementedError("Order creation is not implemented yet")
    }

    fun getOrders(userId: Long): List<OrderResponse> {
        // Will be implemented in the next commit of this stage.
        return emptyList()
    }

    fun cancelOrder(userId: Long, orderId: Long) {
        // Will be implemented in the next commit of this stage.
        orderService.cancelOrder(userId, orderId)
    }
}
