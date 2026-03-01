package org.example.data.service.impl

import org.example.domain.model.OrderStatus
import org.example.domain.repository.OrderRepository
import org.example.domain.service.StatsService
import java.math.BigDecimal

class StatsServiceImpl(
    private val orderRepository: OrderRepository
) : StatsService {
    override fun getOrdersStats(): Map<String, Any> {
        val orders = orderRepository.findAll()
        val totalOrders = orders.size.toLong()
        val canceledOrders = orders.count { it.status == OrderStatus.CANCELED }.toLong()
        val createdOrders = orders.count { it.status == OrderStatus.CREATED }.toLong()
        val totalRevenue = orders
            .filter { it.status == OrderStatus.CREATED }
            .fold(BigDecimal.ZERO) { acc, order -> acc + order.total }

        return mapOf(
            "totalOrders" to totalOrders,
            "canceledOrders" to canceledOrders,
            "createdOrders" to createdOrders,
            "totalRevenue" to totalRevenue
        )
    }
}
