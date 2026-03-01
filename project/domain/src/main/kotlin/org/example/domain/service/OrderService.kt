package org.example.domain.service

import org.example.domain.model.Order
import org.example.domain.model.OrderItem

interface OrderService {
    fun createOrder(userId: Long, items: List<OrderItem>): Order
    fun getOrders(userId: Long): List<Order>
    fun cancelOrder(userId: Long, orderId: Long)
}
