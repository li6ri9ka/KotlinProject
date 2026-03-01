package org.example.app.controller

import org.example.common.dto.order.CreateOrderRequest
import org.example.common.dto.order.OrderCreatedResponse
import org.example.common.dto.order.OrderItemResponse
import org.example.common.dto.order.OrderResponse
import org.example.domain.model.OrderItem
import org.example.domain.service.OrderService

class OrderController(
    private val orderService: OrderService
) {
    fun createOrder(userId: Long, request: CreateOrderRequest): OrderCreatedResponse {
        val created = orderService.createOrder(
            userId = userId,
            items = request.items.map { OrderItem(it.productId, it.quantity, java.math.BigDecimal.ZERO) }
        )
        return OrderCreatedResponse(created.id)
    }

    fun getOrders(userId: Long): List<OrderResponse> =
        orderService.getOrders(userId).map { order ->
            OrderResponse(
                id = order.id,
                status = order.status.name,
                total = order.total.toPlainString(),
                createdAt = order.createdAt.toString(),
                items = order.items.map { item ->
                    OrderItemResponse(
                        productId = item.productId,
                        quantity = item.quantity,
                        unitPrice = item.unitPrice.toPlainString()
                    )
                }
            )
        }

    fun cancelOrder(userId: Long, orderId: Long) {
        orderService.cancelOrder(userId, orderId)
    }
}
