package org.example.domain.repository

import org.example.domain.model.Order

interface OrderRepository {
    fun create(order: Order): Order
    fun findById(orderId: Long): Order?
    fun findByUser(userId: Long): List<Order>
    fun update(order: Order): Order
}
